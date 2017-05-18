package com.nuoxian.kokojia.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付中心
 */
public class PayCenterActivity extends BaseActivity implements View.OnClickListener {

    private AutoRelativeLayout rlBalance,rlBindZFB,rlPay,rlRecharge,rlRefund,rlGetMoney,rlSetPassword,rlFindPassword;
    private TextView tvBalance,tvSetPassword;
    private String uid,sign;
    private boolean isSuccess=false;//是否加载数据成功
    private String passwdStatus="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        addContentView(R.layout.activity_pay_center);
        //设置标题
        setTitle("支付中心");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayCenterActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        //加载FONTAWESOME
        Typeface typeface = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.layout_pay_center),typeface);

        initView();
        //获取数据
        getData();
        //设置监听
        setListener();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        rlBalance = (AutoRelativeLayout) findViewById(R.id.rl_pay_center_balance);
        rlBindZFB = (AutoRelativeLayout) findViewById(R.id.rl_pay_center_bind_zfb);
        rlPay = (AutoRelativeLayout) findViewById(R.id.rl_pay_center_pay);
        rlRecharge = (AutoRelativeLayout) findViewById(R.id.rl_pay_center_recharge);
        rlRefund = (AutoRelativeLayout) findViewById(R.id.rl_pay_center_refund);
        rlGetMoney = (AutoRelativeLayout) findViewById(R.id.rl_pay_center_get_money);
        rlSetPassword = (AutoRelativeLayout) findViewById(R.id.rl_pay_center_set_password);
        rlFindPassword = (AutoRelativeLayout) findViewById(R.id.rl_pay_center_find_password);
        tvBalance = (TextView) findViewById(R.id.tv_pay_center_balance);
        tvSetPassword = (TextView) findViewById(R.id.tv_pay_center_set_password);
    }

    /**
     * 设置监听
     */
    private void setListener(){
        rlBalance.setOnClickListener(this);
        rlBindZFB.setOnClickListener(this);
        rlPay.setOnClickListener(this);
        rlRecharge.setOnClickListener(this);
        rlRefund.setOnClickListener(this);
        rlGetMoney.setOnClickListener(this);
        rlSetPassword.setOnClickListener(this);
        rlFindPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_pay_center_balance://账户余额
                CommonMethod.startActivity(this,PayCenterBalanceActivity.class);
                break;
            case R.id.rl_pay_center_bind_zfb://绑定支付宝
                Intent intent = new Intent(this,BindZFBActivity.class);
                intent.putExtra("sign",sign);
                startActivity(intent);
                break;
            case R.id.rl_pay_center_pay://支出记录
                CommonMethod.startActivity(this,PayCenterPayActivity.class);
                break;
            case R.id.rl_pay_center_recharge://充值记录
                CommonMethod.startActivity(this,PayCenterRechargeActivity.class);
                break;
            case R.id.rl_pay_center_refund://退款记录
                CommonMethod.startActivity(this,PayCenterRefundActivity.class);
                break;
            case R.id.rl_pay_center_get_money://提现记录
                CommonMethod.startActivity(this,PayCenterWithdrawalsActivity.class);
                break;
            case R.id.rl_pay_center_set_password://设置密码
                if(isSuccess){//加载数据成功
                    if("1".equals(passwdStatus)){
                        //已设置密码，跳转到重置密码界面
                        Intent intent1 = new Intent(this,ResetPayPasswordActivity.class);
                        intent1.putExtra("sign",sign);
                        startActivity(intent1);
                    }else{
                        //没有设置密码，跳转到设置密码界面
                        Intent intent1 = new Intent(this,SetPayPasswordActivity.class);
                        intent1.putExtra("sign",sign);
                        startActivity(intent1);
                    }
                }else {//重新加载数据
                    getData();
                    CommonMethod.toast(this,"加载失败请重试!");
                }
                break;
        }
    }

    /**
     * 获取数据
     */
    private void getData(){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.PAY_CENTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                tvBalance.setText("￥"+j1.getString("money"));
                                passwdStatus = j1.getString("passwd_status");
                                if("1".equals(j1.getString("passwd_status"))){//已设置密码
                                    tvSetPassword.setText("重置支付密码");
                                }else{//未设置
                                    tvSetPassword.setText("设置支付密码");
                                }
                                sign = j1.optString("sign");//找回密码和设置密码时所需要的参数
                                isSuccess = true;
                            }else{
                                CommonMethod.toast(PayCenterActivity.this,j1.optString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(PayCenterActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 通知刷新界面
     */
    public void onEventMainThread(Bundle bundle) {
        if("toRefresh".equals(bundle.getString("ok"))){
            getData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册
        EventBus.getDefault().unregister(this);
    }
}
