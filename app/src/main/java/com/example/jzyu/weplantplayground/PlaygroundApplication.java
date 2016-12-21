package com.example.jzyu.weplantplayground;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Author: jzyu
 * Date  : 2016/12/20
 */

public class PlaygroundApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
