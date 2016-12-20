package com.example.jzyu.weplantplayground;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.jzyu.weplantplayground.bean.Size;
import com.example.jzyu.weplantplayground.util.ScreenTool;
import com.example.jzyu.weplantplayground.util.UrlUtil;
import com.wohuizhong.client.app.widget.ShortVideoView;
import com.zhy.utils.DensityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.feed_video)
    ShortVideoView feedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        //final String videoUrl = "http://fetchvideo.wohuizhong.cn/596580031-1482042533583-720x720-d-14.mp4";
        //final String thumbnailUrl = "http://fetchvideo.wohuizhong.cn/596580031-1482042533583-720x720-d-14.mp4?vframe/jpg/offset/5";

        final String videoUrl = "http://wohuizhong.cn/web-427255437-1-1481878781169-480x360-d-785.mp4";
        final String thumbnailUrl = "http://wohuizhong.cn/web-427255437-1-1481878781169-480x360-d-785.mp4?vframe/jpg/offset/5";

        Size size = calcExtendSize(UrlUtil.getSize(videoUrl).width, UrlUtil.getSize(videoUrl).height);
        long durationSec = UrlUtil.getVideoSeconds(videoUrl);

        feedVideo.setData(videoUrl, thumbnailUrl, size.width, size.height, durationSec);
    }

    private Size calcExtendSize(float width, float height) {
        final int IMAGE_MAX_WIDTH = ScreenTool.getScreenWidth(this) - 2 * DensityUtils.dp2px(this, 12);
        final int  IMAGE_MAX_HEIGHT = DensityUtils.dp2px(this, 300);

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

    public void onClickToolbarAutoHide(View view) {
        startActivity(new Intent(this, ToolbarHiddenOfScrollviewActivity.class));
    }

    public void onClickGestureNormal(View view) {
        startActivity(new Intent(this, GestureNormalActivity.class));
    }

    public void onClickGestureScrollView(View view) {
        startActivity(new Intent(this, GestureScrollViewActivity.class));
    }

    public void onClickPtrBubbleMsg(View view) {
        startActivity(new Intent(this, PtrBubbleMsgActivity.class));
    }

    public void onClickFeedVideoPlay(View view) {
        feedVideo.play();
    }
    public void onClickFeedVideoPause(View view) {
        feedVideo.pause();
    }
    public void onClickFeedVideoReplay(View view) {
        feedVideo.replay();
    }
}
