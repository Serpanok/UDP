package concurrentutils;

/**
 * Created by grihon07 on 03.10.2014.
 */
public class WorkerThread implements Runnable {
    private boolean isRunning = false;
    private Closeable currentTask = null;
    private final Object lock = new Object();
    private final ThreadPool boss;
    private final Thread thread;

    public WorkerThread(ThreadPool pool){
        boss = pool;
        thread = new Thread(this);
        isRunning = true;
    }

    public void start(){
        thread.start();
    }

    public void stop(){
        isRunning = false;
        if(currentTask != null)
            currentTask.close();
        thread.interrupt();
    }
    @Override
    public void run() {
        while(isRunning) {
            synchronized (lock) {
                while (currentTask == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                currentTask.run();
            } finally {
                boss.release(this);
                synchronized (lock) {
                    currentTask = null;
                }
            }
        }
    }

    public void setCurrentTask(Closeable task){
        if(task == null){
            throw new IllegalArgumentException("Null task");
        }
        synchronized (lock){
            if(currentTask == null){
                this.currentTask = task;
                lock.notify();
            }
            else
                throw new IllegalStateException("Already busy");
        }
    }
}
