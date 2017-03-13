package com.example.jzyu.weplantplayground;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.wohuizhong.client.app.util.Listener;
import com.wohuizhong.client.app.util.SimpleListener;
import com.wohuizhong.client.app.util.StringUtil;
import com.wohuizhong.client.app.util.WidgetUtil;
import com.wohuizhong.client.app.widget.DrawableItemDecoration;
import com.wohuizhong.client.app.widget.MaskItemDecoration;
import com.wohuizhong.client.app.widget.TitleBarView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.utils.DensityUtils;
import com.zhy.utils.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.finalteam.loadingviewfinal.HeaderAndFooterRecyclerViewAdapter;
import cn.finalteam.loadingviewfinal.RecyclerViewFinal;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class PtrBubbleMsgActivity extends AppCompatActivity {

    public static final String TAG = PtrBubbleMsgActivity.class.getSimpleName();

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
    private List<String> texts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_bubble_msg);
        ButterKnife.bind(this);

        texts.addAll(Arrays.asList("one"/*, "two", "three", "four", "five", "six"*/));

        initRecyclerView();

        titlebar.setRightText("add");
        titlebar.setLeftRightClickListener(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                texts.add("new at pos = " + texts.size());
                recyclerView.getAdapter().notifyItemInserted(texts.size());
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        CommonAdapter<String> rvAdapter = new CommonAdapter<String>(this, R.layout.row_text, texts) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                //holder.setVisible(R.id.divider_list_top, position == 0);
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
        }, 0, 4);

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
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        recyclerView.addItemDecoration(new DrawableItemDecoration(this,
                true,
                R.drawable.rect_grey1,
                DensityUtils.dp2px(this, 12),
                0, true, false));
        /*recyclerView.addItemDecoration(new DrawableItemDecoration(this,
                true,
                R.color.divide_line,
                DensityUtils.dp2px(this, 1),
                DensityUtils.dp2px(this, 12), false, true));*/

        recyclerView.addItemDecoration(new MaskItemDecoration(this, R.color.mask_light_green,
                new MaskItemDecoration.CheckListener() {
                    @Override
                    public boolean onCheck(int position) {
                        return (position == 1 || position == 3);
                    }
                }));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            String itemMove = texts.get(texts.size() - 1);

            texts.remove(texts.size() - 1);
            texts.add(0, itemMove);

            scrollTopThen(new Listener.Simple() {
                @Override
                public void onComplete() {
                    //getRvAdapter().notifyDataSetChanged();
                    //getRvAdapter().notifyItemMoved(texts.size() - 1, 0);
                    getRvAdapter().notifyDataSetChanged();
                    //recyclerView.scrollToPosition(0);
                }
            });

            //getRvAdapter().notifyItemMoved(texts.size() - 1, 0);
            //recyclerView.getAdapter().notifyItemMoved(texts.size() - 1, 0);
            //Toast.makeText(PtrBubbleMsgActivity.this, "move end to top", Toast.LENGTH_SHORT).show();
        }

        return super.onKeyDown(keyCode, event);
    }

    final public RecyclerView.Adapter<RecyclerView.ViewHolder> getRvAdapter() {
        return ((HeaderAndFooterRecyclerViewAdapter) (recyclerView.getAdapter())).getInnerAdapter();
    }

    public void scrollTopThen(final Listener.Simple listener) {
        if (WidgetUtil.isLinearRecyclerViewAtTop(recyclerView)) {
            L.v(TAG, "already at top, onComplete synced.");
            listener.onComplete();
        } else {
            WidgetUtil.linearRvScrollToTop(recyclerView);
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    L.v(TAG, "onComplete async.");
                    listener.onComplete();
                }
            }, 2000);
        }
    }
}
