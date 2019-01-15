package day20190111;


import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * @author : hao
 * @project : StudySjms
 * @description :
 * @time : 2019/1/10 14:36
 */
public class DealOrderThread implements Runnable {

    OrderQueue orderQueue;

    static volatile boolean execute = false;

    public DealOrderThread(OrderQueue orderQueue) {
        this.orderQueue = orderQueue;
    }

    @Override
    public void run() {
        try {
            execute = true;

            while (orderQueue != null && orderQueue.size() > 0) {
                OrderRequest request = (OrderRequest) orderQueue.take();
                dealWithOrder(request);
            }
        } catch (Exception e) {

        } finally {
            execute = false;
        }
    }

    private void dealWithOrder(OrderRequest request) throws InterruptedException {
        Jedis jedis = JedisClientUtil.getJedis();
        try {
            int goodNum = Integer.parseInt(jedis.get("good" + request.getGoodid()));

            if (goodNum < 1) {
                System.out.println("处理失败了呢");
                return;
            }
            jedis.decr("good" + request.getGoodid());
            //Thread.sleep(100);
        } finally {
            JedisClientUtil.returnJedis(jedis);
        }

        System.out.println(Thread.currentThread().getName()+"=="+DateUtils.format2sql(new Date())+"====================用户："+request.getUserid()+"抢购记录已经入库，准备开始跳转支付页面");
    }
}
