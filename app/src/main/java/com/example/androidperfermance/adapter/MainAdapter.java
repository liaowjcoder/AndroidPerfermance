package com.example.androidperfermance.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.androidperfermance.MainActivity;
import com.example.androidperfermance.R;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private static String TAG = MainActivity.class.getSimpleName();
    private static List<String> mDataSource = new ArrayList<>();

    {
        mDataSource.add("Android");
        mDataSource.add("PHP");
        mDataSource.add("iOS");
        mDataSource.add("Java");
        mDataSource.add("Ruby");
        mDataSource.add("Json");
        mDataSource.add("Python");
        mDataSource.add("ClassLoader");
        mDataSource.add("音视频");
        mDataSource.add("Sony");
        mDataSource.add("维他奶");
        mDataSource.add("板蓝根");
        mDataSource.add("华为");
        mDataSource.add("Google");
        mDataSource.add("倚天屠龙记");
        mDataSource.add("三大傻宝");
        mDataSource.add("华南师范大学");
        mDataSource.add("中山大学");
        mDataSource.add("MacBook Pro");
        mDataSource.add("MacBook Air");
        mDataSource.add("MacBook");
        mDataSource.add("Mac Pro");
        mDataSource.add("Mac Mini");
    }

    private OnFeedShowListener mOnFeedShowListener;
    private boolean mHasRecorded;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        long start = System.currentTimeMillis();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        long end = System.currentTimeMillis();
        Log.d(TAG, "onCreateViewHolder: "+(end-start));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
//        long start = System.currentTimeMillis();
        viewHolder.tvItemView.setText(mDataSource.get(i));

        if (i == 0 && !mHasRecorded) {
            mHasRecorded = true;
            viewHolder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    viewHolder.itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                    Log.d(TAG, "列表第一个 View 开始绘制");

                    if (mOnFeedShowListener != null) {
                        //首帧
                        mOnFeedShowListener.onFeedShow();
                    }
                    return true;
                }
            });
        }

//        long end = System.currentTimeMillis();

//        Log.d(TAG, "onBindViewHolder: "+(end-start));
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvItemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemView = itemView.findViewById(R.id.main_item_view);
        }
    }

    public void setOnFeedShowListener(OnFeedShowListener mOnFeedShowListener) {
        this.mOnFeedShowListener = mOnFeedShowListener;
    }
    public interface OnFeedShowListener {
        void onFeedShow();
    }
}