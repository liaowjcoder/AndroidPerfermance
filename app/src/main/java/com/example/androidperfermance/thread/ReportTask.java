package com.example.androidperfermance.thread;

import android.util.Log;

public class ReportTask implements Runnable {
    private static final String TAG = ReportTask.class.getSimpleName();

    @Override
    public void run() {
        try {
            Log.d(TAG, "report task running");
            Thread.sleep(3000);
            Log.d(TAG, "report task end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
