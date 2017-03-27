package com.wohuizhong.client.app.UiBase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wohuizhong.client.app.http.HttpUtil;


public class NetFragment extends BaseFragment {
    public static final String TAG = "NetFragment";

    public HttpUtil http;

    private void initHttp(Context context) {
        if (context instanceof NetActivity && http == null) {
            http = ((NetActivity) context).http;
        }
    }

    // -- 初始化http --
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initHttp(context);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initHttp(getContext());
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initHttp(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (http != null)
            http.cancelAll();
    }
}

