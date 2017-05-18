package com.nuoxian.kokojia.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.fragment.SellOderAll;
import com.nuoxian.kokojia.fragment.SellOrderClosed;
import com.nuoxian.kokojia.fragment.SellOrderNoPay;
import com.nuoxian.kokojia.fragment.SellOrderPayed;
import com.nuoxian.kokojia.fragment.SellOrderSuccessed;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 销售订单
 */
public class SellOrderActivity extends BaseActivity {

    private RadioGroup rg;
    private ViewPager mViewPager;
    private EditText etSearch;
    private TextView tvSearch;
    private String searchWord = "";
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_sell_order);

        initView();
        initFragments();

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);
        //设置监听
        setListener();
    }

    private void initView() {
        //设置页面标题
        setTitle("销售订单");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SellOrderActivity.this.finish();
            }
        });
        rg = (RadioGroup) findViewById(R.id.rg_sell_order);
        etSearch = (EditText) findViewById(R.id.et_sell_order_search);
        tvSearch = (TextView) findViewById(R.id.tv_sell_order_search);
        mViewPager = (ViewPager) findViewById(R.id.vp_sell_order);
    }

    private void initFragments(){
        fragments = new ArrayList<>();
        fragments.add(new SellOderAll());
        fragments.add(new SellOrderNoPay());
        fragments.add(new SellOrderPayed());
        fragments.add(new SellOrderSuccessed());
        fragments.add(new SellOrderClosed());
    }

    private void setListener() {
        //获取用户输入的关键字
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
                    CommonMethod.hideInput(SellOrderActivity.this, etSearch);
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
        //单选按钮监听
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //将搜索关键字设为空
                searchWord = "";
                etSearch.setText("");
                switch (checkedId) {//显示选择的fragment
                    case R.id.rb_all://全部
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_no_pay://未付款
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_payed://已付款
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_successed://已成功
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.rb_closed://已关闭
                        mViewPager.setCurrentItem(4);
                        break;
                }
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
     * 发送广播给fragment去更新搜索的数据
     */
    private void sendEventBus(){
        Bundle bundle = new Bundle();
        switch (rg.getCheckedRadioButtonId()){
            case R.id.rb_all://全部
                bundle.putString("fragment", "SellOrderAll");
                break;
            case R.id.rb_no_pay://未付款
                bundle.putString("fragment", "SellOrderNoPay");
                break;
            case R.id.rb_payed://已付款
                bundle.putString("fragment", "SellOrderPayed");
                break;
            case R.id.rb_successed://已成功
                bundle.putString("fragment", "SellOrderSuccessed");
                break;
            case R.id.rb_closed://已关闭
                bundle.putString("fragment", "SellOrderClosed");
                break;
        }
        bundle.putString("word",searchWord);
        EventBus.getDefault().post(bundle);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
