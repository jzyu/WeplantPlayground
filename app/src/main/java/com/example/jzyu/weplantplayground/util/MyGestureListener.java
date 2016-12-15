package com.example.jzyu.weplantplayground.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.zhy.utils.L;

import java.util.Locale;

/**
 * Author: Administrator
 * Date  : 2016/11/30
 */

public class MyGestureListener implements GestureDetector.OnGestureListener {

    static final String TAG = "MyGestureListener";

    private static final int SWIPE_MAX_OFF_PATH = 100;
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    public Context context;

    public MyGestureListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        L.v(TAG, "onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        L.d(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        L.v(TAG, "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        L.d(TAG, String.format(Locale.getDefault(),
                "onScroll, distance(%d, %d), e1(%d, %d), e2(%d, %d)",
                (int)distanceX, (int)distanceY,
                (int)e1.getX(), (int)e1.getY(),
                (int)e2.getX(), (int)e2.getY()
        ));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        L.v(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        L.v(TAG, String.format(Locale.getDefault(),
                "onFling, velocity(%f, %f), e1(%f, %f), e2(%f, %f)",
                velocityX, velocityY,
                e1.getX(), e1.getY(),
                e2.getX(), e2.getY()
        ));

        /*if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            return false;

        if ((e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            Log.e(TAG, "onFling left");

        } else if ((e2.getX() - e1.getX()) > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            Log.e(TAG, "onFling right");
            ((Activity) context).finish();

        }
        */
        return true;
    }
}
