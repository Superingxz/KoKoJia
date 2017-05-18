package com.nuoxian.kokojia.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.fragment.MyPackageFragment;
import com.nuoxian.kokojia.fragment.MyPackageRecycleBinFragment;

import java.util.ArrayList;
import java.util.List;

public class ManagePackageActivity extends BaseActivity {

    private RadioGroup rg;
    private ViewPager mViewPager;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_manage_package);
        //设置标题
        setTitle("套餐管理");

        initView();
        initFragments();

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        setListener();
    }

    private void initView(){
        rg = (RadioGroup) findViewById(R.id.rg_manage_package);
        mViewPager = (ViewPager) findViewById(R.id.vp_manage_package);
    }

    private void initFragments(){
        fragments = new ArrayList<>();
        fragments.add(new MyPackageFragment());
        fragments.add(new MyPackageRecycleBinFragment());
    }

    /**
     * 设置监听
     */
    private void setListener(){
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManagePackageActivity.this.finish();
            }
        });
        //切换fragment
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //隐藏fragment
                switch (checkedId) {
                    case R.id.rb_my_package://我的套餐
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_recycle_bin://回收站
                        mViewPager.setCurrentItem(1);
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
                RadioButton rb = (RadioButton) rg.getChildAt(position);
                rb.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
