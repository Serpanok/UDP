package concurrentutils;

import java.util.LinkedList;

/**
 * Created by grihon07 on 03.10.2014.
 */
public class ThreadPool {
    private final Object lock = new Object();
    private final int initSize;
    private final int max;
    private final int inc;
    private final LinkedList<WorkerThread> allWorkers = new LinkedList<WorkerThread>();
    private final LinkedList<WorkerThread> freeWorkers = new LinkedList<WorkerThread>();

    public ThreadPool(int initSize, int max, int inc){
        this.initSize = initSize;
        this.max = max;
        this.inc = inc;
        for(int i =0; i < initSize; ++i) {
            WorkerThread runWorker = new WorkerThread(this);
            runWorker.start();
            allWorkers.add(runWorker);
            freeWorkers.add(runWorker);
        }
    }

    public void executeTask(Closeable task){
        synchronized (lock){
            while(freeWorkers.isEmpty() && allWorkers.size() >= max){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!freeWorkers.isEmpty()) {
            freeWorkers.removeLast().setCurrentTask(task);
        }

        else if(allWorkers.size() < max){
            WorkerThread runWorker = null;
            int index = allWorkers.size();
            synchronized (lock) {
                for (int i = index; i < index + inc - 1 && i < max; ++i) {
                    runWorker = new WorkerThread(this);
                    runWorker.start();
                    allWorkers.add(runWorker);
                    freeWorkers.addLast(runWorker);
                }
                runWorker = new WorkerThread(this);
                allWorkers.add(runWorker);
            }
            runWorker.setCurrentTask(task);
            runWorker.start();
        }
    }

    public void release(WorkerThread worker){
        synchronized (lock) {
            freeWorkers.addLast(worker);
            lock.notify();
        }
    }

    public void stop(){
        for(WorkerThread worker : allWorkers){
            worker.stop();
        }
    }
}
