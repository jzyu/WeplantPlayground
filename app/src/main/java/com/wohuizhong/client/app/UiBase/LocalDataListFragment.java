package com.wohuizhong.client.app.UiBase;

import com.wohuizhong.client.app.http.WeplantService;

import java.util.List;

import retrofit2.Response;

public abstract class LocalDataListFragment<T> extends PtrListFragment<T> {

    final protected void onLoadData(WeplantService service, boolean isFirstLoad, final boolean isRefresh) {
        // do nothing
    }

    final protected List<T> onProvideItemsInResponse(Response response) {
        // do nothing
        return null;
    }

    final protected ConfigBuilder<T> getBuilder(int rowLayoutId, List<T> items) {
        return super.getBuilder(rowLayoutId, items).firstLoadFromLocal();
    }
}
