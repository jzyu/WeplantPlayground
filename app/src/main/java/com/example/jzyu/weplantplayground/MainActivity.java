package com.example.jzyu.weplantplayground;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickToolbarAutoHide(View view) {
        startActivity(new Intent(this, ToolbarHiddenOfScrollviewActivity.class));
    }

    public void onClickGestureNormal(View view) {
        startActivity(new Intent(this, GestureNormalActivity.class));
    }

    public void onClickGestureScrollView(View view) {
        startActivity(new Intent(this, GestureScrollViewActivity.class));
    }

    public void onClickPtrBubbleMsg(View view) {
        startActivity(new Intent(this, PtrBubbleMsgActivity.class));
    }
}