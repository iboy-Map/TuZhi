package com.geopdfviewer.android;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

public class VideoView extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    android.widget.VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoview);

        videoView=findViewById(R.id.video);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏显示

        /** 加载播放视频 **/

        Intent intent = getIntent();
        String url = intent.getStringExtra("path");

        File file=new File(url);
        //获取视频文件对象
        if(file.exists()){
            videoView.setVideoPath(file.getAbsolutePath());//指定要播放的视频
        }else{
            Toast.makeText(VideoView.this, "文件不存在", Toast.LENGTH_SHORT).show();
        }
        //控制视频播放
        MediaController mc = new MediaController(VideoView.this);
        videoView.setMediaController(mc);//让VideoView与MediaControl关联

        videoView.requestFocus();//让VideoView获取焦点
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(VideoView.this, "播放完毕", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
