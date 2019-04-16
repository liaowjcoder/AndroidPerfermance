package com.example.androidperfermance.thread;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {

    private volatile static ThreadPoolManager sInstance = null;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 定义线程工厂
     */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r);
            //设置优先级
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            thread.setName(ThreadPoolManager.class.getSimpleName() + "#" + count.getAndIncrement());
            return thread;
        }
    };

    private final static Executor sCPUExecutor =
            new ThreadPoolExecutor(CPU_COUNT + 1,
                    CPU_COUNT + 1,
                    60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    sThreadFactory
            );

    private final static Executor sIoExecutor =
            Executors.newCachedThreadPool(sThreadFactory);

    private ThreadPoolManager() {
    }

    public static ThreadPoolManager getsInstance() {

        if (sInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (sInstance == null) {
                    sInstance = new ThreadPoolManager();
                }
            }
        }
        return sInstance;
    }

    public static Executor getCpuExecutor() {
        return sCPUExecutor;
    }

    public static Executor getIoExecutor() {
        return sIoExecutor;
    }
}
