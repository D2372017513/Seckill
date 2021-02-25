package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SecKillDao;
import org.seckill.entity.SecKill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * OK
 * SecKill{secKillId=1002, name='36元秒杀大米',
 * number=9994, startTime=Sun Jan 17 16:00:00 CST 2021,
 * endTime=Wed Feb 17 16:00:00 CST 2021,
 * createTime=Mon Jan 18 00:50:38 CST 2021}
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class RedisDaoTest {
    long id = 1002;

    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SecKillDao secKillDao;

    @Test
    public void testRedisDao() throws Exception{
        SecKill secKill = redisDao.getSeckill(id);
        if (secKill == null){
            secKill = secKillDao.queryById(id);
            if (secKill != null){
                String result = redisDao.putSeckill(secKill);
                System.out.println(result);
                secKill = redisDao.getSeckill(id);
                System.out.println(secKill);
            }
        }else {
            System.out.println(secKill);
        }
    }

}

