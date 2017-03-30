package com.example.jzyu.weplantplayground;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public void onClickLocalData(View view) {
        startActivity(new Intent(this, TestLocalListActivity.class));
    }

    public void onClickNetData(View view) {
        startActivity(new Intent(this, TestNetListActivity.class));
    }

    public void onClickMisc(View view) {
        startActivity(new Intent(this, TestMiscActivity.class));
    }
}
