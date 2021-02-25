package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.SecKill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
    // 新建Redis连接池，相当于JDBC的connection
    private final JedisPool jedisPool;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private RuntimeSchema<SecKill> schema = RuntimeSchema.createFrom(SecKill.class);

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    public SecKill getSeckill(long seckillId) {
        // redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                // Jedis 并没有进行内部序列化的操作
                // get -> byte[] -> 反序列化 -> Object(Seckill)
                // 采用自定义序列化
                byte[] bytes = jedis.get(key.getBytes());
                // 缓存重获取到
                if (bytes != null) {
                    // 创建一个空对象
                    SecKill secKill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes, secKill, schema);
                    // seckill 被反序列
                    return secKill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String putSeckill(SecKill secKill) {
        // set Object(Seckill) -> 序列化 -> byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + secKill.getSecKillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(secKill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                int timeout = 60 * 60;
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
