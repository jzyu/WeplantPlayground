package com.wohuizhong.client.app.http;

import java.util.List;

import retrofit2.Response;

/**
 * Author: jzyu
 * Date  : 2017/03/18
 */

public interface HttpLoadListCallback <API_DATA_TYPE, ITEM_TYPE> {
    void onSuccess(Response<API_DATA_TYPE> response, List<ITEM_TYPE> newItems);
}
