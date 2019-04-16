package com.example.androidperfermance;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.os.TraceCompat;
import android.support.v4.util.Pools;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.androidperfermance.adapter.MainAdapter;
import com.example.androidperfermance.thread.ReportTask;
import com.example.dexposed.DexposedManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainAdapter.OnFeedShowListener {


    private static final String TAG = "MainActivity";
    private long mLastFrameTime;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Hook 每一个布局加载的时间
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new LayoutInflater.Factory2() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                long startTime = System.currentTimeMillis();
                View view = getDelegate().createView(parent, name, context, attrs);
                long cost = System.currentTimeMillis() - startTime;
                Log.d(TAG, "加载布局：" + name + "耗时：" + cost);
                return view;
            }

            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                return null;
            }
        });

        //异步 inflate
        new AsyncLayoutInflater(MainActivity.this).inflate(R.layout.activity_main, null, new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(@NonNull View view, int i, @Nullable ViewGroup viewGroup) {
                //view：加载得到 view
                setContentView(view);
                RecyclerView list = findViewById(R.id.list);
                MainAdapter mainAdapter = new MainAdapter();
                list.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                list.setAdapter(mainAdapter);
                mainAdapter.setOnFeedShowListener(MainActivity.this);
            }
        });


        super.onCreate(savedInstanceState);
        TraceCompat.beginSection("AATrace");


        //同步：MainActivity.onCreate(..) cost:223
        //异步 inflate：MainActivity.onCreate(..)  cost:22

//        long startTime = System.currentTimeMillis();
//        setContentView(R.layout.activity_main);
//        long cost = System.currentTimeMillis() - startTime;
//        Log.i(TAG, "setContentView cost " + cost);
//
//        RecyclerView list = findViewById(R.id.list);
//        MainAdapter mainAdapter = new MainAdapter();
//        list.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        list.setAdapter(mainAdapter);
//        mainAdapter.setOnFeedShowListener(MainActivity.this);

        //TraceCompat.endSection();
        //getFps();




        //上报-重要人物需要测试(线程体执行的时间)
        report();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getFps() {
        //4.1以上才支持
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }

        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            //屏幕刷新率
            //public float deviceRefreshRateMs = getRefreshTime();
            private int mFrameCount = 0;

            @Override
            public void doFrame(long frameTimeNanos) {
                if (mLastFrameTime == 0) {
                    mLastFrameTime = frameTimeNanos;
                }

                float diff = (frameTimeNanos - mLastFrameTime) / 1000000.0f;//得到毫秒，正常是 16.66 ms

                if (diff > 500) {
                    double fps = (((double) (mFrameCount * 1000L)) / diff);
                    mFrameCount = 0;
                    mLastFrameTime = 0;
                    Log.d("doFrame", "doFrame: " + fps);
                } else {
                    ++mFrameCount;
                }
                Choreographer.getInstance().postFrameCallback(this);
            }


            /**
             * 获取屏幕刷新率
             * 60HZ 表示 1s 刷新60次
             * <p>
             * 一次刷新的时间：1000/60
             *
             * @return
             */
            public float getRefreshRate() {
                Display display = getWindowManager().getDefaultDisplay();
                float refreshRate = display.getRefreshRate();
                return refreshRate;
            }

            /**
             * 获取一次刷新的时间 16.66ms
             * @return
             */
            public float getRefreshTime() {
                return 1000 / getRefreshRate();
            }
        });

        //Log.d(TAG, "getFps: " + getRefreshTime());//16.666666

    }


    @Override
    public void onFeedShow() {
        Log.d(TAG, "onFeedShow");

        final List<Runnable> tasks = new ArrayList<>();
        tasks.add(new Runnable() {
            @Override
            public void run() {
                callInMainThread();
            }
        });
        tasks.add(new Runnable() {
            @Override
            public void run() {
                callInMainThread2();
            }
        });
        MessageQueue.IdleHandler idleHandler = new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                Log.d(TAG, "queueIdle");
                if (!tasks.isEmpty()) {
                    Runnable task = tasks.get(0);
                    task.run();
                    tasks.remove(task);
                }
                return !tasks.isEmpty();
            }
        };

        Looper.myQueue().addIdleHandler(idleHandler);

//        callInMainThread();
//        callInMainThread2();


    }

    private void callInMainThread() {
        try {
            Thread.sleep(500);
            Log.d(TAG, "callInMainThread");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void callInMainThread2() {
        try {
            Thread.sleep(100);
            Log.d(TAG, "callInMainThread2");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onclick(View view) {
//        SharedPreferences sp = getSharedPreferences("AppPref", Context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = sp.edit();
//        for (int i = 0; i < 10000; i++) {
//            edit.putString(i + "", System.currentTimeMillis() + "-" + System.currentTimeMillis() + "-" + System.currentTimeMillis());
//        }
//        edit.commit();
        testWrite2Disk();
//        PerformanceApp.sActivityList.add(this);
//        startActivity(new Intent(this, MainActivity.class));
//        finish();
    }


    public void testWrite2Disk() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "test.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);

            int i = 0;
            while (++i < 1000000) {
                fos.write("Hello StrictMode".getBytes());
                fos.flush();
            }
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void report() {
        new Thread(new ReportTask()).start();
    }
}
