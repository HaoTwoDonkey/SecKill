package day20190111;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : hao
 * @project : StudySjms
 * @description :
 * @time : 2019/1/10 14:24
 */
public class OrderQueue extends LinkedBlockingQueue {

    static AtomicInteger num = new AtomicInteger(0);

    private  Integer queueMaxSize;

    public OrderQueue(){
        super();
    }

    public OrderQueue(int size){
        super(size);
        queueMaxSize = size;
    }

    @Override
    public boolean offer(Object o) {

        boolean success =  super.offer(o);

        if(success){
            num.getAndIncrement();
        }

        return success;
    }


    public  boolean isNotFull(){
        return num.get() < queueMaxSize;
    }


}
