package com.wohuizhong.client.app.UiBase;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.wohuizhong.client.app.util.DialogUtils;

public class BaseActivity extends AppCompatActivity {
    public static final String TAG = "BaseActivity";

    private Dialog mWaitDlg;

    public BaseActivity getAty() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void openWaitDialog() {
        if (mWaitDlg != null) return;
        mWaitDlg = DialogUtils.createLoadingDialog(this, "请稍候...");
        mWaitDlg.show();
    }

    public void openWaitDialog(String msg) {
        if (mWaitDlg != null) return;
        mWaitDlg = DialogUtils.createLoadingDialog(this, msg);
        mWaitDlg.show();
    }

    public void closeWaitDialog() {
        if (mWaitDlg == null) return;
        mWaitDlg.dismiss();
        mWaitDlg = null;
    }
}
