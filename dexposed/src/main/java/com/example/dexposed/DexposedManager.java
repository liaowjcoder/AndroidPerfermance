package com.example.dexposed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 六号表哥
 * Hook 管理类
 */
public class DexposedManager {

    private final static String TAG = DexposedManager.class.getSimpleName();
    private volatile static DexposedManager sIntance = null;

    private DexposedManager() {
    }

    public static DexposedManager getIntance() {

        if (sIntance == null) {
            synchronized (DexposedManager.class) {
                if (sIntance == null) {
                    sIntance = new DexposedManager();
                }
            }
        }
        return sIntance;
    }


    /**
     * hook 构造方法
     *
     * @param clazz
     * @param callback
     * @param <T>
     */
    public <T> void hookConstructs(Class<T> clazz, final HookConstructorCallback<T> callback) {


        DexposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (callback != null) {
                    callback.hookConstruct((T) param.thisObject);
                }
            }
        });

    }

    /**
     * hook 某一个方法
     *
     * @param clazz
     * @param methodName
     * @param callback
     * @param <T>
     */
    public <T> void hookMethod(final Class<T> clazz, final String methodName, final HookMethodCallback<T> callback, final Class... args) {


        Object[] objs = new Object[args != null ? args.length + 1 : 1];

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                objs[i] = args[i];
            }
        }

        //设置最后一个元素的为 callback
        objs[objs.length - 1] = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                if (callback != null) {
                    callback.hookMethodBefore((T) param.thisObject);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (callback != null) {
                    callback.hookMothodAfter((T) param.thisObject);
                }
            }
        };
        DexposedBridge.findAndHookMethod(clazz, methodName, objs);

    }


    /**
     * 构造回调
     *
     * @param <T>
     */
    public interface HookConstructorCallback<T> {
        void hookConstruct(T t);
    }


    /**
     * 方法回调
     *
     * @param <T>
     */
    public interface HookMethodCallback<T> extends HookConstructorCallback<T> {
        void hookMethodBefore(T t);

        void hookMothodAfter(T t);
    }
}
