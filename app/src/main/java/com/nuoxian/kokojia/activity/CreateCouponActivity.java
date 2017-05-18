package com.nuoxian.kokojia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.fragment.TeacherCouponAllFragment;
import com.nuoxian.kokojia.fragment.TeacherCouponInsideFragment;
import com.nuoxian.kokojia.fragment.TeacherCouponOutsideFragment;
import com.nuoxian.kokojia.fragment.TeacherCouponOverduedFragment;
import com.nuoxian.kokojia.fragment.TeacherCouponUnOverdueFragment;
import com.nuoxian.kokojia.utils.CommonMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的优惠券页面
 */
public class CreateCouponActivity extends BaseActivity {

    private RadioGroup rg;
    private ViewPager mViewPager;
    private TextView tvCreateCoupon;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_create_coupon);
        //设置标题
        setTitle("创建优惠券");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCouponActivity.this.finish();
            }
        });

        initView();
        initFragment();

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        setListener();
    }

    private void initView() {
        rg = (RadioGroup) findViewById(R.id.rg_teacher_coupon);
        tvCreateCoupon = (TextView) findViewById(R.id.tv_create_coupon);
        mViewPager = (ViewPager) findViewById(R.id.vp_teacher_coupon);
    }

    private void initFragment(){
        fragments = new ArrayList<>();
        fragments.add(new TeacherCouponAllFragment());
        fragments.add(new TeacherCouponUnOverdueFragment());
        fragments.add(new TeacherCouponOverduedFragment());
        fragments.add(new TeacherCouponInsideFragment());
        fragments.add(new TeacherCouponOutsideFragment());
    }

    private void setListener(){
        //fragment切换
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_teacher_coupon_all://全部
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_teacher_coupon_unOverdue://未过期
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_teacher_coupon_overdued://已过期
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_teacher_coupon_inside://站内券
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.rb_teacher_coupon_outside://站外券
                        mViewPager.setCurrentItem(4);
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

        tvCreateCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建优惠券
                CommonMethod.startActivity(CreateCouponActivity.this, CreateCouponDetailsActivity.class);
            }
        });
    }

}
