package com.wohuizhong.client.app.UiBase;

import android.os.Bundle;

import com.wohuizhong.client.app.http.HttpUtil;
import com.wohuizhong.client.app.widget.LoadingView;

public class NetActivity extends BaseActivity {
    protected LoadingView mLoadingView = null;
    public HttpUtil http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadingView = new LoadingView(this);
        http = new HttpUtil(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        http.cancelAll();
    }
}
