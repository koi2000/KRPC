package com.koi.krpc.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author koi
 * @date 2023/8/5 10:57
 */
// 线程池工具类
public class ThreadPoolFactory {
    // 线程池参数
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);
    private static Map<String, ExecutorService> threadPoolMap = new ConcurrentHashMap<>();

    private ThreadPoolFactory() {
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    private static ExecutorService createDefaultThreadPool(String threadNamePrefix, boolean daemon) {
        // 使用有界队列
        ExecutorService pool = threadPoolMap.computeIfAbsent(threadNamePrefix, k ->
                createThreadPool(threadNamePrefix, daemon));
        if (pool.isShutdown() || pool.isTerminated()) {
            threadPoolMap.remove(threadNamePrefix);
            pool = createThreadPool(threadNamePrefix, daemon);
            threadPoolMap.put(threadNamePrefix, pool);
        }
        return pool;
    }

    public static void shutDownAll() {
        logger.info("关闭所有线程池...");
        threadPoolMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("关闭线程池 [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10,TimeUnit.SECONDS);
            }catch (InterruptedException ie){
                logger.error("关闭线程池失败!");
                executorService.shutdown();
            }
        });
    }

    private static ExecutorService createThreadPool(String threadNamePrefix, boolean daemon) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES,
                workQueue, threadFactory);
    }

    // 创建ThreadFactory，如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用default
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon)
                        .build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
