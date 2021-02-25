package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<SecKill> list = seckillService.getSeckillList();
        logger.info("list = {}", list);
    }

    @Test
    public void getById() {
        long id = 1000;
        SecKill secKill = seckillService.getById(id);
        logger.info("seckill = {}", secKill);
    }

    @Test
    /**
     * Exposer{exposed=true,
     * md5='81c003e266701ca48287cd4c588fcac8',
     * seckillId=1002,
     * now=0,
     * start=0,
     * end=0}
     */
    public void exportSeckillUrl() {
        long id = 1003;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            long phone = 13228217104L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("result={}", execution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }
        } else {
            // 秒杀未开启
            logger.warn("exposer = {}", exposer);
        }


    }

    /**
     * 输出应为 stateInfo 对应字段，并且当秒杀成功后数据库应有相应变化
     */
    @Test
    public void executeSeckillProcedure() {
        long seckillId = 1001;
        long phone = 13228217109l;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }
    }

//    @Test
    /**
     * result=SeckillExecution{
     * seckillId=1002,
     * state=1,
     * stateInfo='秒杀成功',
     * successKilled=SuccessKilled{secKillId=1002,
     * userPhone=13228217104,
     * state=-1,
     * createTime=Tue Jan 26 23:16:53 CST 2021}}
     */
//    public void executeSeckill() {
//
//    }
}