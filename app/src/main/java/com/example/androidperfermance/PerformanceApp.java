package com.example.androidperfermance;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import com.example.androidperfermance.thread.ReportTask;
import com.example.androidperfermance.thread.ThreadPoolManager;
import com.example.dexposed.DexposedManager;
import com.facebook.drawee.backends.pipeline.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PerformanceApp extends Application {


    private static final String TAG = "PerformanceApp";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public static List<Activity> sActivityList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //如果在这里初始化 sp 并且获取对应的元素，可能会因为 sp 正在加载导致当前 getString 被挂起，等到加载回来。
        //所以在这里不建议直接调用 sp.getXxx() 获取数据。
        //因为这里会拉长启动的时间。
        //如果一定要在这里获取 sp 存储的数据的话，那么这个sp文件必须是很小。
        //当然这里可以在子线程中去提前加载比较大的 sp 对象。
        SharedPreferences sp = getSharedPreferences("AppPref", Context.MODE_PRIVATE);
//        String string = sp.getString("1", "");
//        Log.d(TAG, "string = " + string);

        //hook 所有线程的创建
        DexposedManager.getIntance(this).hookConstructs(Thread.class, new DexposedManager.HookConstructorCallback<Thread>() {
            @Override
            public void hookConstruct(Thread thread) {
                //得到创建线程的堆栈信息
                Log.i(TAG, thread.getName() + " stack " + Log.getStackTraceString(new Throwable()));
            }
        });

        //使用 hook 机制检测某一个方法的耗时
        DexposedManager.getIntance(this).hookMethod(ReportTask.class, "run", new DexposedManager.HookMethodCallback<ReportTask>() {

            private long start = 0;

            @Override
            public void hookMethodBefore(ReportTask reportTask) {
                start = System.currentTimeMillis();
            }

            @Override
            public void hookMothodAfter(ReportTask reportTask) {
                long end = System.currentTimeMillis();
                Log.d(TAG, "ReportTask run :" + (end - start));
            }

            @Override
            public void hookConstruct(ReportTask reportTask) {

            }
        });

    }

    @Override
    public void onCreate() {
        super.onCreate();


        //参考AsyncTask来设置线程的个数。
//        ExecutorService service = Executors.newFixedThreadPool(CORE_POOL_SIZE);
//
//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                initBugly();
//            }
//        });

//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                initImageLoader();
//            }
//        });

//        service.submit(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(initJPush(getDeviceId()));
//            }
//        });


        ThreadPoolManager.getCpuExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String oldName = Thread.currentThread().getName();
                Thread.currentThread().setName("initBugly");
                initBugly();
                Thread.currentThread().setName(oldName);
            }
        });

        ThreadPoolManager.getCpuExecutor().execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(initJPush(getDeviceId()));
            }
        });


        ThreadPoolManager.getCpuExecutor().execute(new Runnable() {
            @Override
            public void run() {
                initImageLoader();
            }
        });

        //Debug.startMethodTracing("App");
        //TraceCompat.beginSection("AppOncreate");
//        initBugly();
//        initImageLoader();
        //Debug.stopMethodTracing();
        //TraceCompat.endSection();

        initStrictMode();


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Log.e(TAG, "Application onCreate 执行完毕");

    }

    private void initStrictMode() {
        // 分别为MainThread和VM设置Strict Mode
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()//侦测磁盘读
                    .detectDiskWrites()//侦测磁盘写
                    .detectNetwork()//侦测网络操作
                    .detectCustomSlowCalls()//检测应用中执行缓慢代码
                    //.penaltyDeath()//惩罚：直接让应用crash
                    .penaltyLog()//惩罚：输出日志
                    //.detectAll()// 侦测一切潜在违规
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()//侦测 SQlite 泄露
                    .detectLeakedClosableObjects()//侦测 Closeable 类型的对象没有关闭
                    .detectActivityLeaks()//侦测 Activity 泄露
                    .penaltyLog()
                    .build());
        }

    }


    private void initImageLoader() {
        long startTime = System.currentTimeMillis();
        Fresco.initialize(this);
        Log.d(TAG, "initImageLoader cost :" + (System.currentTimeMillis() - startTime));
    }

    private void initBugly() {

        Executors.newFixedThreadPool(3).execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "hello threadpool");
            }
        });

    }

    private int initJPush(String deviceId) {
        //通过 deviceId 来初始化JPush
        try {
            //模拟JPush耗时
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "初始化JPush完毕");

        countDownLatch.countDown();

        return 1;
    }

    private String getDeviceId() {
        return "";
    }


}
