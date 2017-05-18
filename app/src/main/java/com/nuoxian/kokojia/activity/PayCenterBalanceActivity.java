package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付中心  账户余额
 */
public class PayCenterBalanceActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvGetMoney,tvRecharge,tvBalance,tvFreezingAmount,tvCanGetMoney;
    private AutoLinearLayout llExplain,llHint;
    private String uid;
    private String depositStatus;
    private String depositCondition;
    private String money = null;
    private String password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pay_center_balance);
        EventBus.getDefault().register(this);
        CommonMethod.showLoadingDialog("正在加载...",this);
        //设置标题
        setTitle("账户余额");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayCenterBalanceActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        initView();
        //获取数据
        getData();
        // 设置监听
        setListener();
    }

    private void initView(){
        Typeface typeface = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.ll_pay_center_balance), typeface);

        tvGetMoney = (TextView) findViewById(R.id.tv_get_money);
        tvRecharge = (TextView) findViewById(R.id.tv_recharge);
        tvBalance = (TextView) findViewById(R.id.tv_balance);
        tvFreezingAmount = (TextView) findViewById(R.id.tv_freezing_amount);
        tvCanGetMoney = (TextView) findViewById(R.id.tv_can_get_money);
        llExplain = (AutoLinearLayout) findViewById(R.id.ll_get_money_explain);
        llHint = (AutoLinearLayout) findViewById(R.id.ll_recharge_hint);
    }

    /**
     * 设置监听
     */
    private void setListener(){
        tvGetMoney.setOnClickListener(this);
        tvRecharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_get_money://提现
                if("1".equals(depositStatus)){//可提现
                    showGetMoneyDialog();
                }else if ("2".equals(depositStatus)){//未绑定支付宝
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提现失败");
                    builder.setMessage("你还没有绑定支付宝账号!");
                    builder.setNegativeButton("前去绑定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //跳转到绑定支付宝界面
                            CommonMethod.startActivity(PayCenterBalanceActivity.this,BindZFBActivity.class);
                        }
                    });
                    builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }else if("3".equals(depositStatus)){//不可提现
                    CommonMethod.showAlerDialog("提现失败",depositCondition,this);
                }else{
                    getData();
                    CommonMethod.toast(this, "网络出现异常，请重试");
                }
                break;
            case R.id.tv_recharge://充值
                CommonMethod.startActivity(this,RechargeActivity.class);
                break;
        }
    }

    /**
     * 显示提现窗口
     */
    private void showGetMoneyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view  = LayoutInflater.from(this).inflate(R.layout.layout_get_money, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        EditText etMoney = (EditText) view.findViewById(R.id.et_get_money);
        //获取用户输入的提现金额
        etMoney.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                money = s+"";
            }
        });
        EditText etPassword = (EditText) view.findViewById(R.id.et_pay_password);
        //获取用户输入的支付密码
        etPassword.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                password = s+"";
            }
        });
        TextView comfirm = (TextView) view.findViewById(R.id.tv_comfirm);
        //确认
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提现
                if(!TextUtils.isEmpty(money)&&!TextUtils.isEmpty(password)){
                    getMoney();
                }
            }
        });
        TextView cancle = (TextView) view.findViewById(R.id.tv_cancle);
        //取消
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 提现
     */
    private void getMoney(){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.GET_MONEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //跳转到提现记录界面
                                CommonMethod.startActivity(PayCenterBalanceActivity.this,GetMoneyActivity.class);
                            }else{
                                //提现失败
                                CommonMethod.showAlerDialog("提现失败",j1.getString("msg"),PayCenterBalanceActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(PayCenterBalanceActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("money",money);
                params.put("passwd",password);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取数据
     */
    private void getData(){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.USER_BALANCE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        llExplain.removeAllViews();
                        llHint.removeAllViews();
                        //解析json数据
                        parseJson(response);
                        CommonMethod.dismissLoadingDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(PayCenterBalanceActivity.this);
                CommonMethod.dismissLoadingDialog();
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
     * 解析json数据
     * @param json
     */
    private void parseJson(String json){
        try {
            JSONObject j1 = new JSONObject(json);
            if("1".equals(j1.getString("status"))){
                JSONObject j2 = j1.getJSONObject("data");
                tvBalance.setText(j2.getString("total_money"));//余额
                tvFreezingAmount.setText(j2.getString("frozen_money"));//冻结金额
                tvCanGetMoney.setText(j2.getString("deposit_money"));//可提现金额
                depositStatus = j2.getString("deposit_status");
                depositCondition = j2.optString("deposit_condition");

                JSONArray j3 = j1.getJSONArray("deposit_desc");
                for (int i=0;i<j3.length();i++){//提现说明
                    JSONObject j4 = j3.getJSONObject(i);
                    TextView textView = new TextView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,10,0,0);
                    textView.setLayoutParams(params);
                    textView.setText(j4.getString("name"));
                    textView.setTextSize(12);
                    textView.setTextColor(Color.parseColor("#4f5961"));
                    llExplain.addView(textView);
                }

                JSONArray j5 = j1.getJSONArray("recharge_desc");
                for (int i=0;i<j5.length();i++){
                    JSONObject j6 = j5.getJSONObject(i);
                    TextView textView = new TextView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,10,0,0);
                    textView.setLayoutParams(params);
                    textView.setText(j6.getString("name"));
                    textView.setTextSize(12);
                    textView.setTextColor(Color.parseColor("#4f5961"));
                    llHint.addView(textView);
                }
            }else{
                CommonMethod.toast(this,"数据访问失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 充值成功发过来的消息
     * @param event
     */
    public void onEventMainThread(BuyResult event) {
        if ("recharge".equals(event.getBuyStatus())) {
            //如果购买成功或者充值成功,刷新界面信息
            getData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
