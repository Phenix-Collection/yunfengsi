package com.demo;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.video);
        videoView.setVideoURI(Uri.parse("http://9445.long-vod.cdn.aodianyun.com/u/9445/mp4/0x0/f319576218aec1996139ab6ef44600bc.mp4"));
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);


        JCVideoPlayerStandard jc= (JCVideoPlayerStandard) findViewById(R.id.jcvideo);
        jc.setUp("http://9445.long-vod.cdn.aodianyun.com/u/9445/mp4/0x0/f319576218aec1996139ab6ef44600bc.mp4","测试");
        jc.coverImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.mine_gerenbeijing));
        jc.thumbImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.loading3));
    }
}
