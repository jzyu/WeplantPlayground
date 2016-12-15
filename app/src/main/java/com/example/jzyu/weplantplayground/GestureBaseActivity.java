package com.example.jzyu.weplantplayground;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.jzyu.weplantplayground.util.MyGestureListener;

/**
 * Author: Administrator
 * Date  : 2016/11/30
 */

public class GestureBaseActivity extends AppCompatActivity{

    protected GestureDetector gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetector(this, new MyGestureListener(this));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
