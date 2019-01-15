package day20190111;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author : hao
 * @project : StudySjms
 * @description :
 * @time : 2019/1/11 15:35
 */
public class SeckillController {

    private static OrderQueue queue;

    ThreadPoolExecutor pool = new ThreadPoolExecutor(1,10,0, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadFactoryBuilder().setNameFormat("Order-task-%d").build());

    public void seckillByOne(OrderRequest orderRequest) {

        Jedis jedis = JedisClientUtil.getJedis();

        try {
            int goodNum = Integer.parseInt(jedis.get("good" + orderRequest.getGoodid()));
            if (goodNum < 1) {
                System.out.println(Thread.currentThread().getName()+"=="+DateUtils.format2sql(new Date()) + ": 商品数量为=" + goodNum + "用户=" + orderRequest.getUserid() + "没抢到");
                return;
            }
            if (queue == null) {
                queue = new OrderQueue(10);
            }

            if (queue.isNotFull()) {
                queue.offer(orderRequest);
            } else {
                System.out.println(Thread.currentThread().getName()+"=="+DateUtils.format2sql(new Date()) + "：用户：" + orderRequest.getUserid() + "没抢到=》因为抢购队列已满");
                return;
            }

            System.out.println(Thread.currentThread().getName()+"=="+DateUtils.format2sql(new Date()) + "：用户：" + orderRequest.getUserid() + "抢到了商品，将要跳转支付页面");

            if (!DealOrderThread.execute) {
                DealOrderThread dealQueue = new DealOrderThread(queue);
                pool.execute(dealQueue);
            }

        } catch (Exception e) {
            System.out.println("抢购失败了。。。");
        } finally {
            JedisClientUtil.returnJedis(jedis);
        }
    }

    public void seckillByTwo(OrderRequest orderRequest) {
        Jedis jedis = null;
        try {
            jedis = JedisClientUtil.getJedis();
            while (true) {
                try {
                    jedis.watch("good" + orderRequest.getGoodid());
                    int prdNum = Integer.parseInt(jedis.get("good" + orderRequest.getGoodid()));
                    if (prdNum > 0) {
                        Transaction transaction = jedis.multi();
                        transaction.set("good" + orderRequest.getGoodid(), String.valueOf(prdNum - 1));
                        List<Object> result = transaction.exec();
                        if (result == null || result.isEmpty()) {
                            System.out.println(Thread.currentThread().getName()+"=="+DateUtils.format2sql(new Date()) + "：用户：" + orderRequest.getUserid() + "没抢到=》因为抢购失败");
                            break;
                        } else {
                            System.out.println(Thread.currentThread().getName()+"=="+DateUtils.format2sql(new Date()) + "：用户：" + orderRequest.getUserid() + "抢到商品了");
                            break;
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName()+"=="+DateUtils.format2sql(new Date()) + "：用户：" + orderRequest.getUserid() + "没抢到=》因为商品数量为"+prdNum);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    jedis.unwatch();
                }
            }
        } catch (Exception e) {
            System.out.println("redis bug:" + e.getMessage());
        } finally {
            JedisClientUtil.returnJedis(jedis);
        }

    }
}
