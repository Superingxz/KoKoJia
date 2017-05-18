package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.zhy.autolayout.AutoLayoutActivity;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.widget.media.IjkVideoView;

/**
 * 本地播放
 */
public class LocalPlayActivity extends AutoLayoutActivity {

    private IjkVideoView videoView;
    private String path;
    private static int GONE = 0;
    private static int VISIBLE = 1;
    private static final int SHOW = 1;
    private static final int HIDE = 0;
    private Handler handler;
    private ImageView ivBack;
    private TextView btnFrame;
    private RelativeLayout arlStart, arlBar;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //横屏显示
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_loacal_play);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHOW://显示
                        arlBar.setVisibility(View.VISIBLE);
                        arlBar.setTag(VISIBLE);
                        sendEmptyMessageDelayed(GONE, 5000);
                        break;
                    case HIDE://隐藏
                        arlBar.setVisibility(View.GONE);
                        arlBar.setTag(GONE);
                        break;
                }
            }
        };

        //获取播放地址
        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        initView();
        //初始化video
        initVideo();
    }

    private void initView() {
        videoView = (IjkVideoView) findViewById(R.id.ijkPlayer);
        //隐藏
        arlBar = (RelativeLayout) findViewById(R.id.arl_local_play_bar);
        arlBar.setVisibility(View.GONE);
        arlBar.setTag(GONE);

        arlStart = (RelativeLayout) findViewById(R.id.arl_local_play_start);
        //设置播放按钮的监听
        arlStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arlStart.setVisibility(View.GONE);
                videoView.start();
            }
        });

        ivBack = (ImageView) findViewById(R.id.iv_local_play_back);
        //设置返回按钮的监听
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalPlayActivity.this.finish();
            }
        });

        btnFrame = (TextView) findViewById(R.id.btn_local_play_frame);
        btnFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.toggleAspectRatio();
            }
        });
    }

    /**
     * 初始化video
     */
    private void initVideo() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        AndroidMediaController controller = new AndroidMediaController(this, false);
        videoView.setMediaController(controller);
        videoView.setVideoURI(Uri.parse(path));
    }

    /**
     * 触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Message msg = Message.obtain();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候,判断状态
            if (GONE == ((int) arlBar.getTag())) {
                //如果是隐藏的状态就显示按钮
                msg.what = SHOW;
                handler.sendMessage(msg);
            } else if (VISIBLE == ((int) arlBar.getTag())) {
                //如果是显示的状态就隐藏按钮
                msg.what = HIDE;
                handler.sendMessage(msg);
                handler.removeMessages(SHOW);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
            //保存当前的播放位置
            currentPosition = videoView.getCurrentPosition();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (videoView != null) {
            videoView.seekTo(currentPosition);
            videoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release(true);
    }
}
