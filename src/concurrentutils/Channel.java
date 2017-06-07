package concurrentutils;

import java.util.LinkedList;

/**
 * Created by grihon07 on 19.09.2014.
 */
public class Channel {

    private final LinkedList queue = new LinkedList();
    private final Object lock = new Object();
    private int maxQueueSize = 2000000000;

    public Channel(){

    }

    public Channel(int maxQueueSize){
        this.maxQueueSize = maxQueueSize;
    }

    /**
     *
     * @param object cannot be null
     */
    public void put(Object object){
        synchronized (lock) {
            if(object == null)
                throw new IllegalArgumentException("null put to channel");
            while(queue.size() > maxQueueSize){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(object);
            lock.notify();
        }
    }

    public Object get(){
        synchronized (lock){
            while(queue.isEmpty()){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }lock.notify();
            return queue.removeLast();
        }
    }

    public boolean isEmpty(){
        synchronized (lock){
            return queue.isEmpty();
        }
    }
}
