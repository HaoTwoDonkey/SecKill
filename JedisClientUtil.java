package day20190111;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author : hao
 * @project : StudySjms
 * @description :   redis���ӳع�������
 * @time : 2019/1/11 15:34
 */
public class JedisClientUtil {
    private static JedisPool pool;

    private static Object obj = new Object();

    static {

        JedisPoolConfig poolconfig = new JedisPoolConfig();
        poolconfig.setMaxIdle(100);//������ø���
        poolconfig.setMinIdle(100);//��С���ø���
        poolconfig.setMaxTotal(100);//���������

        poolconfig.setMaxWaitMillis(10 * 1000);
        poolconfig.setTestOnBorrow(true);
        pool = new JedisPool(poolconfig, "127.0.0.1", 6379);
    }

    //���jedis��Դ�ķ���
    public static Jedis getJedis() {

        return pool.getResource();

    }

    public synchronized static void returnJedis(Jedis jedis) {
        if (null != jedis) {
            pool.returnResource(jedis);
        }

    }

    public synchronized static void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            pool.returnBrokenResource(jedis);
        }
    }
}
