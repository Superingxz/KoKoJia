package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 * 播放  笔记
 */
public class PlayerNoteFragment extends Fragment {

    private RadioGroup mRadioGroup;
    private ViewPager mViewPager;
    private List<Fragment> fragments;
    private String id,lid,uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_player_note,container,false);

        initView(view);
        initFragments();

        MainPagerAdapter adapter = new MainPagerAdapter(getChildFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        setListener();

        return view;
    }

    private void initView(View v){
        mRadioGroup = (RadioGroup) v.findViewById(R.id.rg_note);
        mViewPager = (ViewPager) v.findViewById(R.id.vp_note);
    }

    private void initFragments(){
        Bundle bundle = getArguments();
        fragments = new ArrayList<>();
        MyNoteFragment noteFragment = new MyNoteFragment();
        noteFragment.setArguments(bundle);
        fragments.add(noteFragment);

        ClassmateNoteFragment classmateNoteFragment = new ClassmateNoteFragment();
        classmateNoteFragment.setArguments(bundle);
        fragments.add(classmateNoteFragment);
    }

    private void setListener(){
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_my_note://我的笔记
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_others_note://同学的笔记
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
                if(position==1){
                    position = 2;
                }
                RadioButton rb = (RadioButton) mRadioGroup.getChildAt(position);
                rb.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
