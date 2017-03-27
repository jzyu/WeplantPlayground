package com.example.jzyu.weplantplayground;

import android.app.Application;

import com.wohuizhong.client.app.http.Api;

/**
 * Author: jzyu
 * Date  : 2016/12/20
 */

public class PlaygroundApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Fresco.initialize(this);
        Api.init(this);
    }
}
