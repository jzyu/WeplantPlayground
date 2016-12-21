package com.example.jzyu.weplantplayground.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;

import com.zhy.utils.DensityUtils;

/**
 * Created by Administrator on 2015/7/13.
 */
public class ScreenTool {

    public static int[]  getWinPos(View view){
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return location;
    }

    public static int[]  getScreenPos(View view){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y - getStatusBarHeight(activity);
    }

    public static final int STATUS_BAR_HEIGHT = 25;
    public static int getStatusBarHeight(Context context) {
        return DensityUtils.dp2px(context, STATUS_BAR_HEIGHT);
    }
}
