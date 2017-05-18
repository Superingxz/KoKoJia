package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class BalancePayActivity extends AutoLayoutActivity implements View.OnClickListener {

    private ImageView ivImage;
    private TextView tvTitle, tvSchoolName, tvClassNum, tvTrialNum, tvBalance, tvPrice,ivBack;
    private EditText etPassword;
    private Button btnConfirm;
    private RequestQueue mQueue;
    private String orderNum, newOrderNum, uid, password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_pay);
        //设置标题栏的颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        mQueue = MyApplication.getRequestQueue();
        //获取订单号
        orderNum = getIntent().getStringExtra("order_sn");
        //获取uid
        uid = CommonMethod.getUid(this);
        //初始化数据
        initView();
        //获取支付页面数据
        getPayData(Urls.ORDER_PAY);
        //设置支付按钮不可点击
        btnConfirm.setEnabled(false);
        btnConfirm.setBackgroundColor(Color.GRAY);
        //获取支付密码
        etPassword.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                password = s + "";
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initView() {
        Typeface tp = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_balance_pay_back);
        ivBack.setTypeface(tp);
        ivImage = (ImageView) findViewById(R.id.iv_balance_pay);
        tvTitle = (TextView) findViewById(R.id.tv_balance_pay_title);
        tvSchoolName = (TextView) findViewById(R.id.tv_balance_pay_school_name);
        tvClassNum = (TextView) findViewById(R.id.tv_balance_pay_class_num);
        tvTrialNum = (TextView) findViewById(R.id.tv_balance_pay_trial_num);
        tvBalance = (TextView) findViewById(R.id.tv_balance_pay_balance);
        tvPrice = (TextView) findViewById(R.id.tv_balance_pay_price);
        etPassword = (EditText) findViewById(R.id.et_balance_pay_password);
        btnConfirm = (Button) findViewById(R.id.btn_balance_pay_confirm);

        ivBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    /**
     * 点击监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_balance_pay_back://返回
                finish();
                break;
            case R.id.btn_balance_pay_confirm://确认支付
                if (TextUtils.isEmpty(password)) {
                    //输入的支付密码为空
                    CommonMethod.toast(this, "请输入支付密码");
                } else {
                    //显示正在支付dialog
                    CommonMethod.showLoadingDialog("正在支付请稍后...", this);
                    //获取支付结果
                    getPayResult(Urls.CONFIRM_BALANCE_PAY);
                    //设置按钮不可点击
                    btnConfirm.setEnabled(false);
                    btnConfirm.setBackgroundColor(Color.GRAY);
                }
                break;
        }
    }

    /**
     * 获取支付页面数据
     *
     * @param url
     */
    private void getPayData(String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parsePayDataJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(BalancePayActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("order_sn", orderNum);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 解析json数据
     *
     * @param json
     */
    private void parsePayDataJson(String json) {
        try {
            JSONObject j1 = new JSONObject(json);
            if ("0".equals(j1.getString("status"))) {
                //出错
                CommonMethod.toast(this, j1.getString("msg"));
            } else if ("1".equals(j1.getString("status"))) {
                //使用余额支付
                JSONObject j2 = j1.optJSONObject("data");
                newOrderNum = j1.getString("order_sn");
                //加载数据
                tvTitle.setText(j2.optString("title"));
                tvPrice.setText("￥" + j2.optString("payment_price"));
                tvTrialNum.setText(j2.optString("trial_num") + "人在学");
                tvClassNum.setText("课时共" + j2.optString("class_num") + "节");
                tvSchoolName.setText(j2.optString("school_name"));
                tvBalance.setText("￥" + j2.optString("user_money"));
                MyApplication.imageLoader.displayImage(j2.optString("image_url"), ivImage, MyApplication.options);
            } else if ("2".equals(j1.getString("status"))) {
                //使用第三方支付
                Intent intent = new Intent(BalancePayActivity.this, ThirdPayActivity.class);
                intent.putExtra("orderNum", orderNum);
                startActivity(intent);
            }
            //设置支付按钮可点击的
            btnConfirm.setEnabled(true);
            btnConfirm.setBackgroundColor(0xff00aeef);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取支付结果
     */
    private void getPayResult(String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BalancePayActivity.this);
                            builder.setTitle("支付结果");
                            builder.setCancelable(false);
                            JSONObject j1 = new JSONObject(response);
                            if ("0".equals(j1.getString("status"))) {
                                //取消正在加载dialog
                                CommonMethod.dismissLoadingDialog();
                                //支付不成功
                                builder.setMessage(j1.getString("msg"));
                                builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                //设置按钮可点击
                                btnConfirm.setEnabled(true);
                                btnConfirm.setBackgroundColor(0xff00aeef);
                            } else if ("1".equals(j1.getString("status"))) {
                                //取消正在加载dialog
                                CommonMethod.dismissLoadingDialog();
                                //支付成功
                                builder.setMessage(j1.getString("msg"));
                                builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //发送广播，支付成功
                                        BuyResult result = new BuyResult();
                                        result.setBuyStatus("ok");
                                        EventBus.getDefault().post(result);
                                        //关闭支付页面
                                        BalancePayActivity.this.finish();
                                    }
                                });
                            }
                            builder.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //取消正在加载dialog
                            CommonMethod.dismissLoadingDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //提交结果失败
                CommonMethod.loadFailureToast(BalancePayActivity.this);
                //取消正在加载dialog
                CommonMethod.dismissLoadingDialog();
                //设置按钮可点击
                btnConfirm.setEnabled(true);
                btnConfirm.setBackgroundColor(0xff00aeef);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("order_sn", newOrderNum);
                params.put("passwd", password);
                return params;
            }
        };
        mQueue.add(request);
    }
}
