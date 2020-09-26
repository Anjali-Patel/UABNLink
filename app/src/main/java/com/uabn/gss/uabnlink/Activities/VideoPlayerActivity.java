package com.uabn.gss.uabnlink.Activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.uabn.gss.uabnlink.R;

public class VideoPlayerActivity extends AppCompatActivity {
    private String url;
FrameLayout progressBarHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        progressBarHolder=findViewById(R.id.progressBarHolder);
        VideoView videoView =(VideoView)findViewById(R.id.videoView1);
        url = getIntent().getStringExtra("url");
        //Creating MediaController
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file
//        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/media/1.mp4");
        Uri uri=Uri.parse(url);
        progressBarHolder.setVisibility(View.VISIBLE);
        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
//        progressBarHolder.setVisibility(View.GONE);
        videoView.start();


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            progressBarHolder.setVisibility(View.GONE);

            }
        });

    }



}
