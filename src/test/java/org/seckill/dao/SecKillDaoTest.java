package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SecKill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * 配置 Spring 和 JUnit 整合，JUnit 启动时加载 SpringIOC 容器
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SecKillDaoTest {

    @Resource
    private SecKillDao secKillDao;

    @Test
    public void queryById() {
        long id = 1000;
        SecKill secKill = secKillDao.queryById(id);
        System.out.println(secKill.getName());
        System.out.println(secKill);
    }

    @Test
    public void reduceNumber() {
    }


    @Test
    public void queryAll() {
        System.out.println(secKillDao.queryAll(0,2));
    }
}