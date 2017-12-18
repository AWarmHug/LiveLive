package com.warm.playerlib.weight;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.warm.playerlib.controller.BasePlayController;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * 作者：warm
 * 时间：2017-12-18 11:00
 * 描述：
 */

public class JustVideoView extends FrameLayout implements BasePlayController.PlayControl {
    private static final String TAG = "JustVideoView";
    private IMediaPlayer mMediaPlayer;//ijkPlayer
    private TextureView mTextureView;

    private BasePlayController mController;

    private String urlPath;

    /**
     * 默认状态，既一开始的状态
     */
    public static final int STATE_DEFAULT = 0;

    /**
     * 出错
     */
    public static final int STATE_ERROR = -1;
    /**
     * 没有网络
     */
    public static final int STATE_NET_ERROR = -2;

    /**
     * 加载中
     */
    public static final int STATE_LONGING = 1;
    /**
     * 准备好的状态
     */
    public static final int STATE_PREPARE = 2;
    /**
     * 播放ing
     */
    public static final int STATE_PLAYING = 3;

    /**
     * 播放暂停
     */
    public static final int STATE_PAUSE = 4;

    /**
     * 播放完成
     */
    private static final int STATE_COMPLETION = 5;


    private int mState = STATE_DEFAULT;


    public JustVideoView(Context context) {
        this(context, null);
    }

    public JustVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JustVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPlayer();
        addTextureView();
    }


    public void addController(BasePlayController playController) {
        this.mController = playController;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        addView(playController, params);
        mController.setControl(this);
    }


    private void initPlayer() {
        mMediaPlayer = new IjkMediaPlayer();
        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//开启硬解码
        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnErrorListener(onErrorListener);
        mMediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        mMediaPlayer.setOnInfoListener(onInfoListener);
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
    }

    public void addTextureView() {


        mTextureView = new JustTextureView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d(TAG, "onSurfaceTextureAvailable: ");
                mMediaPlayer.setSurface(new Surface(surface));
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        addView(mTextureView, 0, params);
    }


    public void setDataSource(String urlPath) {
        this.urlPath = urlPath;

    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void start() {
        if (mState == STATE_DEFAULT) {
            setDataSourceAndPrepare();
        }
        mState = STATE_PLAYING;
        mMediaPlayer.start();
    }

    private void setDataSourceAndPrepare() {

        try {
            mMediaPlayer.setDataSource(urlPath);
            mMediaPlayer.prepareAsync();
            mState = STATE_DEFAULT;
        } catch (IOException e) {
            e.printStackTrace();
            mState = STATE_ERROR;
        }

    }

    @Override
    public void pause() {

        mMediaPlayer.pause();
        mState = STATE_PAUSE;

    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void seekTo(long seekTo) {
        Log.d(TAG, "seekTo: " + seekTo + "d=" + mMediaPlayer.getDuration());
        if (mState == STATE_PAUSE || mState == STATE_PLAYING) {
            mMediaPlayer.seekTo(seekTo);
        }
    }

    @Override
    public void toFull() {

    }

    @Override
    public void toNotFull() {

    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }


    /**
     * 播放完成的回调
     */
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            mState = STATE_COMPLETION;

        }
    };

    /**
     * 播放缓冲回调
     */
    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
            Log.d(TAG, "onBufferingUpdate: " + percent);

        }
    };

    /**
     * 播放出错回调
     */
    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
            //onError: i=-10000i1=0
            Log.d(TAG, "onError: i=" + what + "i1=" + extra);
            return false;
        }
    };
    /**
     * 播放视频信息回调
     */
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {

            Log.d(TAG, "onInfo: i=" + what + "i1=" + extra + "name=" + iMediaPlayer.getMediaInfo());

            return false;
        }
    };
    /**
     * 播放准备完毕回调
     */
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "onPrepared: ");
            mState = STATE_PREPARE;

        }
    };
    /**
     * 播放时间
     */
    private IMediaPlayer.OnTimedTextListener onTimedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
            Log.d(TAG, "onTimedText: " + ijkTimedText.getText());

        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height,
                                       int sar_num, int sar_den) {

        }
    };

    /**
     * 拖动后完成的回调
     */
    private IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {


        }
    };


}
