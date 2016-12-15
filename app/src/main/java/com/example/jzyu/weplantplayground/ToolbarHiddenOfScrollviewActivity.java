package com.example.jzyu.weplantplayground;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.jzyu.weplantplayground.util.ScrollDetector;
import com.wohuizhong.client.app.util.WidgetUtil;
import com.wohuizhong.client.app.widget.TitleBarView;
import com.zhy.utils.L;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ToolbarHiddenOfScrollviewActivity extends AppCompatActivity implements ScrollDetector{

    public static final float SCROLL_THRESHOLD = 10f;

    @Bind(R.id.titlebar)
    TitleBarView titlebar;
    @Bind(R.id.scroll_view)
    ScrollView scrollView;
    @Bind(R.id.container_bottom_bar)
    LinearLayout containerBottomBar;

    private OperationBar topBar;
    private OperationBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_hidden_of_scrollview);
        ButterKnife.bind(this);

        topBar = new TopBar(titlebar);
        bottomBar = new BottomBar(containerBottomBar);

        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(this, new MyGestureListener(this));
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    @Override
    public void onScrollUp(float distance) {
        if (Math.abs(distance) < SCROLL_THRESHOLD)
            return;

        if (topBar.isShown()) {
            topBar.hide();
            bottomBar.hide();
        }
    }

    @Override
    public void onScrollDown(float distance) {
        if (Math.abs(distance) < SCROLL_THRESHOLD)
            return;

        if (topBar.isHidden()) {
            topBar.show();
            bottomBar.show();
        }
    }

    abstract class OperationBar {
        protected View view;

        abstract void onAnimateValueUpdate(float value);
        abstract boolean isShown();
        abstract boolean isHidden();

        public OperationBar(View view) {
            this.view = view;
        }

        public void show() {
            move(true);
        }

        public void hide() {
            move(false);
        }

        public void move(boolean isShow) {
            ValueAnimator animator;
            if (isShow) {
                animator = ValueAnimator.ofFloat(-view.getHeight(), 0);
            } else {
                animator = ValueAnimator.ofFloat(0, -view.getHeight());
            }
            animator.setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    onAnimateValueUpdate(value);
                }
            });
            animator.start();
        }
    }

    private class TopBar extends OperationBar {
        public TopBar(View view) {
            super(view);
        }

        @Override
        public boolean isShown() {
            return WidgetUtil.getMarginTop(view) == 0;
        }

        @Override
        public boolean isHidden() {
            return (WidgetUtil.getMarginTop(view) == -view.getHeight());
        }

        @Override
        void onAnimateValueUpdate(float value) {
            WidgetUtil.setMarginTop(view, (int) value);
        }
    }

    private class BottomBar extends OperationBar {
        public BottomBar(View view) {
            super(view);
        }

        @Override
        public boolean isShown() {
            return WidgetUtil.getMarginBottom(view) == 0;
        }

        @Override
        public boolean isHidden() {
            return (WidgetUtil.getMarginBottom(view) == -view.getHeight());
        }

        @Override
        void onAnimateValueUpdate(float value) {
            WidgetUtil.setMarginBottom(view, (int) value);
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        public final String TAG = MyGestureListener.class.getSimpleName();
        private ScrollDetector scrollDetector;

        public MyGestureListener(ScrollDetector scrollDetector) {
            this.scrollDetector = scrollDetector;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) > Math.abs(distanceX)) {
                boolean isScrollDown = e1.getRawY() < e2.getRawY();
                if (isScrollDown) {
                    L.d(TAG, String.format("onScrollDown(%f)", distanceY));
                    scrollDetector.onScrollDown(distanceY);
                } else  {
                    L.d(TAG, String.format("onScrollUp(%f)", distanceY));
                    scrollDetector.onScrollUp(distanceY);
                }
            }

            return false;
        }
    }
}
