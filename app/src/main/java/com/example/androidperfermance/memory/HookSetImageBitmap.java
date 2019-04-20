package com.example.androidperfermance.memory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.dexposed.DexposedManager;

public class HookSetImageBitmap {

    private static final String TAG = HookSetImageBitmap.class.getSimpleName();

    public static void hookSetImageBitmap() {
        DexposedManager.getIntance().hookMethod(ImageView.class, "setImageBitmap", new DexposedManager.HookMethodCallback<ImageView>() {
            @Override
            public void hookMethodBefore(ImageView imageView) {

            }

            @Override
            public void hookMothodAfter(final ImageView imageView) {

                if (imageView.getDrawable() instanceof BitmapDrawable) {

                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

                    final Bitmap bitmap = drawable.getBitmap();

                    if (bitmap != null) {

                        int bitmapWidth = bitmap.getWidth();
                        int bitmapHeight = bitmap.getHeight();
                        int viewWidth = imageView.getWidth();
                        int viewHeight = imageView.getHeight();


                        if (viewHeight > 0 && viewWidth > 0) {

                            if (bitmapWidth >= viewWidth << 2 && bitmapHeight >= viewHeight << 2) {
                                //不合理
                                StringBuffer msg = new StringBuffer();

                                msg.append("图片大小不合理：")
                                        .append("bitmapWidth=").append(bitmapWidth)
                                        .append(",bitmapHeight=").append(bitmapHeight)
                                        .append(",viewWidth=").append(viewWidth)
                                        .append(",viewHeight=").append(viewHeight);

                            }
                        } else {
                            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    int bitmapWidth = bitmap.getWidth();
                                    int bitmapHeight = bitmap.getHeight();
                                    int viewWidth = imageView.getWidth();
                                    int viewHeight = imageView.getHeight();
                                    if (bitmapWidth >= viewWidth << 2 && bitmapHeight >= viewHeight << 2) {

                                        StringBuffer msg = new StringBuffer();

                                        msg.append("图片大小不合理：")
                                                .append("bitmapWidth=").append(bitmapWidth)
                                                .append(",bitmapHeight=").append(bitmapHeight)
                                                .append(",viewWidth=").append(viewWidth)
                                                .append(",viewHeight=").append(viewHeight);

                                        //不合理
                                        Log.e(TAG, Log.getStackTraceString(new Throwable(msg.toString())));
                                    }
                                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                                    return true;
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void hookConstruct(ImageView imageView) {

            }
        }, Bitmap.class);
    }

}
