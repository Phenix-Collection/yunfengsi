package com.maimaizu.Activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.maimaizu.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2017/4/10.
 */

public class Video_Detail extends AppCompatActivity {
    JCVideoPlayerStandard player;
    private boolean isPlaying=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.video_d);
        player= (JCVideoPlayerStandard) findViewById(R.id.player);
        String  url=getIntent().getStringExtra("url");
        String title=getIntent().getStringExtra("title");
        player.setUp(url, JCVideoPlayer.SCREEN_LAYOUT_NORMAL,title);

//        JCVideoPlayerStandard.startFullscreen(this,JCVideoPlayerStandard.class,url,title);

//        player.startWindowFullscreen();
//        player.backButton.setVisibility(View.GONE);
//        player.fullscreenButton.setVisibility(View.GONE);
        player.startButton.performClick();
        player.fullscreenButton.performClick();
        isPlaying=true;
//


    }



    @Override
    protected void onPause() {
        super.onPause();
//       JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isPlaying){
            player.startButton.performClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JCVideoPlayer.releaseAllVideos();
    }
}
