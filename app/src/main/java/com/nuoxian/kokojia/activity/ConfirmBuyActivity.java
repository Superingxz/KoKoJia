package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.CanUseCouponAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.MyCoupon;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *  提交订单页面
 */
public class ConfirmBuyActivity extends AutoLayoutActivity implements View.OnClickListener {

    private ImageView ivImage;
    private EditText etPhone,etPreferentialCode;
    private TextView tvTitle,tvSchoolName,tvClassNum,tvTrialNum,tvPrice,ivBack,tvSelectCoupon,tvCouponInfo;
    private Button btnConfirm;
    private RequestQueue mQueue;
    private AutoLinearLayout llCoupon;
    private ListView myCouponListView;
    private CanUseCouponAdapter couponAdapter;
    private List<MyCoupon.DataBean> myCouponList;
    private String originalPrice;
    private String id,uid,phone="",code="",status,order_sn,errorMsg,json;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){//获取到订单结果
                //设置按钮可点击
                btnConfirm.setEnabled(true);
                btnConfirm.setBackgroundColor(0xff00aeef);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_buy);

        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        mQueue = MyApplication.getRequestQueue();
        //获取id和uid
        id = getIntent().getStringExtra("id");
        uid = CommonMethod.getUid(this);
        myCouponList = new ArrayList<>();
        initView();
        //获取订单数据
        parseJson(getIntent().getStringExtra("json"));
        //获取输入的电话
        etPhone.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                phone = s + "";
            }
        });
        //获取优惠码
        etPreferentialCode.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                code = s + "";
                if (code.length() == 18) {
                    //验证码输入完成，刷新价格
                    getNewPrice();
                    CommonMethod.showLoadingDialog("正在加载...", ConfirmBuyActivity.this);
                } else {//验证码不正确
                    tvCouponInfo.setVisibility(View.GONE);
                    tvPrice.setText(originalPrice);
                }
            }
        });
    }

    private void initView(){
        Typeface typeface = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_confirm_buy_back);
        ivBack.setTypeface(typeface);
        ivImage = (ImageView) findViewById(R.id.iv_confirm_buy);
        etPhone = (EditText) findViewById(R.id.et_confirm_phone);
        llCoupon = (AutoLinearLayout) findViewById(R.id.ll_coupon);
        tvSelectCoupon = (TextView) findViewById(R.id.tv_comfirm_buy_select_coupon);
        etPreferentialCode = (EditText) findViewById(R.id.et_confirm_preferential_code);
        tvTitle = (TextView) findViewById(R.id.tv_confirm_buy_title);
        tvSchoolName = (TextView) findViewById(R.id.tv_confirm_buy_school_name);
        tvClassNum = (TextView) findViewById(R.id.tv_confirm_buy_class_num);
        tvTrialNum = (TextView) findViewById(R.id.tv_confirm_buy_trial_num);
        tvPrice = (TextView) findViewById(R.id.tv_confirm_buy_price);
        btnConfirm = (Button) findViewById(R.id.btn_confirm_buy_confirm);
        tvCouponInfo = (TextView) findViewById(R.id.tv_coupon_info);
        //设置确认按钮不可点击
        btnConfirm.setEnabled(false);
        btnConfirm.setBackgroundColor(Color.GRAY);

        ivBack.setOnClickListener(this);
        tvSelectCoupon.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_confirm_buy_back://返回
                finish();
                break;
            case R.id.tv_comfirm_buy_select_coupon://选择优惠券
                if(myCouponList!=null&&!myCouponList.isEmpty()){
                    showMyCoupon();
                }else{
                    CommonMethod.showAlerDialog("","您没有可用的优惠券",this);
                }
                break;
            case R.id.btn_confirm_buy_confirm://确认
                //获取提交订单的结果
                getOrderResultData(Urls.CONFIRM_ORDER_RESULT);
                //显示正在加载
                CommonMethod.showLoadingDialog("正在加载请稍后...",this);
                break;
        }
    }

    /**
     * 显示我的优惠券
     */
    private void showMyCoupon(){
        //自定义dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_my_coupon, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button cancle = (Button) view.findViewById(R.id.btn_my_coupon_cancle);//取消
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //加载我的优惠券
        myCouponListView = (ListView) view.findViewById(R.id.lv_my_coupon);
        couponAdapter = new CanUseCouponAdapter(myCouponList,this);
        myCouponListView.setAdapter(couponAdapter);

        myCouponListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etPreferentialCode.setText(myCouponList.get(position).getCoupon_code());
                dialog.dismiss();
            }
        });
    }

    /**
     * 获取新的价格
     */
    private void getNewPrice(){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.VERIFICATION_CODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                JSONObject j2 = j1.getJSONObject("data");
                                tvCouponInfo.setVisibility(View.VISIBLE);
                                tvCouponInfo.setText(j2.getString("coupon_name"));
                                tvPrice.setText(j2.getString("price"));
                            }else{
                                CommonMethod.showAlerDialog("",j1.getString("msg"),ConfirmBuyActivity.this);
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(ConfirmBuyActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("uid", uid);
                params.put("code", code);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 跳转到响应的支付界面
     */
    private void pay(){
        //根据获取提交的订单结果来确定支付方式
        if("0".equals(status)){
            //订单信息有错误
            Toast.makeText(ConfirmBuyActivity.this,errorMsg,Toast.LENGTH_LONG).show();
        }else if("1".equals(status)){
            //使用余额支付,跳转到余额支付页面
            Intent intent = new Intent(ConfirmBuyActivity.this,BalancePayActivity.class);
            intent.putExtra("order_sn",order_sn);
            startActivity(intent);
        }else if("2".equals(status)){
            //使用第三方支付，跳转到第三方支付页面
            Intent intent = new Intent(ConfirmBuyActivity.this,ThirdPayActivity.class);
            intent.putExtra("orderNum",order_sn);
            startActivity(intent);
        }
        //关闭该页面
        finish();
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
                tvTitle.setText(j2.getString("title"));
                tvSchoolName.setText(j2.getString("school_name"));
                tvClassNum.setText("课时共" + j2.getString("class_num") + "节");
                tvTrialNum.setText(j2.getString("trial_num")+"人在学习");
                if("0".equals(j2.getString("is_coupon"))){
                    //没有优惠码
                    llCoupon.setVisibility(View.GONE);
                }else{
                    //有优惠码
                    llCoupon.setVisibility(View.VISIBLE);
                }
                myCouponList.clear();
                JSONArray j3 = j1.getJSONArray("coupon");
                if(j3!=null&&j3.length()!=0){//有优惠券
                    for(int i=0;i<j3.length();i++){
                        JSONObject j4 = j3.getJSONObject(i);
                        MyCoupon.DataBean coupon = new MyCoupon.DataBean();
                        coupon.setCoupon_code(j4.getString("coupon_code"));
                        coupon.setCoupon_sale(j4.getString("sale"));
                        coupon.setSale_price(j4.getString("sale_price"));
                        myCouponList.add(coupon);
                    }
                }
                originalPrice = j2.optString("price");
                tvPrice.setText(j2.optString("price"));
                MyApplication.imageLoader.displayImage(j2.getString("image_url"),ivImage,MyApplication.options);
                //加载完成，通知提交按钮可点击
                Message msg = Message.obtain();
                msg.what=1;
                mHandler.sendMessage(msg);
            }else{
                CommonMethod.toast(this,"访问数据失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取提交订单后的信息
     * @param url
     */
    private void getOrderResultData(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            status = j1.getString("status");
                            order_sn = j1.optString("order_sn");
                            errorMsg = j1.optString("msg");
                            //跳转到支付界面
                            pay();
                            //隐藏正在加载
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //隐藏正在加载
                            CommonMethod.dismissLoadingDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(ConfirmBuyActivity.this);
                //隐藏正在加载
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("uid",uid);
                params.put("code",code);
                params.put("tel", phone);
                return params;
            }
        };
        mQueue.add(request);
    }
}
