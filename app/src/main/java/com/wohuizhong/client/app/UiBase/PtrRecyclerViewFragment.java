package com.wohuizhong.client.app.UiBase;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jzyu.weplantplayground.R;
import com.wohuizhong.client.app.http.Api;
import com.wohuizhong.client.app.http.HttpCallback;
import com.wohuizhong.client.app.http.HttpLoadListCallback;
import com.wohuizhong.client.app.http.WeplantService;
import com.wohuizhong.client.app.util.Callback;
import com.wohuizhong.client.app.util.CollectionUtil;
import com.wohuizhong.client.app.util.Consts;
import com.wohuizhong.client.app.util.StringUtil;
import com.wohuizhong.client.app.util.UiFeatures;
import com.wohuizhong.client.app.util.WidgetUtil;
import com.wohuizhong.client.app.widget.LoadMoreFooterView;
import com.wohuizhong.client.app.widget.LoadingView;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.utils.L;
import com.zhy.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.finalteam.loadingviewfinal.HeaderAndFooterRecyclerViewAdapter;
import cn.finalteam.loadingviewfinal.OnLoadMoreListener;
import cn.finalteam.loadingviewfinal.RecyclerViewFinal;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 支持下拉刷新的 RecyclerView Fragment 基类
 */
public abstract class PtrRecyclerViewFragment<T> extends NetFragment
        implements UiFeatures.BackTopListener {

    public static final String TAG = PtrRecyclerViewFragment.class.getSimpleName();

    @Bind(R.id.recycler_view)
    protected RecyclerViewFinal recyclerView;
    @Bind(R.id.ptr_frame)
    protected PtrFrameLayout ptrFrame;
    @Bind(R.id.tv_empty_tip)
    protected TextView tvEmptyTip;

    private LoadingView loadingView = null;
    private Config<T> config;

    protected abstract void onLoadData(WeplantService service, boolean isFirstLoad, final boolean isRefresh);
    protected abstract List<T> onProvideItemsInResponse(Response response);

    protected interface RowConvert<T> {
        void onRowConvert(ViewHolder holder, final T item, int position);
    }

    public interface ListLoader<T> {
        List<T> onLoadList();
    }

    public interface CanDoRefreshChecker {
        boolean canDoRefresh();
    }

    public interface RefreshErrorTipper {
        void onTipError(String error);
    }

    protected enum LoadFrom {
        LOCAL, NETWORK, NETWORK_CACHE
    }

    protected static final class Config<T> {
        private List<T> items;

        private PtrUIHandler ptrUIHandler;
        private RecyclerView.LayoutManager layoutManager;
        private MultiItemTypeAdapter<T> adapter;

        private boolean enablePtr;
        private boolean enableLoadMore;
        private boolean disableNorMoreTip;
        private int dataPageLength;
        private String emptyTip = "暂无内容";
        private boolean dataSorted;

        private CanDoRefreshChecker canDoRefreshChecker;
        private RefreshErrorTipper refreshErrorTipper;

        private FirstLoadConfig<T> firstLoad = new FirstLoadConfig();

        private static class FirstLoadConfig<T> {
            private boolean disabled;
            private boolean thenRefresh = true;
            private LoadFrom from = LoadFrom.NETWORK_CACHE;
            private ListLoader<T> loader;  // exec in another thread
        }
    }

    protected static final class ConfigBuilder<T> {
        Config<T> config = new Config<>();

        public ConfigBuilder<T> items(List<T> items) {
            config.items = items;
            return this;
        }
        public ConfigBuilder<T> ptrUIHandler(PtrUIHandler ptrUIHandler) {
            config.ptrUIHandler = ptrUIHandler;
            return this;
        }
        public ConfigBuilder<T> layoutManager(RecyclerView.LayoutManager layoutManager) {
            config.layoutManager = layoutManager;
            return this;
        }
        public ConfigBuilder<T> adapter(MultiItemTypeAdapter<T> adapter) {
            config.adapter = adapter;
            return this;
        }
        public ConfigBuilder<T> firstLoadFromLocal() {
            config.firstLoad.from = LoadFrom.LOCAL;
            return this;
        }
        public ConfigBuilder<T> firstLoadAsync(ListLoader<T> loader) {
            config.firstLoad.loader = loader;
            return this;
        }
        public ConfigBuilder<T> disableNetworkCache() {
            config.firstLoad.from = LoadFrom.NETWORK;
            return this;
        }
        public ConfigBuilder<T> disableAutoLoad() {
            config.firstLoad.disabled = true;
            return this;
        }
        public ConfigBuilder<T> disableRefreshAfterFirstLoad() {
            config.firstLoad.thenRefresh = false;
            return this;
        }
        public ConfigBuilder<T> enablePtr() {
            config.enablePtr = true;
            return this;
        }
        public ConfigBuilder<T> enableLoadMore() {
            config.enableLoadMore = true;
            return this;
        }
        public ConfigBuilder<T> emptyTip(String emptyTip) {
            config.emptyTip = emptyTip;
            return this;
        }
        public ConfigBuilder<T> disableNoMoreTip() { // 加载完时不显示没有更多
            config.disableNorMoreTip = true;
            return this;
        }
        // 加载数据时一次返回的条数，如实际返回小于此值则已加载完 (没有更多)
        public ConfigBuilder<T> dataPageLength(int pageLength) {
            config.dataPageLength = pageLength;
            return this;
        }
        public ConfigBuilder<T> customCanDoRefresh(CanDoRefreshChecker checker) {
            config.canDoRefreshChecker = checker;
            return this;
        }
        public ConfigBuilder<T> customRefreshErrorTipper(RefreshErrorTipper tipper) {
            config.refreshErrorTipper = tipper;
            return this;
        }
        public ConfigBuilder<T> dataSorted() { // 下拉刷新时不清除已有数据，只添加新的
            config.dataSorted = true;
            return this;
        }
        public Config<T> build() {
            return config;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onCreateViewEx(0, inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    final protected View onCreateViewEx(int customLayoutId, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                customLayoutId > 0 ? customLayoutId : R.layout.common_ptr_recyclerview, container, false);
        ButterKnife.bind(this, view);
        loadingView = new LoadingView(getContext());
        return view;
    }

    final protected void init(final Config<T> config) {
        if (config.enablePtr && config.ptrUIHandler == null) {
            // 注意：因这行代码有副作用 mPtrFrameLayout.setRefreshCompleteHook(mPtrUIHandlerHook);
            // 导致设过 Material PtrUI 就不能换其他 PtrUI，否则 下拉刷新动画一直转；
            // 所以把它留做默认
            config.ptrUIHandler = WidgetUtil.newPtrUIMaterial(getContext(), ptrFrame);
        }

        this.config = config;

        // recyclerView
        recyclerView.setLayoutManager(config.layoutManager);
        recyclerView.setAdapter(config.adapter);

        // recyclerViewFinal
        tvEmptyTip.setText(config.emptyTip);
        recyclerView.setEmptyView(tvEmptyTip);
        if (config.disableNorMoreTip) {
            recyclerView.setNoLoadMoreHideView(true);
        }

        // 即使不启用 loadMore，也要设置 loadMoreFooter，因为要显示没有更多
        recyclerView.setLoadMoreView(new LoadMoreFooterView(getContext()));
        if (config.enableLoadMore) {
            recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void loadMore() {
                    L.v(TAG, "onLoadMore");
                    _loadData(false, false);
                }
            });
        }

        // ptrFrame
        if (config.enablePtr) {
            ptrFrame.setHeaderView((View) config.ptrUIHandler);
            ptrFrame.addPtrUIHandler(config.ptrUIHandler);
            ptrFrame.setPinContent(config.ptrUIHandler instanceof MaterialHeader);

            ptrFrame.setPtrHandler(new PtrDefaultHandler() {
                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    L.v(TAG, "PtrHandler: onRefreshBegin");
                    loadData();
                }

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    if (config.canDoRefreshChecker != null) {
                        return config.canDoRefreshChecker.canDoRefresh();
                    } else {
                        return super.checkCanDoRefresh(frame, content, header);
                    }
                }
            });
        }

        if (config.firstLoad.from == LoadFrom.LOCAL) {
            // 此方法放需在 setLoadMoreView 后面，否则刚添加的 footerView 又被移除了
            recyclerView.setHasMore(false);

            // 此行多余，且必须注释掉，否则图片流的详情界面转场动画失效（原因先显示 emptyView 了）
            //getRvAdapter().notifyDataSetChanged();
        } else {
            if (! config.firstLoad.disabled) {
                firstLoad();
            }
        }
    }

    private void firstLoad() {
        if (config.firstLoad.loader != null) {
            prepareLoadingView();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<T> items = config.firstLoad.loader.onLoadList();

                    if (null != items) {
                        getItems().addAll(items);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resetLoadingUi(true, false, null);
                            getRvAdapter().notifyDataSetChanged();

                            if (config.firstLoad.thenRefresh && NetUtils.isConnected(getContext())) {
                                postRefresh();
                            }
                        }
                    });
                }
            }).start();
        } else {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            });
        }
    }

    final public RecyclerView.Adapter<RecyclerView.ViewHolder> getRvAdapter() {
        return ((HeaderAndFooterRecyclerViewAdapter) (recyclerView.getAdapter())).getInnerAdapter();
    }

    private void prepareLoadingView() {
        if (! loadingView.isAttached()) {
            ptrFrame.setVisibility(View.INVISIBLE);
            tvEmptyTip.setVisibility(View.INVISIBLE);

            loadingView.attachToParent((ViewGroup) ptrFrame.getParent(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadingView.setStatusAsLoading();
                            loadData();
                        }
                    });
        }
    }

    protected void resetLoadingUi(boolean isRefresh, boolean hasMore, String errorText) {
        if (loadingView.isAttached()) {
            if (! StringUtil.isEmpty(errorText)) {
                tvEmptyTip.setVisibility(View.INVISIBLE);
                loadingView.setStatusAsRetry(errorText);
            } else {
                ptrFrame.setVisibility(View.VISIBLE);
                tvEmptyTip.setVisibility(View.VISIBLE);
                recyclerView.setHasMore(hasMore);

                loadingView.detach();
            }
            return;
        }

        // patch: 私聊界面 refresh 动画消不掉，因为是以 loadMore 方式 refresh，isRefresh = false
        isRefresh = ptrFrame.isRefreshing();

        if (isRefresh) {
            if (! StringUtil.isEmpty(errorText)) {
                tvEmptyTip.setText(errorText);

                if (config.refreshErrorTipper != null) {
                    config.refreshErrorTipper.onTipError(errorText);
                } else {
                    Toast.makeText(getContext(), errorText, Toast.LENGTH_SHORT).show();
                }
            }

            if (ptrFrame.isRefreshing()) {
                ptrFrame.refreshComplete();
            }
            recyclerView.setHasMore(hasMore);
        } else {
            recyclerView.onLoadMoreComplete(hasMore, errorText);
        }
    }

    public void postRefresh() {
        L.d(TAG, "postRefresh");
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (!ptrFrame.isRefreshing()) {
                    ptrFrame.autoRefresh();
                }
            }
        });
    }

    private static void trySmoothScrollToTop(RecyclerView rv) {
        if (rv.getLayoutManager() instanceof LinearLayoutManager
                && ((LinearLayoutManager) rv.getLayoutManager()).findFirstVisibleItemPosition() < 30) {
            rv.smoothScrollToPosition(0);
        } else {
            rv.scrollToPosition(0);
        }
    }

    private static void scrollTopIfTopVisible(RecyclerView rv) {
        if (rv.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();

            if (lm.findFirstVisibleItemPosition() == 0) {
                lm.scrollToPosition(0);
            }
        } else if (rv.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) rv.getLayoutManager();

            boolean topIsVisible = false;
            int positions[] = lm.findFirstVisibleItemPositions(null);
            for (int position: positions) {
                if (position == 0) {
                    topIsVisible = true;
                }
            }

            if (topIsVisible) {
                lm.scrollToPosition(0);
            }
        }
    }

    @Override
    public void onBackTop() {
        if (! WidgetUtil.isLinearRecyclerViewAtTop(recyclerView)) {
            trySmoothScrollToTop(recyclerView);
        } else {
            postRefresh();
        }
    }

    public void scrollToTop() {
        scrollTopThen(null);
    }

    public void scrollTopThen(final Callback.Simple listener) {
        if (WidgetUtil.isLinearRecyclerViewAtTop(recyclerView)) {
            if (listener != null)
                listener.onComplete();
        } else {
            trySmoothScrollToTop(recyclerView);

            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //L.v(TAG, "onComplete async.");
                    if (listener != null)
                        listener.onComplete();
                }
            }, 500);
        }
    }

    public void smoothScrollToPosition(int position) {
        if (position < 0) {
            position = getItems().size() - 1;
        }
        //recyclerView.scrollToPosition(position);
        recyclerView.smoothScrollToPosition(position);
    }

    public void scrollToBottom() {
        recyclerView.scrollToPosition(getItems().size() - 1);
    }

    public List<T> getItems() {
        return config.items;
    }

    public T getItemOfBottom() {
        if (CollectionUtil.isEmpty(getItems())) {
            return null;
        } else {
            return getItems().get(getItems().size() - 1);
        }
    }

    private boolean isFirstLoad = true;
    public void loadData() {
        _loadData(isFirstLoad, true);

        if (isFirstLoad) {
            isFirstLoad = false;
        }
    }

    private void _loadData(final boolean isFirstLoad, final boolean isRefresh) {
        if (isFirstLoad && isRefresh) {
            prepareLoadingView();
        }

        WeplantService service = Api.get();
        if (isFirstLoad && config.firstLoad.from == LoadFrom.NETWORK_CACHE) {
            service = Api.getCacheOnly();
        }

        onLoadData(service, isFirstLoad, isRefresh);
    }

    final protected <API_DATA_TYPE> void httpGo(final Call<API_DATA_TYPE> httpCall,
                                                final boolean isFirstLoad,
                                                final boolean isRefresh,
                                                final HttpLoadListCallback<API_DATA_TYPE, T> extraCallback)
    {
        httpGo(httpCall, isFirstLoad, isRefresh, extraCallback, false);
    }

    final protected <API_DATA_TYPE> void httpGo(final Call<API_DATA_TYPE> httpCall,
                                                final boolean isFirstLoad,
                                                final boolean isRefresh,
                                                final HttpLoadListCallback<API_DATA_TYPE, T> extraCallback,
                                                final boolean provideItemsInBackground)
    {
        http.go(httpCall, new HttpCallback<API_DATA_TYPE>() {
            public static final String TAG = "httpGo";

            @Override
            public void onSuccess(final Call<API_DATA_TYPE> call, final Response<API_DATA_TYPE> response) {
                if (provideItemsInBackground) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<T> newItems = provideItemsInResponseNoneNull(response);

                            getAty().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onNewData(response, newItems);
                                }
                            });
                        }
                    }).start();
                } else {
                    onNewData(response, provideItemsInResponseNoneNull(response));
                }
            }

            private void onNewData(Response<API_DATA_TYPE> response, List<T> newItems) {
                boolean hasMore = config.enableLoadMore && newItems.size() > 0;
                if (hasMore && config.dataPageLength > 0) {
                    hasMore = newItems.size() >= config.dataPageLength;
                }

                L.d(TAG, "onNewData: hasMore = " + hasMore);
                resetLoadingUi(isRefresh, hasMore, null);

                // update
                if (newItems.size() > 0) {
                    if (isRefresh) {
                        if (getItems().size() > 0 && config.dataSorted) {
                            int pos = -1;
                            if (getItems().size() > 0) {
                                pos = newItems.indexOf(getItems().get(0));
                            }

                            if (pos == 0) {
                                L.v(TAG, "data not changed.");
                            } else if (pos > 0) {
                                L.d(TAG, "find new item, count = " + pos);

                                getItems().addAll(0, newItems.subList(0, pos));
                                getRvAdapter().notifyItemRangeInserted(0, pos);

                                scrollTopIfTopVisible(recyclerView);
                            } else {
                                L.e(TAG, "data has all changed !");
                                updateAllData(newItems);
                            }
                        } else {
                            updateAllData(newItems);
                        }
                    } else {
                        // load more: append data
                        int tail = getItems().size();

                        getItems().addAll(newItems);
                        getRvAdapter().notifyItemRangeInserted(tail, newItems.size());
                    }
                }

                if (isFirstFromCache()
                        && config.firstLoad.thenRefresh
                        && NetUtils.isConnected(getContext())) {
                    postRefresh();
                }

                /*
                 * 数据个性化处理
                 */
                if (extraCallback != null) {
                    extraCallback.onSuccess(response, newItems);
                }
            }

            private boolean isFirstFromCache() {
                return isFirstLoad && config.firstLoad.from == LoadFrom.NETWORK_CACHE;
            }

            @Override
            public void onError(int responseCode, String errorMsg) {
                if (isFirstLoad && responseCode == Consts.HTTP_STATUS_CODE_CACHE_IS_EMPTY) {
                    loadData();
                } else {
                    resetLoadingUi(isRefresh, config.enableLoadMore, errorMsg);
                }
            }
        });
    }

    private void updateAllData(List<T> newItems) {
        getItems().clear();
        getItems().addAll(newItems);
        getRvAdapter().notifyDataSetChanged();
    }

    private List<T> provideItemsInResponseNoneNull(Response response) {
        List<T> items = onProvideItemsInResponse(response);
        if (items == null) {
            items = new ArrayList<>(0);
        }
        return items;
    }
}
