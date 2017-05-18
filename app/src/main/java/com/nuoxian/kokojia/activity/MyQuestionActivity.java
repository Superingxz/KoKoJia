package com.nuoxian.kokojia.activity;

/**
 * 我的提问页面
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.fragment.MyQuestionFragment;
import com.nuoxian.kokojia.fragment.MyReplyFragment;

import java.util.ArrayList;
import java.util.List;

public class MyQuestionActivity extends BaseActivity {

    private RadioGroup rg;
    private ViewPager mViewPager;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_my_question);
        //设置标题
        setTitle("我的提问");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyQuestionActivity.this.finish();
            }
        });
        //初始化视图
        initView();
        initFragments();

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        setListener();
    }

    private void initView(){
        rg = (RadioGroup) findViewById(R.id.rg_my_question);
        mViewPager = (ViewPager) findViewById(R.id.vp_my_question);

    }

    private void initFragments(){
        fragments = new ArrayList<>();
        fragments.add(new MyQuestionFragment());
        fragments.add(new MyReplyFragment());
    }

    private void setListener(){
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_my_question://我的提问
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_my_reply://我的回复
                        mViewPager.setCurrentItem(1);
                        break;
                    default:
                        break;
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==1){
                    position = 2;
                }
                RadioButton rb = (RadioButton) rg.getChildAt(position);
                rb.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
