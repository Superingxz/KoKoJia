package com.nuoxian.kokojia.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * 登录和注册页面
 */
public class LoginAndRegistActivity extends AutoLayoutActivity implements View.OnClickListener {

    private Button btnLogin,btnRegist;
    private TextView ibBack;
    public static Activity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_regist);
        //设置标题栏的颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        instance = this;
        initView();
    }

    private void initView(){
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        btnLogin = (Button) findViewById(R.id.btn_to_login);
        btnRegist = (Button) findViewById(R.id.btn_to_regist);
        ibBack = (TextView) findViewById(R.id.ib_back);
        ibBack.setTypeface(iconFont);

        btnLogin.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
        ibBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_to_login://登录
                CommonMethod.startActivity(this,LoginActivity.class);
                break;
            case R.id.btn_to_regist://注册
                CommonMethod.startActivity(this,RegistActivity.class);
                break;
            case  R.id.ib_back://返回
                this.finish();
                break;
        }
    }
}
