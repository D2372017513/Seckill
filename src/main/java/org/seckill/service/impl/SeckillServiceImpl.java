package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SecKillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {
    // MD5 盐值字符串，用来混淆 MD5
    private final String slat = "njxdchviangaidjvoamv#@%#%￥%%&%#%2387453";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    // 进行 Spring 注入
    @Autowired
    private SecKillDao secKillDao;
    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Override
    public List<SecKill> getSeckillList() {
        return secKillDao.queryAll(0, 10);
    }

    @Override
    public SecKill getById(long seckillId) {
        return secKillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        // 优化点：缓存优化,一致性维护基于超时
        // 1：访问redis
        SecKill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            // 访问数据库
            seckill = secKillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                // 添加进redis
                String result = redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 进行md5加密,不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    /**
     * 使用存储过程完成秒杀
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            // 当md5验证失败时，返回数据篡改异常
            return new SeckillExecution(seckillId, SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        // 存储过程执行完毕，result被赋值
        try {
            secKillDao.killByProcedure(map);
            // 获取result
            Integer result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWithSecKill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);

        }

    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法 的优点：
     * 1：开发团队达成一致的约定，明确标注事务方法的编程风格
     * 2：保证事务方法的执行时间尽可能的短，不要穿插其他的网络操作或者剥离到事务方法外
     * 3：不是所有的方法都需要事务，如果只有一条修改操作，或者只有只读操作不需要事务控制
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        // 执行秒杀业务逻辑：减库存 + 记录购买行为
        Date nowTime = new Date();
        try {
            // 记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            // 唯一：seckillId , userPhone
            if (insertCount <= 0) {
                // 重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                // 热点商品竞争
                int updateCount = secKillDao.reduceNumber(seckillId, nowTime);
                if (updateCount < 0) {
                    // 没有更新到记录,秒杀结束
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    // 秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有编译器异常转化为运行期异常
            // 当 Spring 检测到运行期异常时，Spring 的声明式事务会自动的进行 rollback 回滚操作
            throw new SeckillException("seckill inner error " + e.getMessage());
        }
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
