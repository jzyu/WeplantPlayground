package com.wohuizhong.client.app.http;

import com.example.jzyu.weplantplayground.bean.ApiData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by weplant on 16/4/18.
 */
public interface WeplantService {
    @GET("api/explore/v2/{fromTimestamp}")
    Call<ApiData.Explore> getExplore(@Path("fromTimestamp") long fromTimestamp);
}
