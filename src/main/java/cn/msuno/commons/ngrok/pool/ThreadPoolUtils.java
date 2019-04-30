package cn.msuno.commons.ngrok.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolUtils {
    
    private static Logger log = LoggerFactory.getLogger(ThreadPoolUtils.class);
    private final static ExecutorService executorService = Executors.newCachedThreadPool();
    
    private static final AtomicInteger num = new AtomicInteger(1);
    
    public static void shutdown() {
        log.info("Ngrok Worker is shutdown!");
        executorService.shutdownNow();
    }
    
    public static void submit(Runnable worker) {
        executorService.submit(worker);
    }
    
}
