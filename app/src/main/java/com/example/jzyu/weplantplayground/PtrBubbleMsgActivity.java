package com.example.jzyu.weplantplayground;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.wohuizhong.client.app.util.SimpleListener;
import com.wohuizhong.client.app.util.StringUtil;
import com.wohuizhong.client.app.util.WidgetUtil;
import com.wohuizhong.client.app.widget.TitleBarView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.finalteam.loadingviewfinal.RecyclerViewFinal;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class PtrBubbleMsgActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    RecyclerViewFinal recyclerView;
    @Bind(R.id.tv_bubble)
    TextView tvBubble;
    @Bind(R.id.ptrFrame)
    PtrFrameLayout ptrFrame;
    @Bind(R.id.titlebar)
    TitleBarView titlebar;

    private String refreshBubbleMsg;
    private View topicHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_bubble_msg);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        String[] strings = new String[]{"one", "two", "three", "four", "five", "six"};

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        CommonAdapter<String> rvAdapter = new CommonAdapter<String>(this, R.layout.row_text, Arrays.asList(strings)) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setVisible(R.id.divider_list_top, position == 0);
                holder.setText(android.R.id.text1, s);
            }
        };

        recyclerView.setAdapter(rvAdapter);

        WidgetUtil.ptrAddUIHeader(this, ptrFrame, new SimpleListener() {
            @Override
            public void callback() {
                refreshBubbleMsg = "测试一下";
                makeBubbleMessage(refreshBubbleMsg);
            }
        }, R.color.divide_line, 4);

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

    private void makeBubbleMessage(String message) {
        if (topicHeader != null || StringUtil.isEmpty(message))
            return;

        tvBubble.setText(message);

        ObjectAnimator animator = ObjectAnimator
                .ofFloat(tvBubble, "translationY", -tvBubble.getHeight(), 0)
                .setDuration(600);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new WidgetUtil.AnimatorEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator animator = ObjectAnimator
                        .ofFloat(tvBubble, "translationY", 0, -tvBubble.getHeight())
                        .setDuration(2000);
                animator.setInterpolator(new AccelerateInterpolator(8.0f));
                animator.start();
            }
        });
        animator.start();
    }
}
