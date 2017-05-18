package com.nuoxian.kokojia.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.fragment.BindExistAccountFragment;
import com.nuoxian.kokojia.fragment.PerfectInfoFragment;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;

/**
 * 邮箱注册页面
 */
public class BindAccountActivity extends BaseActivity {

    private RadioGroup rg;
    private TextView tvEdit,tvBind;
    private PerfectInfoFragment infoFragment;
    private BindExistAccountFragment accountFragment;
    private RadioButton rbEdit,rbBind;
    private View lineEdit,lineBind;
    private String uid,nickname;
    public static Activity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_email_regist);

        instance = this;
        initView();
        uid = getIntent().getStringExtra("uid");
        nickname = getIntent().getStringExtra("nickname");
        //默认第一个被选中
        rbEdit.setChecked(true);
        tvEdit.setTextColor(0xfff9ac1d);
        tvBind.setTextColor(0xff999999);
        //设置写划线
        lineEdit.setVisibility(View.VISIBLE);
        lineBind.setVisibility(View.INVISIBLE);
        //显示第一个fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        infoFragment = new PerfectInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid",uid);
        bundle.putString("nickname",nickname);
        infoFragment.setArguments(bundle);
        ft.add(R.id.fl_bind_account,infoFragment);
        ft.commit();

        setListener();
    }

    private void initView(){
        Typeface typeface = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.layout_bind), typeface);
        //设置标题
        setTitle("绑定账号");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindAccountActivity.this.finish();
            }
        });
        rg = (RadioGroup) findViewById(R.id.rg_bind);
        rbEdit = (RadioButton) findViewById(R.id.rb_edit);
        rbBind = (RadioButton) findViewById(R.id.rb_bind);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        tvBind = (TextView) findViewById(R.id.tv_bind);
        lineEdit = findViewById(R.id.line_edit);
        lineBind = findViewById(R.id.line_bind);
    }

    private void setListener(){
        //fragment的切换
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //隐藏所有fragment
                hideAllFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (checkedId){
                    case R.id.rb_edit://完善信息
                        //设置下划线
                        lineEdit.setVisibility(View.VISIBLE);
                        lineBind.setVisibility(View.INVISIBLE);

                        tvEdit.setTextColor(0xfff9ac1d);
                        tvBind.setTextColor(0xff999999);
                        if(infoFragment==null){
                            infoFragment = new PerfectInfoFragment();
                            ft.add(R.id.fl_bind_account,infoFragment);
                        }else{
                            ft.show(infoFragment);
                        }
                        break;
                    case R.id.rb_bind://绑定已有账号
                        //设置下划线
                        lineEdit.setVisibility(View.INVISIBLE);
                        lineBind.setVisibility(View.VISIBLE);

                        tvEdit.setTextColor(0xff999999);
                        tvBind.setTextColor(0xfff9ac1d);
                        if(accountFragment==null){
                            accountFragment = new BindExistAccountFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("uid",uid);
                            accountFragment.setArguments(bundle);
                            ft.add(R.id.fl_bind_account,accountFragment);
                        }else{
                            ft.show(accountFragment);
                        }
                        break;
                    default:
                        break;
                }
                ft.commit();
            }
        });
    }

    /**
     * 隐藏fragment
     */
    private void hideAllFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(infoFragment!=null){
            ft.hide(infoFragment);
        }
        if(accountFragment!=null){
            ft.hide(accountFragment);
        }
        ft.commit();
    }

}

