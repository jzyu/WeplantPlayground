package com.wohuizhong.client.app.http;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.wohuizhong.client.app.util.CompatUtil;
import com.wohuizhong.client.app.util.Consts;
import com.wohuizhong.client.app.util.DialogUtils;
import com.wohuizhong.client.app.util.StringUtil;
import com.zhy.utils.L;
import com.zhy.utils.NetUtils;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * -- 带 ui 提示 --
 *   go         : successCallback, 提示网络未连接，toast后台报错
 *   goWait     : go + 等待对话框
 *   goWaitToast: goWait + 简化callback为toastMessage
 *
 * * -- quick ui --
 *   goQuickToggle : 只要有网络连接(否则toast提示无网络)，就立即执行successCallback,
 *               后台返回错误信息时先toast提示再执行 reverseCallback
 *
 *
 * -- 无 ui --
 *   goBackground: callback or successCallback
 *   goNoResult  : 不管网络结果
 */

public class HttpUtil {
    public static final String TAG = HttpUtil.class.getSimpleName();

    private Context context;
    private Dialog waitDlg;
    private List<Request> httpRequests = new ArrayList<>();

    public HttpUtil(Context context) {
        this.context = context;
    }

    public void cancelAll() {
        for (okhttp3.Request request : httpRequests) {
            Api.cancelCallsWithTag(request);
        }
    }

    private void openWaitDialog(String message) {
        if (waitDlg != null) return;
        if (StringUtil.isEmpty(message)) message = "请稍候...";
        waitDlg = DialogUtils.createLoadingDialog(context, message);
        waitDlg.show();
    }

    private void closeWaitDialog() {
        if (waitDlg == null) return;
        waitDlg.dismiss();
        waitDlg = null;
    }

    private  <T> void _go(final Call<T> apiCall,
                          final HttpCallback<T> callback,
                          final boolean tipNoNetwork,
                          final boolean showWaitDialog,
                          String waitDialogMessage,
                          ToggleCallback toggleOnReady) {
        if (tipNoNetwork && ! NetUtils.isConnected(context)) {
            callback.onError(0, Consts.PLEASE_CONNECT_NET);
            return;
        }

        if (showWaitDialog) openWaitDialog(waitDialogMessage);

        if (toggleOnReady != null) toggleOnReady.doToggle();

        httpRequests.add(apiCall.request());
        apiCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (isActivityDestroyed())
                    return;
                if (showWaitDialog)
                    closeWaitDialog();

                if (! response.isSuccessful()) {
                    callback.onError(response.code(), logHttpError(call, response));
                } else {
                    callback.onSuccess(call, response);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (isActivityDestroyed())
                    return;

                if (showWaitDialog) closeWaitDialog();

                if (call.isCanceled() || t instanceof SocketException) {
                    // 取消时会抛出SocketException且isCanceled = false，原因未知
                    L.e(TAG, "http canceled. url=" + call.request().url().toString());
                    callback.onCancel(call);
                } else {
                    callback.onError(0, logHttpError(call, t));
                }
            }

            private boolean isActivityDestroyed() {
                if (context instanceof Activity) {
                    if (CompatUtil.isActivityDestroyed((Activity) context)) {
                        L.e(TAG, "Activity has destroyed, http callback ignore.");
                        return true;
                    }
                }
                return false;
            }
        });
    }

    final public <T> void goBackground(final Call<T> apiCall, final HttpSuccessCallback<T> callback) {
        _go(apiCall, new HttpCallback<T>() {
            @Override
            public void onSuccess(Call<T> call, Response<T> response) {
                callback.onSuccess(call, response);
            }

            @Override
            public void onError(int responseCode, String errorMsg) {
                // do nothing
            }
        }, false, false, null, null);
    }

    final public <T> void goBackground(final Call<T> apiCall, final HttpCallback<T> callback) {
        _go(apiCall, callback, false, false, null, null);
    }

    final public <T> void goWait(Call<T> apiCall, final HttpSuccessCallback<T> successCallback, String waitMessage) {
        _go(apiCall, new HttpCallback<T>() {
            @Override
            public void onSuccess(Call<T> call, Response<T> response) {
                successCallback.onSuccess(call, response);
            }

            @Override
            public void onError(int responseCode, String errorMsg) {
                //Tst.warn(context, errorMsg);
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }, true, true, waitMessage, null);
    }

    final public <T> void goWait(Call<T> apiCall, final HttpSuccessCallback<T> successCallback) {
        goWait(apiCall, successCallback, null);
    }

    /*final public  <T> void goWaitToast(Call<T> apiCall, final String toastMsg) {
        goWait(apiCall, new HttpSuccessCallback<T>() {
            @Override
            public void onSuccess(Call<T> call, Response<T> response) {
                Tst.done(context, toastMsg);
            }
        }, null);
    }*/

    final public <T> void goNoResult(Call<T> apiCall) {
        goBackground(apiCall, new HttpSuccessCallback<T>() {
            @Override
            public void onSuccess(Call<T> call, Response<T> response) {}
        });
    }

    private String getErrorMessage(Response response) {
        String errMsg;
        try {
            errMsg = response.errorBody().string();
            if (StringUtil.isEmpty(errMsg)) {
                errMsg = String.format(Locale.getDefault(),
                        "code=%d, body=%s", response.code(), response.raw().message());
            }
        } catch (IOException e) {
            errMsg = "出错了!";
            e.printStackTrace();
        }
        return errMsg;
    }

    private String getErrorMessage(Throwable t) {
        String errMsg = t.toString();
        if (! NetUtils.isConnected(context)) { //没联网最常见的是返回DNS解析失败的Exception
            errMsg = Consts.PLEASE_CONNECT_NET;
        } else if (t instanceof SocketTimeoutException){
            errMsg = Consts.TIME_OUT;
        } else if (t instanceof JSONException) {
            errMsg = Consts.RESPONSE_INVALID;
        }
        return errMsg;
    }

    private <T> String logHttpError(Call<T> call, Response<T> response) {
        String errMsg = getErrorMessage(response);
        L.e(TAG, String.format(Locale.getDefault(), "http failed. url=[%s], reason=[%s]",
                call.request().url().toString(), errMsg));
        return errMsg;
    }

    private <T> String logHttpError(Call<T> call, Throwable t) {
        String errMsg = getErrorMessage(t);
        L.e(TAG, String.format(Locale.getDefault(), "http failed. url=[%s], reason=[%s]",
                call.request().url().toString(), errMsg));
        return errMsg;
    }

    final public <T> void goQuickToggle(final Call<T> apiCall, final ToggleCallback toggleCallback) {
        _go(apiCall, new HttpCallback<T>() {
            @Override
            public void onSuccess(Call<T> call, Response<T> response) {
            }

            @Override
            public void onError(int responseCode, String errorMsg) {
                //Tst.warn(context, errorMsg);
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();

                if (responseCode > 0) {
                    toggleCallback.doToggle();
                }
            }
        }, true, false, null, toggleCallback);
    }

    //todo: 应合并到 goBackground
    public <T> void go(final Call<T> apiCall, final HttpCallback<T> callback) {
        goBackground(apiCall, callback);
    }

    public <T> void go(final Call<T> apiCall, final HttpSuccessCallback<T> callback) {
        goBackground(apiCall, callback);
    }
}
