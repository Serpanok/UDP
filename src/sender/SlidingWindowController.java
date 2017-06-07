package sender;

import concurrentutils.Channel;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Vector;


public class SlidingWindowController {
    private Channel channel;
    private final Object lock = new Object();
    private final Object lockTimer = new Object();
    private ArrayList<byte[]> window;
    private ArrayList<Integer> timer;
    private volatile boolean isFreeToPull = false;
    private final int timeout;
    private volatile int leftWindowIndex;
    private volatile int rightWindowIndex;
    private volatile int maxSize = 0;
    private volatile boolean isMaxSizeFixed = false;

    public SlidingWindowController(int capacity, Channel channel,int timeout){
        this.channel = channel;
        window = new ArrayList<>();
        timer = new ArrayList<>();
        this.timeout = timeout;

        leftWindowIndex = 0;
        rightWindowIndex = capacity - 1;
        isFreeToPull = true;
    }

    public void push(byte[] buffer){

        synchronized (lock){
            while(!isFreeToPull){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            window.add(buffer);
            if(window.size() == rightWindowIndex + 1){
                isFreeToPull = false;
            }
        }
        synchronized (lockTimer){
            timer.add(timeout);
        }
        channel.put(buffer);
    }

    public byte[] getBytes(){
        return (byte[]) channel.get();
    }

    public void resend(int index){
        byte[] buffer;
        synchronized (lock){
            buffer = window.get(index);
        }
        if(buffer != null)
            channel.put(buffer);
    }

    public void setReceived(int index){
        synchronized (lock) {
            window.set(index, null);
        }

        synchronized (lockTimer) {
            timer.set(index, -1);
            int a = leftWindowIndex;
            int c = timer.size();
            for (int i = a; i < c; ++i) {
                if (timer.get(i) == -1) {
                    leftWindowIndex++;
                    if (!isMaxSizeFixed)
                        rightWindowIndex++;
                    else
                        rightWindowIndex = maxSize;
                    isFreeToPull = true;
                } else break;
            }
        }

        if(leftWindowIndex > rightWindowIndex){
            System.exit(0);
        }

        synchronized (lock) {
            if (isFreeToPull)
                lock.notify();
        }
    }

    public void setMaxSize(int size){
        this.maxSize = size;
        this.isMaxSizeFixed = true;
    }

    public void doTimerIteration(){
        synchronized (lockTimer){
            int a = leftWindowIndex;
            int b = timer.size();
            for(int i = a; i < b; ++i) {
                int t = timer.get(i);
                if (t == -1)
                    continue;
                if (t == 1) {
                    timer.set(i, timeout);
                    resend(i);
                } else {
                    timer.set(i, t - 1);
                }
            }
        }
    }
}
