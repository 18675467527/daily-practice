package com.mk.async.threadpool;

import java.util.concurrent.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtil {

    // 用于存储线程池的缓存
    private static final Map<String, ExecutorService> threadPoolMap = new ConcurrentHashMap<>();
    
    // 锁对象，确保线程池的创建是线程安全的
    private static final Lock lock = new ReentrantLock();

    // 核心线程数
    private static final int CORE_POOL_SIZE = 5;

    // 最大线程数
    private static final int MAX_POOL_SIZE = 10;

    // 队列容量
    private static final int QUEUE_CAPACITY = 500;

    /**
     * 获取指定名称的线程池，如果线程池不存在，则创建一个新的线程池。
     * @param threadPoolName 线程池名称
     * @return 线程池实例
     */
    public static ExecutorService getThreadPool(String threadPoolName) {
        // 先检查线程池是否已存在
        ExecutorService threadPool = threadPoolMap.get(threadPoolName);
        
        if (threadPool == null) {
            // 如果线程池不存在，尝试创建一个新的线程池
            lock.lock(); // 获取锁，保证线程池的创建是线程安全的
            try {
                // 再次检查线程池是否已经被创建（可能在锁外已被创建）
                threadPool = threadPoolMap.get(threadPoolName);
                if (threadPool == null) {
                    threadPool = createThreadPool(threadPoolName);
                    threadPoolMap.put(threadPoolName, threadPool);
                }
            } finally {
                lock.unlock();
            }
        }
        
        return threadPool;
    }

    /**
     * 创建新的线程池
     * @param threadPoolName 线程池名称
     * @return 线程池实例
     */
    private static ExecutorService createThreadPool(String threadPoolName) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final ThreadGroup group = Thread.currentThread().getThreadGroup();
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(group, r, threadPoolName + "-Thread-" + threadNumber.getAndIncrement());
            }
        };

        return new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY),
            threadFactory,
            new ThreadPoolExecutor.DiscardPolicy()
        );
    }

    /**
     * 提交任务到指定名称的线程池
     * @param threadPoolName 线程池名称
     * @param task 要执行的任务
     */
    public static void submitTask(String threadPoolName, Runnable task) {
        ExecutorService threadPool = getThreadPool(threadPoolName);
        threadPool.submit(task);
    }

    /**
     * 获取指定名称线程池的状态
     * @param threadPoolName 线程池名称
     * @return 线程池的状态信息
     */
    public static String getThreadPoolStatus(String threadPoolName) {
        ExecutorService threadPool = threadPoolMap.get(threadPoolName);
        if (threadPool == null) {
            return "Thread pool with name " + threadPoolName + " does not exist.";
        }

        if (threadPool instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) threadPool;
            return "Core Pool Size: " + executor.getCorePoolSize() + ", " +
                   "Pool Size: " + executor.getPoolSize() + ", " +
                   "Active Count: " + executor.getActiveCount() + ", " +
                   "Completed Task Count: " + executor.getCompletedTaskCount() + ", " +
                   "Queue Size: " + executor.getQueue().size();
        }
        return "Unknown ThreadPool type.";
    }

    /**
     * 获取当前所有线程池的名称
     * @return 当前所有线程池名称的集合
     */
    public static Set<String> getAllThreadPoolNames() {
        return threadPoolMap.keySet();
    }

    /**
     * 关闭所有线程池，优雅关闭所有线程池
     */
    public static void shutdownAll() {
        for (ExecutorService threadPool : threadPoolMap.values()) {
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }
        }
    }

    /**
     * 关闭指定名称的线程池，优雅关闭
     * @param threadPoolName 线程池名称
     */
    public static void shutdown(String threadPoolName) {
        ExecutorService threadPool = threadPoolMap.get(threadPoolName);
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }
    }

    /**
     * 直接提交任务并等待完成（同步执行）
     * @param threadPoolName 线程池名称
     * @param task 要执行的任务
     */
    public static void submitAndWait(String threadPoolName, Runnable task) {
        ExecutorService threadPool = getThreadPool(threadPoolName);
        try {
            threadPool.submit(task).get();  // 使用get()等待任务完成
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            System.err.println("Task execution interrupted: " + e.getMessage());
        }
    }

    // 测试用例
    public static void main(String[] args) {
        // 提交任务
        submitTask("pool1", () -> System.out.println(Thread.currentThread().getName() + " is working in pool1"));
        submitTask("pool2", () -> System.out.println(Thread.currentThread().getName() + " is working in pool2"));
        
        // 获取线程池状态
        System.out.println(getThreadPoolStatus("pool1"));
        System.out.println(getThreadPoolStatus("pool2"));

        // 等待任务完成
        submitAndWait("pool1", () -> {
            System.out.println(Thread.currentThread().getName() + " executed in pool1");
        });

        // 打印所有线程池名称
        System.out.println("All thread pool names: " + getAllThreadPoolNames());

        // 关闭线程池
        shutdownAll();
    }
}
