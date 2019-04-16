package com.example.dexposed;

import android.content.Context;

import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

/**
 * @author 六号表哥
 * Hook 管理类
 */
public class DexposedManager {

    private final static String TAG = DexposedManager.class.getSimpleName();
    private volatile static DexposedManager sIntance = null;

    private DexposedManager(Context context) {
    }

    public static DexposedManager getIntance(Context context) {

        if (sIntance == null) {
            synchronized (DexposedManager.class) {
                if (sIntance == null) {
                    sIntance = new DexposedManager(context);
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
    public <T> void hookMethod(final Class<T> clazz, final String methodName, final HookMethodCallback<T> callback) {


        DexposedBridge.hookAllConstructors(clazz, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (callback != null) {
                    callback.hookConstruct((T) param.thisObject);
                }

                DexposedBridge.findAndHookMethod(clazz, methodName, new XC_MethodHook() {
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
                });
            }
        });

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
