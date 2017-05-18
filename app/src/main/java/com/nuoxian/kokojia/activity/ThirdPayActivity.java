package com.nuoxian.kokojia.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
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
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.WXPay;
import com.nuoxian.kokojia.utils.ZFBPay;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ThirdPayActivity extends AutoLayoutActivity implements View.OnClickListener {

    private ImageView ivImage;
    private TextView tvTitle,tvSchoolName,ivBack,tvClassNum,tvTrialNum,tvPrice,tvBalance;
    private ListView mListView;
    private Button btnConfirm;
    private String orderNum,newOrderNum;
    private RequestQueue mQueue;
    private String uid,price;
    private List<PayWay> list = new ArrayList<>();
    private ThirdPayListAdapter adapter;
    public static Activity instance;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){//数据加载完成
                //设置支付按钮可点击
                btnConfirm.setEnabled(true);
                btnConfirm.setBackgroundColor(0xff00aeef);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_pay);
        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        instance = this;
        mQueue = MyApplication.getRequestQueue();
        uid = CommonMethod.getUid(this);
        //获取订单号
        orderNum = getIntent().getStringExtra("orderNum");
        //初始化控件
        initView();
        //初始化list
        initList();
        adapter = new ThirdPayListAdapter(this, list);
        mListView.setAdapter(adapter);
        //获取订单数据
        getOrderData(Urls.ORDER_PAY);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        Typeface typeface = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_third_pay_back);
        ivBack.setTypeface(typeface);
        ivImage = (ImageView) findViewById(R.id.iv_third_pay);
        tvTitle = (TextView) findViewById(R.id.tv_third_pay_title);
        tvSchoolName = (TextView) findViewById(R.id.tv_third_pay_school_name);
        tvClassNum = (TextView) findViewById(R.id.tv_third_pay_class_num);
        tvTrialNum = (TextView) findViewById(R.id.tv_third_pay_trial_num);
        tvPrice = (TextView) findViewById(R.id.tv_third_pay_price);
        tvBalance = (TextView) findViewById(R.id.tv_third_pay_balance);
        btnConfirm = (Button) findViewById(R.id.btn_third_pay_confirm);
        mListView = (ListView) findViewById(R.id.lv_third_pay);

        ivBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        //设置支付按钮不可点击
        btnConfirm.setEnabled(false);
        btnConfirm.setBackgroundColor(Color.GRAY);
        //listView的监听时间
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
     * 初始化List
     */
    private void initList(){
        //支付宝支付
        PayWay zfb = new PayWay();
        zfb.setPayWay(R.mipmap.ali_pay_55_200);
        zfb.setSelect(true);
        zfb.setPayName("zfb");
        PayWay wx = new PayWay();
        wx.setPayWay(R.mipmap.wx_pay55_200);
        wx.setSelect(false);
        wx.setPayName("wx");
        list.add(zfb);
        list.add(wx);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_third_pay_back://返回
                finish();
                break;
            case R.id.btn_third_pay_confirm://确认支付
                for(int i=0;i<list.size();i++){
                    PayWay pay = list.get(i);
                    if(pay.isSelect()){
                        CommonMethod.showLoadingDialog("正在加载...",this);
                        if("wx".equals(pay.getPayName())){
                            //使用微信支付
                            buyByWX(Urls.WX_BUY);
                        }else if("zfb".equals(pay.getPayName())){
                            //使用支付宝支付
                            buyByZFB(Urls.THIRD_PAY);
                        }
                        break;
                    }
                }
                break;
        }
    }

    /**
     * 获取订单数据
     * @param url
     */
    private void getOrderData(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseOrderData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(ThirdPayActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("order_sn",orderNum);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 解析数据
     * @param json
     */
    private void parseOrderData(String json){
        try {
            JSONObject j1 = new JSONObject(json);
            if("0".equals(j1.getString("status"))){
                //出错
                CommonMethod.toast(this,j1.getString("msg"));
            }else if("1".equals(j1.getString("status"))){//使用余额支付
                Intent intent = new Intent(this,BalancePayActivity.class);
                intent.putExtra("order_sn",orderNum);
                startActivity(intent);
            }else if("2".equals(j1.getString("status"))){//使用第三方支付
                JSONObject j2 = j1.getJSONObject("data");
                newOrderNum = j1.getString("order_sn");
                tvTitle.setText(j2.getString("title"));
                tvTrialNum.setText(j2.getString("trial_num")+"人在学");
                tvClassNum.setText("课时共"+j2.getString("class_num")+"节");
                tvSchoolName.setText(j2.getString("school_name"));
                tvBalance.setText("￥"+j2.getString("user_money"));
                tvPrice.setText("￥"+j2.getString("payment_price"));
                price = j2.getString("payment_price");
                MyApplication.imageLoader.displayImage(j2.getString("image_url"),ivImage,MyApplication.options);
                //通知数据加载完成
                Message msg = Message.obtain();
                msg.what=1;
                handler.sendMessage(msg);
            }else if("3".equals(j1.getString("status"))){//优惠券过期
                CommonMethod.toast(this,j1.getString("msg"));
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取购买数据
     * @param url
     */
    private void buyByZFB(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.dismissLoadingDialog();
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("0".equals(j1.getString("status"))){
                                CommonMethod.toast(ThirdPayActivity.this,j1.getString("msg"));
                            }else{
                                ZFBPay zfb = new ZFBPay(ThirdPayActivity.this,"ThirdPayActivity");
                                zfb.pay(j1.optString("data"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.dismissLoadingDialog();
                CommonMethod.loadFailureToast(ThirdPayActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("order_sn",newOrderNum);
                params.put("total_fee",price);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 使用微信支付
     */
    private void buyByWX(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.dismissLoadingDialog();
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("0".equals(j1.getString("status"))){
                                CommonMethod.toast(ThirdPayActivity.this,j1.getString("msg"));
                            }else{
                                JSONObject j2 = j1.getJSONObject("data");
                                WXPay pay = new WXPay(ThirdPayActivity.this);
                                pay.pay(j2.getString("appid"),j2.getString("partnerid"),j2.getString("prepayid"),j2.getString("noncestr"),j2.getString("timestamp"),j2.getString("sign"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(ThirdPayActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("order_sn",newOrderNum);
                params.put("total_fee",price);
                return params;
            }
        };
        mQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
