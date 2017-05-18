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
import com.nuoxian.kokojia.fragment.TeacherAnswerAll;
import com.nuoxian.kokojia.fragment.TeacherAnswerReplyed;
import com.nuoxian.kokojia.fragment.TeacherAnswerWaitReply;

import java.util.ArrayList;
import java.util.List;

/**
 * 老师答疑
 */
public class TeacherAnswerActivity extends BaseActivity {

    private RadioGroup mRadioGroup;
    private ViewPager mViewPager;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_teacher_answer);

        initView();
        initFragments();
        //加载ViewPager
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);

        setListener();
    }

    private void initView() {
        //设置标题
        setTitle("讲师答疑");
        //返回
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_teacher_answer);
        mViewPager = (ViewPager) findViewById(R.id.vp_teacher_answer);
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(new TeacherAnswerAll());
        fragments.add(new TeacherAnswerWaitReply());
        fragments.add(new TeacherAnswerReplyed());
    }

    /**
     * 设置监听
     */
    private void setListener() {
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherAnswerActivity.this.finish();
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_teacher_answer_all://全部
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_teacher_answer_wait_reply://待回复
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_teacher_answer_replyed://已回复
                        mViewPager.setCurrentItem(2);
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
                RadioButton rb = (RadioButton) mRadioGroup.getChildAt(position);
                rb.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
