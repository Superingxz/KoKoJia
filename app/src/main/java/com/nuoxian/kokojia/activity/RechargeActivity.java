package com.nuoxian.kokojia.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.ThirdPayListAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.PayWay;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.nuoxian.kokojia.utils.WXPay;
import com.nuoxian.kokojia.utils.ZFBPay;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值
 */
public class RechargeActivity extends AutoLayoutActivity implements View.OnClickListener {

    private TextView tvUserName,tvBalance,ivBack;
    private EditText etPrice;
    private Button btnPay;
    private ListView listView;
    private List<PayWay> list = new ArrayList<>();
    private ThirdPayListAdapter adapter;
    private RequestQueue mQueue;
    private String uid;
    private String price;
    public static Activity instance = null;//用于其他页面去关闭这个activity
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                //数据加载完成，设置充值按钮可点击
                btnPay.setEnabled(true);
                btnPay.setBackgroundColor(0xff00aeef);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        //设置标题栏的颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        instance = this;
        initView();
        mQueue = MyApplication.getRequestQueue();
        //获取uid
        uid = CommonMethod.getUid(this);
        //初始化list
        initList();
        adapter = new ThirdPayListAdapter(this,list);
        listView.setAdapter(adapter);
        //获取界面数据
        getData(Urls.RECHARGE_CENTER);
        //获取用户输入的金额
        etPrice.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                price = s + "";
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView(){
        Typeface tf = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_recharge_back);
        ivBack.setTypeface(tf);
        tvUserName = (TextView) findViewById(R.id.tv_recharge_user);
        tvBalance = (TextView) findViewById(R.id.tv_recharge_balance);
        etPrice = (EditText) findViewById(R.id.et_recharge_price);
        btnPay = (Button) findViewById(R.id.btn_recharge_pay);
        //设置充值按钮不可点击
        btnPay.setEnabled(false);
        btnPay.setBackgroundColor(Color.GRAY);
        listView = (ListView) findViewById(R.id.lv_recharge_pay_way);

        //设置监听
        ivBack.setOnClickListener(this);
        btnPay.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //修改选中的支付方式
                for (int i = 0; i < list.size(); i++) {
                    PayWay pay = list.get(i);
                    if (i == position) {
                        pay.setSelect(true);
                    } else {
                        pay.setSelect(false);
                    }
                    list.set(i, pay);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 初始化list，支付方式
     */
    private void initList(){
        //支付宝充值
        PayWay zfb = new PayWay();
        zfb.setPayWay(R.mipmap.ali_pay_55_200);
        zfb.setSelect(true);
        zfb.setPayName("zfb");
        //微信充值
        PayWay wx = new PayWay();
        wx.setPayWay(R.mipmap.wx_pay55_200);
        wx.setSelect(false);
        wx.setPayName("wx");
        list.add(zfb);
        list.add(wx);
    }

    /**
     * 获取界面数据
     * @param url
     */
    private void getData(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                JSONObject j2 = j1.getJSONObject("data");
                                tvUserName.setText(j2.getString("username"));
                                tvBalance.setText(j2.getString("user_money"));
                                //通知数据加载完成
                                Message msg = Message.obtain();
                                msg.what=1;
                                handler.sendMessage(msg);
                            }else if("0".equals(j1.getString("status"))){
                                CommonMethod.toast(RechargeActivity.this,j1.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(RechargeActivity.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_recharge_back://返回
                finish();
                break;
            case R.id.btn_recharge_pay://充值
                if(CommonMethod.isPositiveDecimal(price)){
                    //输入的金额格式正确，进行充值操作
                    CommonMethod.showLoadingDialog("正在加载...",this);
                    if("wx".equals(getPayWay())){
                        //使用微信充值
                        rechargeByWX(Urls.WX_PAY);
                    }else if("zfb".equals(getPayWay())){
                        //使用支付宝充值
                        rechargeByZFB(Urls.RECHARGE_COMFIRM);
                    }
                }else{
                    CommonMethod.toast(this,"充值金额不能为空!");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 使用支付宝充值
     */
    private void rechargeByZFB(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("0".equals(j1.getString("status"))){
                                //出错
                                CommonMethod.toast(RechargeActivity.this,j1.getString("msg"));
                            }else if("1".equals(j1.getString("status"))){
                                //使用支付宝充值
                                ZFBPay pay = new ZFBPay(RechargeActivity.this,"RechargeActivity");
                                pay.pay(j1.getString("data"));
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            CommonMethod.dismissLoadingDialog();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(RechargeActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("total_fee",price);
                params.put("banktype", CommonValues.BANK_TYPE_ZFB);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 使用微信支付
     */
    private void rechargeByWX(String url){
        StringRequest request = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.dismissLoadingDialog();
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                JSONObject j2 = j1.getJSONObject("data");
                                WXPay wx = new WXPay(RechargeActivity.this);
                                wx.pay(j2.getString("appid"), j2.getString("partnerid"), j2.getString("prepayid"), j2.getString("noncestr"), j2.getString("timestamp"), j2.getString("sign"));
                            }else{
                                //没有数据
                                CommonMethod.toast(RechargeActivity.this, j1.optString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //加载失败
                CommonMethod.loadFailureToast(RechargeActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("total_fee",price);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取支付方式
     * @return
     */
    private String getPayWay(){
        for (int i=0;i<list.size();i++){
            PayWay way = list.get(i);
            if(way.isSelect()){
                return way.getPayName();
            }
        }
        return null;
    }
}
