package com.mk.asynctest.threadpool;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author hjm
 * @Date 2024/6/28 14:04
 */
public class ThreadManager {

    private static Map<String, ExecutorService> poolMap = new ConcurrentHashMap<>();

    public static ExecutorService getPool(String threadNamePrefix) {
        return poolMap.computeIfAbsent(threadNamePrefix, (key) -> {
            return new ThreadPoolExecutor(
                    16,
                    32,
                    60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(1000),
                    new CustomThreadFactory(key),
                    new ThreadPoolExecutor.DiscardPolicy());
        });
    }

    static class CustomThreadFactory implements ThreadFactory {
        private final String threadNamePrefix;
        private final AtomicInteger threadId = new AtomicInteger(1);

        public CustomThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            String threadName = threadNamePrefix + threadId.getAndIncrement();
            Thread t = new Thread(r, threadName);
            return t;
        }
    }


}
