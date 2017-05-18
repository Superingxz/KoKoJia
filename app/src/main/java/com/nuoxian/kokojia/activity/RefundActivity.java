package com.nuoxian.kokojia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.fragment.RefundAgreeFragment;
import com.nuoxian.kokojia.fragment.RefundAllFragment;
import com.nuoxian.kokojia.fragment.RefundClosedFragment;
import com.nuoxian.kokojia.fragment.RefundPlatformFragment;
import com.nuoxian.kokojia.fragment.RefundPrepareSolveFragment;
import com.nuoxian.kokojia.fragment.RefundRefusedFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 退款管理
 */
public class RefundActivity extends BaseActivity {

    private RadioGroup rg;
    private ViewPager mViewPager;
    private List<Fragment> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_refund);
        //设置标题
        setTitle("退款管理");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefundActivity.this.finish();
            }
        });

        initView();
        initFragment();
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),mList);
        mViewPager.setAdapter(adapter);
        //设置监听
        setListener();
    }

    private void initFragment(){
        mList = new ArrayList<>();
        mList.add(new RefundAllFragment());
        mList.add(new RefundPrepareSolveFragment());
        mList.add(new RefundAgreeFragment());
        mList.add(new RefundRefusedFragment());
        mList.add(new RefundPlatformFragment());
        mList.add(new RefundClosedFragment());
    }

    private void initView(){
        rg = (RadioGroup) findViewById(R.id.rg_refund);
        mViewPager = (ViewPager) findViewById(R.id.vp_refund);
    }

    private void setListener(){
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

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_refund_all://全部
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_refund_closed://已关闭
                        mViewPager.setCurrentItem(5);
                        break;
                    case R.id.rb_refund_platform://平台介入
                        mViewPager.setCurrentItem(4);
                        break;
                    case R.id.rb_refund_prepare_solve://待处理
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_refund_refunded://已退款
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_refund_refused://已拒绝
                        mViewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }
}
