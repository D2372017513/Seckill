package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在使用者的角度设计接口
 * 如何实现站在使用者角度进行开发：
 * 1、方法定义粒度：也就是具体到每一个功能
 * 2、参数：简单明确的传递
 * 3、返回类型：类型友好，简单（return/异常）
 */

public interface SeckillService {
    /**
     * 查询所有的秒杀库存
     *
     * @return
     */
    List<SecKill> getSeckillList();

    /**
     * 查询单个的秒杀商品
     *
     * @param seckillId
     * @return
     */
    SecKill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口，
     * 否则暑促系统时间和秒杀时间
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 秒杀执行操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException, RepeatKillException, SeckillCloseException;

    /**
     * 执行秒杀操作 by 存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);

}
