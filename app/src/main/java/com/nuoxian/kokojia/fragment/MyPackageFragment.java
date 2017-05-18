package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class MyPackageFragment extends Fragment {

    private EditText etSearch;
    private TextView tvSearch;
    private RadioGroup rg;
    private ViewPager mViewPager;
    private String word="";
    private List<Fragment> fragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_package, container, false);

        initView(view);
        initFragments();

        MainPagerAdapter adapter = new MainPagerAdapter(getChildFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        //设置监听
        setListener();
        return view;
    }

    private void initView(View view){
        etSearch = (EditText) view.findViewById(R.id.et_mypackage_search);
        tvSearch = (TextView) view.findViewById(R.id.tv_mypackage_search);
        rg = (RadioGroup) view.findViewById(R.id.rg_my_package_fragment);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_mypackage);
    }

    private void initFragments(){
        fragments = new ArrayList<>();
        fragments.add(new MyPackageAll());
        fragments.add(new MyPackageSelling());
        fragments.add(new MyPackagePendingAudit());
        fragments.add(new MyPackageStopSell());
    }

    private void setListener(){
        //获取搜索关键字
        etSearch.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                word = s+"";
            }
        });
        //对软键盘中的搜索按钮进行监听
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    CommonMethod.hideInput(getActivity(), etSearch);
                    //发送广播给fragment
                    sendEventBus();
                }
                return false;
            }
        });
        //点击搜索按钮
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送广播给fragment
                sendEventBus();
            }
        });
        //切换Fragment
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                word = "";
                etSearch.setText("");
                switch (checkedId) {
                    case R.id.rb_all://全部
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_selling://销售中
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_pending_audit://待审核
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_stop_sell://已下架
                        mViewPager.setCurrentItem(3);
                        break;
                }
                //发送广播给fragment
                sendEventBus();
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

    /**
     * 发送广播
     */
    private void sendEventBus(){
        Bundle bundle = new Bundle();
        bundle.putString("word",word);
        switch (rg.getCheckedRadioButtonId()){
            case R.id.rb_all://全部
                bundle.putString("fragment","MyPackageAll");
                break;
            case R.id.rb_selling://销售中
                bundle.putString("fragment","MyPackageSelling");
                break;
            case R.id.rb_pending_audit://待审核
                bundle.putString("fragment","MyPackagePendingAudit");
                break;
            case R.id.rb_stop_sell://已下架
                bundle.putString("fragment","MyPackageStopSell");
                break;
        }
        EventBus.getDefault().post(bundle);
    }

}
