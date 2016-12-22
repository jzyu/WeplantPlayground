package com.wohuizhong.client.app.widget;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.jzyu.weplantplayground.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wohuizhong.client.app.util.FrescoUtil;
import com.wohuizhong.client.app.util.StringUtil;
import com.wohuizhong.client.app.util.WidgetUtil;
import com.zhy.utils.L;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Author: jzyu
 * Date  : 2016/04/26
 */
public class ShortVideoView extends FrameLayout {
    public static final String TAG = "ShortVideoView";

    @Bind(R.id.video_view)
    VideoView videoView;
    @Bind(R.id.drawee_thumbnail)
    SimpleDraweeView thumbnail;
    @Bind(R.id.playing_anim)
    SimpleDraweeView playingAnim;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.pb_loading)
    ProgressBar pbLoading;
    @Bind(R.id.iv_control)
    ImageView ivControl;

    private String videoUrl;
    private String thumbnailUrl;
    private int width;
    private int height;
    private long totalSeconds;
    private long currentSeconds;

    private PlayStatus status;
    private int systemPausePosition;

    private enum ControlType {
        HIDE, PLAY, REPLAY
    }

    private enum PlayStatus {
        IDLE,
        PLAY_LOADING,
        PLAYING,
        PAUSE,
        END
    }

    public ShortVideoView(Context context) {
        this(context, null);
    }

    public ShortVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShortVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate and attach the content
        View v = inflater.inflate(R.layout.widget_short_video_view, this);
        ButterKnife.bind(v);
    }

    private void initView() {
        WidgetUtil.resize(videoView, width, height);
        WidgetUtil.resize(thumbnail, width, height);

        int size = width < height ? width : height;
        WidgetUtil.resize(ivControl, (int) (size * 0.25) , (int) (size * 0.25));
        WidgetUtil.resize(pbLoading, (int) (size * 0.35) , (int) (size * 0.35));

        ivControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == PlayStatus.END) {
                    replay();
                }
            }
        });
    }

    private void showControl(ControlType controlType) {
        int iconId;

        switch (controlType) {
            default:
            case HIDE:
                iconId = 0;
                break;
            case PLAY:
                iconId = R.drawable.video_play;
                break;
            case REPLAY:
                iconId = R.drawable.video_replay;
                break;
        }

        ivControl.setVisibility(iconId > 0 ? VISIBLE : INVISIBLE);
        if (iconId > 0) {
            ivControl.setImageResource(iconId);
        }
    }

    private void setStatus(PlayStatus status) {
        if (this.status == status) {
            L.d(TAG, "status repeat = " + status);
            return;
        }

        L.d(TAG, "enterStatus = " + status);
        this.status = status;

        switch (status) {
            case IDLE:
                enterStatusIdle();
                break;

            case PLAY_LOADING:
                enterStatusPlayLoading();
                break;

            case PLAYING:
                enterStatusPlaying();
                break;

            case PAUSE:
                enterStatusPause();
                break;

            case END:
                enterStatusEnd();
                break;
        }
    }

    private void enterStatusPause() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }

        playingAnim.setVisibility(INVISIBLE);
        tvTime.setVisibility(INVISIBLE);
        pbLoading.setVisibility(INVISIBLE);
        showControl(ControlType.PLAY);
    }

    private void showThumbnail() {
        playingAnim.setVisibility(INVISIBLE);
        tvTime.setVisibility(INVISIBLE);
        pbLoading.setVisibility(INVISIBLE);

        thumbnail.setVisibility(VISIBLE);
    }

    private void enterStatusIdle() {
        initVideoView();
        thumbnail.setImageURI(Uri.parse(thumbnailUrl));
        showThumbnail();
        showControl(ControlType.PLAY);
    }

    private void enterStatusEnd() {
        showThumbnail();
        showControl(ControlType.REPLAY);
    }

    private void updateTime() {
        tvTime.setText(StringUtil.tsToHuman3(currentSeconds * 1000));
    }

    private void enterStatusPlaying() {
        pbLoading.setVisibility(INVISIBLE);
        thumbnail.setVisibility(INVISIBLE);
        showControl(ControlType.HIDE);

        playingAnim.setVisibility(VISIBLE);
        FrescoUtil.setLocalGif(playingAnim, R.raw.audio_animation);

        tvTime.setVisibility(VISIBLE);
        currentSeconds = totalSeconds;
        updateTime();
    }

    private void enterStatusPlayLoading() {
        // prepare ui
        playingAnim.setVisibility(INVISIBLE);
        tvTime.setVisibility(INVISIBLE);

        pbLoading.setVisibility(VISIBLE);
        showControl(ControlType.HIDE);

        videoView.start();
    }

    private void initVideoView() {
        //videoView.setVideoPath(VideoCache.getVideoProxy(getContext()).getProxyUrl(videoUrl));
        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                L.d(TAG, "oPrepared");
                mp.setLooping(false);
                mp.setVolume(0, 0);

                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        L.v(TAG, " MediaPlayer - onBufferingUpdate, percent = " + percent + ", videoUrl = " + videoUrl);
                    }
                });

                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        L.v(TAG, " MediaPlayer - onInfo, what = " + what);
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                                if (status == PlayStatus.PLAYING) {
                                    setStatus(PlayStatus.PLAY_LOADING);
                                }
                                break;

                            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                                if (status == PlayStatus.PLAY_LOADING) {
                                    setStatus(PlayStatus.PLAYING);
                                }
                                break;
                        }
                        return false;
                    }
                });
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                L.e(TAG, "onError url = " + videoUrl);
                setStatus(PlayStatus.IDLE);
                return true;
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                L.d(TAG, "onCompletion");
                setStatus(PlayStatus.END);
            }
        });
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        L.d(TAG, "onWindowVisibilityChanged() visibility = " + visibility);

        if (visibility == VISIBLE) {
            if (systemPausePosition > 0) {
                videoView.seekTo(systemPausePosition);
                systemPausePosition = 0;
                play();
            }
        } else {
            if (status == PlayStatus.PLAY_LOADING || status == PlayStatus.PLAYING) {
                systemPausePosition = videoView.getCurrentPosition();
                pause();
            }
        }
    }

    public void setData(String videoUrl, String thumbnailUrl, int width, int height, long durationSeconds) {
        L.d(TAG, "setData");
        if ((! StringUtil.isEmpty(this.videoUrl)) && videoUrl.equalsIgnoreCase(videoUrl)) {
            L.e(TAG, "videoUrl equal.");
            return;
        }

        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.width = width;
        this.height = height;
        this.totalSeconds = durationSeconds;

        initView();
        setStatus(PlayStatus.IDLE);
    }

    public void replay() {
        videoView.seekTo(0);
        play();
    }

    public void play() {
        setStatus(PlayStatus.PLAY_LOADING);
        if (videoView.isPlaying()) {
            setStatus(PlayStatus.PLAYING);
        }
    }

    public void pause() {
        setStatus(PlayStatus.PAUSE);
    }
}
