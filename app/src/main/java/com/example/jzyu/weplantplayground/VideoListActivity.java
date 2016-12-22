package com.example.jzyu.weplantplayground;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.jzyu.weplantplayground.bean.Size;
import com.example.jzyu.weplantplayground.util.ScreenTool;
import com.example.jzyu.weplantplayground.util.UrlUtil;
import com.waynell.videolist.visibility.calculator.SingleListViewItemActiveCalculator;
import com.waynell.videolist.visibility.items.ListItem;
import com.waynell.videolist.visibility.scroll.ItemsProvider;
import com.waynell.videolist.visibility.scroll.RecyclerViewItemPositionGetter;
import com.wohuizhong.client.app.widget.ShortVideoView;
import com.wohuizhong.client.app.widget.TitleBarView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.utils.DensityUtils;
import com.zhy.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.finalteam.loadingviewfinal.RecyclerViewFinal;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class VideoListActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    RecyclerViewFinal recyclerView;
    @Bind(R.id.ptrFrame)
    PtrFrameLayout ptrFrame;
    @Bind(R.id.titlebar)
    TitleBarView titlebar;

    private List<String> videoUrls;

    private MyAdapter rvAdapter;
    private SingleListViewItemActiveCalculator rvVisibleCalc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_bubble_msg);
        ButterKnife.bind(this);

        videoUrls = makeMockData();
        initView();
    }

    private List<String> makeMockData() {
        final String URL1 = "http://wohuizhong.cn/iOS-4-3-1482209651-320x568-d-10.mov?vframe/jpg/offset/5";
        final String URL2 = "http://fetchvideo.wohuizhong.cn/450881056-1482208481076-720x720-d-11.mp4?vframe/jpg/offset/5";
        final String URL3 = "http://wohuizhong.cn/web-247-1-1482167497981-640x360-d-443.mp4?vframe/jpg/offset/5";
        final String URL4 = "http://fetchvideo.wohuizhong.cn/6349-1482158470519-720x720-d-20.mp4?vframe/jpg/offset/5";

        List<String> videoUrls = new ArrayList<>();
        String[] urlArray = new String[] {URL1,URL2,URL3,URL4};

        for (int i = 0; i < 50; i++) {
            for (String url: urlArray) {
                videoUrls.add(url);
            }
        }

        return videoUrls;
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        rvAdapter = new MyAdapter(this, R.layout.row_video, videoUrls);
        recyclerView.setAdapter(rvAdapter);

        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (!recyclerView.canScrollVertically(-1)) {
                    return super.checkCanDoRefresh(frame, content, header);
                } else {
                    return false;
                }
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrame.refreshComplete();
                    }
                }, 2000);
            }
        });

        titlebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRefresh();
            }
        });

        rvVisibleCalc = new SingleListViewItemActiveCalculator(rvAdapter,
                new RecyclerViewItemPositionGetter(layoutManager, recyclerView));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                scrollState = newState;
                if(newState == RecyclerView.SCROLL_STATE_IDLE && rvAdapter.getItemCount() > 0){
                    rvVisibleCalc.onScrollStateIdle();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                rvVisibleCalc.onScrolled(scrollState);
            }
        });
    }

    private void postRefresh() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (! ptrFrame.isRefreshing()) {
                    ptrFrame.autoRefresh();
                }
            }
        });
    }

    private class MyAdapter extends CommonAdapter<String> implements ItemsProvider {
        public final String TAG = MyAdapter.class.getSimpleName();

        public MyAdapter(Context context, int layoutId, List<String> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, String s, int position) {
            final ShortVideoView videoView = holder.getView(R.id.short_video_view);

            String thumbnailUrl = s;
            String videoUrl = s.substring(0, s.indexOf("?"));

            Size size = calcExtendSize(UrlUtil.getSize(videoUrl).width, UrlUtil.getSize(videoUrl).height);
            videoView.setData(videoUrl, thumbnailUrl, size.width, size.height, UrlUtil.getVideoSeconds(videoUrl));
        }

        @Override
        public ListItem getListItem(int position) {
            return listItem;
        }

        @Override
        public int listItemSize() {
            return videoUrls.size();
        }

        private Size calcExtendSize(float width, float height) {
            final int IMAGE_MAX_WIDTH = ScreenTool.getScreenWidth((Activity) mContext) - 2 * DensityUtils.dp2px(mContext, 12);
            final int  IMAGE_MAX_HEIGHT = DensityUtils.dp2px(mContext, 300);

            float ratio = width / height;

            //固定为最大宽度
            width = IMAGE_MAX_WIDTH;
            height = width / ratio;

            //高度超出，则等比率缩小
            if (height > IMAGE_MAX_HEIGHT) {
                height = IMAGE_MAX_HEIGHT;
                width = height * ratio;
            }

            return new Size((int) width, (int) height);
        }

        private ListItem listItem = new ListItem() {
            @Override
            public void setActive(View newActiveView, int newActiveViewPosition) {
                L.d(TAG, "setActive, pos = " + newActiveViewPosition);
                getVideoView(newActiveView).play();
            }

            @Override
            public void deactivate(View currentView, int position) {
                L.d(TAG, "deactivate, pos = " + position);
                getVideoView(currentView).pause();
            }

            private ShortVideoView getVideoView(View view) {
                ViewHolder holder = (ViewHolder) recyclerView.getChildViewHolder(view);
                final ShortVideoView videoView = holder.getView(R.id.short_video_view);
                return videoView;
            }
        };
    }
}
