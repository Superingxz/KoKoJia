package com.nuoxian.kokojia.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.pullableview.PullableListView;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.MyCourseActivity;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.adapter.MyCourseFragmentAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.FragmentMyCourse;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/2.
 * 课程管理 我的课程
 */
public class MyCourseFragment extends Fragment {

    private View view;
    private RadioGroup rg;
    private ViewPager mViewPager;
    private EditText etSearch;
    private TextView tvSearch;
    public static String searchWord = "";
    private List<Fragment> fragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_course, container, false);
        //初始化视图
        initView();
        initFragments();

        MainPagerAdapter adapter = new MainPagerAdapter(getChildFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        setListener();
        return view;
    }

    private void initView() {
        rg = (RadioGroup) view.findViewById(R.id.rg_my_course_fragment);
        etSearch = (EditText) view.findViewById(R.id.et_mycourse_search);
        tvSearch = (TextView) view.findViewById(R.id.tv_mycourse_search);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_mycourse);
    }

    private void initFragments(){
        fragments = new ArrayList<>();
        fragments.add(new MyCourseAll());
        fragments.add(new MyCourseSelling());
        fragments.add(new MyCourseUnsend());
        fragments.add(new MyCourseStopSell());
        fragments.add(new MyCourseOverdue());
    }

    /**
     * 设置监听
     */
    private void setListener() {
        //获取输入的搜索内容
        etSearch.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                searchWord = s + "";
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
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                searchWord = "";
                etSearch.setText("");
                //先将所有的fragment隐藏
                switch (checkedId) {
                    case R.id.rb_all://全部
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_selling://销售中
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_unsend://未发布
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_stop_sell://已停售
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.rb_overdue://已过期
                        mViewPager.setCurrentItem(4);
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
     * 发送广播给fragment
     */
    private void sendEventBus() {
        //发送广播,搜索
        Bundle bundle = new Bundle();
        switch (rg.getCheckedRadioButtonId()) {
            //当前显示的是哪个fragment就发给哪个fragment
            case R.id.rb_all://全部
                bundle.putString("fragment", "MyCourseAll");
                break;
            case R.id.rb_overdue://过期
                bundle.putString("fragment", "MyCourseOverdue");
                break;
            case R.id.rb_stop_sell://停售
                bundle.putString("fragment", "MyCourseStopSell");
                break;
            case R.id.rb_unsend://未发布
                bundle.putString("fragment", "MyCourseUnsend");
                break;
            case R.id.rb_selling://销售中
                bundle.putString("fragment", "MyCourseSelling");
                break;
        }
        bundle.putString("word", searchWord);
        EventBus.getDefault().post(bundle);
    }
}
