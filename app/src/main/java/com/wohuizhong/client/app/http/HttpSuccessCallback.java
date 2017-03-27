package com.wohuizhong.client.app.http;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by weplant on 16/4/15.
 */
public interface HttpSuccessCallback<T> {
    void onSuccess(Call<T> call, Response<T> response);
}
