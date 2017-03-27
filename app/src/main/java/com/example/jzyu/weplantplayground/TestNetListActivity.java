package com.example.jzyu.weplantplayground;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.jzyu.weplantplayground.bean.ApiData;
import com.wohuizhong.client.app.UiBase.NetActivity;
import com.wohuizhong.client.app.UiBase.PtrListFragment;
import com.wohuizhong.client.app.UiBase.UiCommon;
import com.wohuizhong.client.app.http.WeplantService;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Response;

public class TestNetListActivity extends NetActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_net_list);
        ButterKnife.bind(this);
    }

    private ListFragment getListFragment() {
        return (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list_fragment);
    }

    public void onClickPreAppend(View view) {
        //getListFragment().onPreAppend();
    }

    public void onClickTestNormal(View view) {
        getListFragment().isSpecial = false;
        getListFragment().loadData();
    }

    public void onClickTestSpecial(View view) {
        getListFragment().isSpecial = true;
        getListFragment().loadData();
    }

    public static class ListFragment extends PtrListFragment<ApiData.Explore.Post> {

        boolean isSpecial = true;

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            init(getBuilder(R.layout.row_text_thin, new ArrayList<ApiData.Explore.Post>())
                    .enablePtr()
                    .enableLoadMore()
                    .dataSorted()
                    .build());

            recyclerView.addItemDecoration(UiCommon.newRvDivideLine(getContext()));
        }

        @Override
        public void onRowConvert(ViewHolder holder, ApiData.Explore.Post item, int position) {
            holder.setText(R.id.tv_text, item.title);
        }

        @Override
        protected void onLoadData(WeplantService service, boolean isFirstLoad, boolean isRefresh) {
            httpGo(service.getExplore(isRefresh ? 0 : getItemOfBottom().time), isFirstLoad, isRefresh, null);
        }

        @Override
        protected List<ApiData.Explore.Post> onProvideItemsInResponse(Response response) {
            ArrayList<ApiData.Explore.Post> items = ((Response<ApiData.Explore>) response).body().posts;

            if (isSpecial) {
                index++;
                return makeMore(items);
            } else {
                return items;
            }
        }

        private int index = 0;

        private List<ApiData.Explore.Post> makeMore(ArrayList<ApiData.Explore.Post> oldItems) {
            List<ApiData.Explore.Post> newItems = new ArrayList<>(oldItems);

            for (int i= 0; i < index; i++) {
                ApiData.Explore.Post item = new ApiData.Explore.Post();
                item.title = "test " + i;
                item.time = oldItems.get(0).time + i + 1;

                newItems.add(0, item);
            }

            return newItems;
        }
    }
}
