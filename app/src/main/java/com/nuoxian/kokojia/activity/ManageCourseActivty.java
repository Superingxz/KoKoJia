package com.nuoxian.kokojia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.fragment.MyCourseFragment;
import com.nuoxian.kokojia.fragment.RecycleBinFragment;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.TextChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 * 课程管理
 */
public class ManageCourseActivty extends BaseActivity {

    private RadioGroup rg;
    private ViewPager mViewPager;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_manage_course);
        //设置界面标题
        setTitle("课程管理");

        initView();
        initFragments();
        //加载viewpager
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        setListener();
    }

    private void initView(){
        rg = (RadioGroup) findViewById(R.id.rg_manage_course);
        mViewPager = (ViewPager) findViewById(R.id.vp_manage_course);
    }

    private void initFragments(){
        fragments = new ArrayList<>();
        fragments.add(new MyCourseFragment());
        fragments.add(new RecycleBinFragment());
    }

    /**
     * 设置监听
     */
    private void setListener(){
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageCourseActivty.this.finish();
            }
        });
        //fragment的切换
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //切换fragment
                changeFragment(checkedId);
            }
        });
        //ViewPager监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RadioButton rb = (RadioButton) rg.getChildAt(position);
                rb.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * fragment的切换
     */
    private void changeFragment(int checkedId){
        switch (checkedId) {
            case R.id.rb_my_course://显示我的课程
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rb_recycle_bin://显示回收站
                mViewPager.setCurrentItem(1);
                break;
        }
    }

}
