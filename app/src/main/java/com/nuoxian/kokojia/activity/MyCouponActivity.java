package com.nuoxian.kokojia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.fragment.MyCouponAllFragment;
import com.nuoxian.kokojia.fragment.MyCouponCanUseFragment;
import com.nuoxian.kokojia.fragment.MyCouponOverdueFragment;
import com.nuoxian.kokojia.fragment.MyCouponUsedFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的优惠券
 */
public class MyCouponActivity extends BaseActivity {

    private RadioGroup rg;
    private ViewPager mViewPager;
    private MyCouponAllFragment allFragment;
    private MyCouponCanUseFragment canUseFragment;
    private MyCouponUsedFragment usedFragment;
    private MyCouponOverdueFragment overdueFragment;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentView(R.layout.activity_my_coupon);
        //设置标题
        setTitle("我的优惠券");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCouponActivity.this.finish();
            }
        });

        initView();
        initFragments();

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        setListener();
    }

    private void initView(){
        rg = (RadioGroup) findViewById(R.id.rg_mycoupon);
        mViewPager = (ViewPager) findViewById(R.id.vp_mycoupon);
    }

    private void initFragments(){
        fragments = new ArrayList<>();
        fragments.add(new MyCouponAllFragment());
        fragments.add(new MyCouponCanUseFragment());
        fragments.add(new MyCouponUsedFragment());
        fragments.add(new MyCouponOverdueFragment());
    }

    private void setListener(){
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_mycoupon_all://全部
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_mycoupon_can_use://可使用
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_mycoupon_used://已使用
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_mycoupon_overdu://已过期
                        mViewPager.setCurrentItem(3);
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
