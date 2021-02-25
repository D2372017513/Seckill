package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SecKill;

import java.util.Date;
import java.util.List;
import java.util.Map;

// 一般来说接口是为了为相应的实体进行操作的
public interface SecKillDao {
    /**
     * 减少库存
     * @param secKillId 秒杀商品ID
     * @param killTime  秒杀时间
     * @return
     */
    int reduceNumber(@Param("secKillId") long secKillId,@Param("killTime") Date killTime);

    /**
     * 根据 ID 查询秒杀对象
     * @param secKillId
     * @return
     */
    SecKill queryById(@Param("secKillId") long secKillId);

    /**
     * 根据偏移量查询秒杀列表
     * @param offset
     * @param limit
     * @return
     */
    List<SecKill> queryAll(@Param("offset") int offset,@Param("limit") int limit);

    /**
     * 使用存储过程进行秒杀
     * @param paramMap
     */
    void killByProcedure(Map<String ,Object> paramMap);
}
