package com.wohuizhong.client.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jzyu.weplantplayground.R;
import com.wohuizhong.client.app.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoadingView extends LinearLayout {

    @Bind(R.id.container_loading)
    LinearLayout containerLoading;
    @Bind(R.id.container_retry)
    LinearLayout containerRetry;
    @Bind(R.id.tv_error_message)
    TextView tvErrorMessage;

    private ViewGroup mParent = null;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = LayoutInflater.from(context);
        ButterKnife.bind(inflater.inflate(R.layout.common_loading, this));

        setStatus(Status.LOADING);
    }

    // use in LinearLayout
    public void attach(View anchorView, OnClickListener retryClickListener) {
        if (mParent != null || anchorView == null)
            return;

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent = (ViewGroup) anchorView.getParent();
        mParent.addView(this, 1, lp); //TODO: FIXME 不能写死为1

        findViewById(R.id.btn_retry).setOnClickListener(retryClickListener);
        setStatus(Status.LOADING);
    }

    // use in FrameLayout
    public void attachToParent(ViewGroup parent, OnClickListener retryClickListener) {
        if (mParent != null)
            return;

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent = parent;
        mParent.addView(this, lp);

        findViewById(R.id.btn_retry).setOnClickListener(retryClickListener);
        setStatus(Status.LOADING);
    }

    public void detach() {
        if (mParent == null)
            return;

        mParent.removeView(this);
        mParent = null;
    }

    public void setStatusAsLoading() {
        setStatus(Status.LOADING);
    }

    public void setStatusAsRetry() {
        setStatus(Status.RETRY);
    }

    public void setStatusAsRetry(String errorMsg) {
        if (! StringUtil.isEmpty(errorMsg)) {
            tvErrorMessage.setText(errorMsg);
        }

        setStatus(Status.RETRY);
    }

    private void setStatus(Status status) {
        switch (status) {
            default:
            case UNATTACHED:
                break;
            case LOADING:
                containerLoading.setVisibility(VISIBLE);
                containerRetry.setVisibility(INVISIBLE);
                break;
            case RETRY:
                containerLoading.setVisibility(INVISIBLE);
                containerRetry.setVisibility(VISIBLE);
                break;
        }
    }

    private enum Status {
        UNATTACHED,
        LOADING,
        RETRY
    }

    public boolean isAttached() {
        return mParent != null;
    }
}
