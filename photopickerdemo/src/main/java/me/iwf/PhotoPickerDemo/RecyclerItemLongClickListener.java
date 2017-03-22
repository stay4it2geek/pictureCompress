package me.iwf.PhotoPickerDemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemLongClickListener implements RecyclerView.OnItemTouchListener,RecyclerView.OnLongClickListener{
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }




    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector,mLongGestureDetector;

    public RecyclerItemLongClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }
    public RecyclerItemLongClickListener(Context context, OnItemLongClickListener listener) {
        mLongListener = listener;
        mLongGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildLayoutPosition(childView));
            return true;
        }

        if (childView != null && mLongListener != null && mLongGestureDetector.onTouchEvent(e)) {
            mLongListener.onItemLongClick(childView, view.getChildLayoutPosition(childView));
            return true;
        }

        return false;
    }

    @Override public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}