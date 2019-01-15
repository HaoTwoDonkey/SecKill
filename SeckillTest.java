package day20190111;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @author : hao
 * @project : StudySjms
 * @description :
 * @time : 2019/1/11 16:30
 */
public class SeckillTest {

    public static void main(String[] args) throws InterruptedException {
        long beginTime = System.currentTimeMillis();

        Jedis jedis = JedisClientUtil.getJedis();
        jedis.set("goodone", "10");

        final SeckillController controller = new SeckillController();
        //ExecutorService pool = Executors.newFixedThreadPool(100);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,10,0, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadFactoryBuilder().setNameFormat("Seckill-task-%d").build());
        final CountDownLatch countDownLatch = new CountDownLatch(100);
        final Semaphore semaphore = new Semaphore(100);
        for (int i = 0; i < 100; i++) {
            final int num = i;
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        OrderRequest request = new OrderRequest("user_" + num, "one", DateUtils.format2sql(new Date()));
                        controller.seckillByOne(request);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                        countDownLatch.countDown();
                    }
                }
            });
        }
        countDownLatch.await();
        threadPoolExecutor.shutdown();

        System.out.println("ÃëÉ±¹²ºÄÊ±"+String.valueOf(System.currentTimeMillis()-beginTime));
        System.exit(1);
    }
}
