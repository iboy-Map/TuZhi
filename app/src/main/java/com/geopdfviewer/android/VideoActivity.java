package com.geopdfviewer.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.geopdfviewer.android.R;

/**
 * Created by ZengHongHua on 2018/11/23.
 */
public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mHolder;
    private Button mBtnPlay;
    private RelativeLayout mParent;
    private SeekBar seekBar;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surface_view);
        initData();
    }

    private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if ( mMediaPlayer!= null && mMediaPlayer.isPlaying()) {
            // 设置当前播放的位置
                mMediaPlayer.seekTo(progress);
            }
        }
    };

    private void initData() {
        mBtnPlay = findViewById(R.id.test_btn_play);
        mSurfaceView = findViewById(R.id.test_surfaceView);
        mParent = findViewById(R.id.test_parent_play);
        //进度条

        seekBar = ((SeekBar) findViewById(R.id.seekBar));
        seekBar.setOnSeekBarChangeListener(change);

        mBtnPlay.setOnClickListener(this);

        mMediaPlayer = new MediaPlayer();
        //MediaController mediaController = new MediaController(this);
        mHolder = mSurfaceView.getHolder();
        mHolder.setKeepScreenOn(true);

        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //开始播放
                readyPlay();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                changeVideoSize(width,height);
            }
        });
    }

    //改变视频的尺寸自适应。
    public void changeVideoSize(int width, int height) {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();

        int surfaceWidth;
        int surfaceHeight;

        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float max;

        if (videoWidth > videoHeight) {
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
//        params.addRule(RelativeLayout.CENTER_VERTICAL, mParent.getId());
//        mSurfaceView.setLayoutParams(params);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        surfaceWidth = dm.widthPixels;
        surfaceHeight = dm.heightPixels;

        if (width > height) {
            // 竖屏录制的视频，调节其上下的空余
            int w = surfaceHeight * width / height;
            int margin = (surfaceWidth - w) / 2;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(margin, 0, margin, 0);
            mSurfaceView.setLayoutParams(lp);
        } else {
            // 横屏录制的视频，调节其左右的空余
            int h = surfaceWidth * height / width;
            int margin = (surfaceHeight - h) / 2;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, margin, 0, margin);
            mSurfaceView.setLayoutParams(lp);
        }
    }

    //准好播放了
    public void readyPlay() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("path");
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.setLooping(true);
        // 把视频画面输出到SurfaceView
        mMediaPlayer.setDisplay(mHolder);
        // 通过异步的方式装载媒体资源
        mMediaPlayer.prepareAsync();

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //装载完毕回调
                play();
            }
        });
    }

    /**
     * 播放或者暂停
     */
    private void play() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                isPlaying = mMediaPlayer.isPlaying();
                mMediaPlayer.pause();
                mBtnPlay.setText("播放");
            } else {
                isPlaying = mMediaPlayer.isPlaying();
                mMediaPlayer.start();

                // 设置进度条的最大进度为视频流的最大播放时长
//                seekBar.setMax(mMediaPlayer.getDuration());
//                // 开始线程，更新进度条的刻度
//                new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            isPlaying = true;
//                            while (isPlaying) {
//                                int current = mMediaPlayer
//                                        .getCurrentPosition();
//                                seekBar.setProgress(current);
//                                sleep(500);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();

                mBtnPlay.setText("暂停");
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.test_btn_play) {
            play();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                isPlaying = mMediaPlayer.isPlaying();
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

}