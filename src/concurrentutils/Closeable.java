package concurrentutils;

/**
 * Created by grihon07 on 30.10.2014.
 */
public interface Closeable extends Runnable {
    void close();
}