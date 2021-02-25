package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;
    @Test
    public void insertSuccessKilled() {
        successKilledDao.insertSuccessKilled(1000,13228217105L);
    }

    @Test
    public void queryByIdWithSecKill() {
        SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(1000,13228217105L);
        System.out.println(successKilled);
        System.out.println(successKilled.getSecKill());
    }
    /**
     * SuccessKilled{secKillId=1000,
     * userPhone=13228217105,
     * state=-1,
     * createTime=Mon Jan 25 19:23:26 CST 2021}
     *
     * SecKill{secKillId=1000,
     * name='1000元秒杀IPhone 12',
     * number=100,
     * startTime=Mon
     * Jan 18 08:00:00 CST 2021,
     * endTime=Tue Jan 19 08:00:00 CST 2021,
     * createTime=Mon Jan 18 00:50:38 CST 2021}
     */
}