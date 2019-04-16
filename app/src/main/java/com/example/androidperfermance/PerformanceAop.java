package com.example.androidperfermance;

import android.util.Log;

import com.example.androidperfermance.adapter.MainAdapter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class PerformanceAop {

    @Around("execution(* com.example.androidperfermance.PerformanceApp.**(..))")
    public void getTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();

        String name = signature.toShortString();

        long startTime = System.currentTimeMillis();

        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long costTime = System.currentTimeMillis() - startTime;

        Log.i("Aop", "method " + name + " cost:" + costTime);
    }

    @Around("execution(* android.app.Activity.setContentView(..))")
    public void hookSetContentViewTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();

        String name = signature.toShortString();
        long startTime = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long costTime = System.currentTimeMillis() - startTime;
        Log.i("Aop", "method " + name + " cost:" + costTime);
    }

    @Around("execution(* android.app.Activity.onCreate(..))")
    public void hookActivityOnCreateTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();

        String name = signature.toShortString();
        long startTime = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long costTime = System.currentTimeMillis() - startTime;
        Log.i("Aop", "method " + name + " cost:" + costTime);
    }

    private long start = 0;

//    @Around("execution(* com.example.androidperfermance.adapter.MainAdapter.onCreateViewHolder(..))")
//    public Object hookOnCreateViewHolder(ProceedingJoinPoint joinPoint) {
//        Signature signature = joinPoint.getSignature();
//
//        String name = signature.toShortString();
//        start = System.currentTimeMillis();
//        //Log.i("Aop", "method " + name);
//        try {
//            return joinPoint.proceed();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        return null;
//    }

//    /**
//     * 计算 onCreateViewHolder 返回值
//     *
//     * @param viewHolder
//     */
//    @AfterReturning(pointcut = "execution(* com.example.androidperfermance.adapter.MainAdapter.onCreateViewHolder(..))", returning = "viewHolder")
//    public void getHeight(MainAdapter.ViewHolder viewHolder) {
//        long end = System.currentTimeMillis();
//        Log.i("Aop", "method  onCreateViewHolder" + " cost:" + (end - start));
//    }


    @Around("execution(* com.example.androidperfermance.adapter.MainAdapter.**(..))")
    public void hookMainAdapter(ProceedingJoinPoint joinPoint) {
        Log.i("Aop", "MainAdapter");
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


//    @Around("execution(* java.lang.Thread.**(..))")
//    public void hookThreadCreate(ProceedingJoinPoint joinPoint) {
//        Log.i("Aop", "hookThreadCreate");
//        try {
//            System.out.println(Log.getStackTraceString(new Throwable()));
//            joinPoint.proceed();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//    }

}
