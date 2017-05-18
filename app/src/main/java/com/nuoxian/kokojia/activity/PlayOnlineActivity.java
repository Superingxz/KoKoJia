package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.adapter.PlayCourseLessonAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.Play;
import com.nuoxian.kokojia.enterty.PlayLesson;
import com.nuoxian.kokojia.fragment.PlayerCatalogFragment;
import com.nuoxian.kokojia.fragment.PlayerDiscussFragment;
import com.nuoxian.kokojia.fragment.PlayerNoteFragment;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.UniversalMediaController;
import com.nuoxian.kokojia.utils.UniversalVideoView;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在线播放
 */
public class PlayOnlineActivity extends AutoLayoutActivity implements
        UniversalVideoView.VideoViewCallback,View.OnClickListener {

    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;
    View mBottomLayout;
    View mVideoLayout;
    private int mSeekPosition;
    private boolean isFirst = true;//是否为第一次设置播放器区域大小
    private int cachedHeight;
    private boolean isFullscreen;
    private RelativeLayout titleBar;
    private TextView ivBack,ivSearch;
    private String playUrl;
    private String id,lid,uid;
    private int page;
    private int position;//被选中的item的坐标
    private Handler handler;
    private ViewPager mViewPager;
    private List<Fragment> fragments;
    private RadioGroup mRadioGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_player);

        EventBus.getDefault().register(this);
        //获取所需的参数
        getValues();
        //初始化控件
        initView();
        initFragment();

        //设置播放区域大小
        setVideoAreaSize();
        //设置控制器
        mVideoView.setMediaController(mMediaController);
        //设置回调
        mVideoView.setVideoViewCallback(this);
        //设置播放完成的监听事件
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);
        //设置监听
        setListener();
    }

    private void initFragment(){
        fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        bundle.putString("id", id);
        bundle.putString("lid", lid);
        bundle.putInt("page", page);
        bundle.putInt("position", position);
        PlayerCatalogFragment catalogFragment = new PlayerCatalogFragment();
        catalogFragment.setArguments(bundle);
        fragments.add(catalogFragment);

        PlayerDiscussFragment discussFragment = new PlayerDiscussFragment();
        discussFragment.setArguments(bundle);
        fragments.add(discussFragment);

        PlayerNoteFragment noteFragment = new PlayerNoteFragment();
        noteFragment.setArguments(bundle);
        fragments.add(noteFragment);
    }

    /**
     * 获取所需的参数
     */
    private void getValues(){
        SharedPreferences sp = getSharedPreferences("flag", MODE_PRIVATE);
        uid = sp.getString("data", "0");
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        lid = intent.getStringExtra("lid");
        page = intent.getIntExtra("page", 1);
        position = intent.getIntExtra("position",1);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        mVideoLayout = findViewById(R.id.video_layout);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        titleBar = (RelativeLayout) findViewById(R.id.rl_play_title_bar);
        ivBack = (TextView) findViewById(R.id.iv_play_back);
        ivBack.setTypeface(iconFont);
        ivSearch = (TextView) findViewById(R.id.iv_play_search);
        ivSearch.setTypeface(iconFont);
        mViewPager = (ViewPager) findViewById(R.id.vp_play_content);
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_play);
    }

    /**
     * 设置监听事件
     */
    private void setListener(){
        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RadioButton rb = null;
                switch (position) {
                    case 0://目录
                        rb = (RadioButton) mRadioGroup.getChildAt(0);
                        rb.setChecked(true);
                        break;
                    case 1://讨论
                        rb = (RadioButton) mRadioGroup.getChildAt(2);
                        rb.setChecked(true);
                        break;
                    case 2://笔记
                        rb = (RadioButton) mRadioGroup.getChildAt(4);
                        rb.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_play_catalog://目录
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_play_discuss://讨论
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_play_note://笔记
                        mViewPager.setCurrentItem(2);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play_back://返回
                PlayOnlineActivity.this.finish();
                break;
            case R.id.iv_play_search://跳转到搜索页面
                CommonMethod.startActivity(this,SearchActivity.class);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mVideoView != null && mVideoView.isPlaying()) {
//            mSeekPosition = mVideoView.getCurrentPosition();
//        }
        mVideoView.pause();
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        mVideoView.start();
//    }

    /**
     * 置视频区域大小
     */
    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout
                        .getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
//                mVideoView.setVideoPath(playUrl);
                mVideoView.requestFocus();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存当前播放的位置
        outState.putInt(SEEK_POSITION_KEY, mVideoView.getCurrentPosition());
        //暂停播放，在清单文件中设置android:configChanges="orientation|screenSize"，横竖屏切换时activity不会销毁，达到现场保护的效果
        mVideoView.pause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        //取出播放的位置
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        //接着之前的开始播放
        mVideoView.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果为横屏
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            //隐藏标题栏
            titleBar.setVisibility(View.GONE);
        }
        //如果为竖屏
        else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        {
            //显示标题栏
            titleBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout
                    .getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);
            mViewPager.setVisibility(View.GONE);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout
                    .getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
            mViewPager.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收目录传过来的播放地址
     */
    public void onEventMainThread(Play play) {
        if("play".equals(play.getStatus())){
            mVideoView.setVideoPath(play.getPlayUrl());
        }else if("start".equals(play.getStatus())){
            mVideoView.start();
        }
    }

}
