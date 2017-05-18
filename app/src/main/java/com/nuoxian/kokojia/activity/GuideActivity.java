package com.nuoxian.kokojia.activity;

/**
 * 引导页
 * Created by 陈思龙 on 2016/6/16.
 */
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.utils.CommonMethod;

import java.lang.reflect.Method;

public class GuideActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_guide);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonMethod.startActivity(GuideActivity.this,HomeActivity.class);
                GuideActivity.this.finish();
            }
        },3000);
    }
}
