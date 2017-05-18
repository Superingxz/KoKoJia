package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.nuoxian.kokojia.utils.TextChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 绑定支付宝
 */
public class BindZFBActivity extends BaseActivity {

    private TextView tvBind, tvUnBind, tvComfirm, tvHint;
    private EditText etZFB, etName;
    private String uid, zfb = "", name = "", sign;
    private RequestQueue mQueue;
    private int count = 1;//EditText监听响应的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_bind_zfb);

        CommonMethod.showLoadingDialog("正在加载...", this);
        sign = getIntent().getStringExtra("sign");
        mQueue = MyApplication.getRequestQueue();
        uid = CommonMethod.getUid(this);
        //设置标题
        setTitle("绑定支付宝");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindZFBActivity.this.finish();
            }
        });

        initView();
        //获取数据
        getData();

        getZFB();//获取用户输入的支付宝
        getName();//获取用户输入的真实姓名

        tvComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputOk()) {
                    tvHint.setVisibility(View.GONE);
                    //提交绑定支付宝
                    CommonMethod.showLoadingDialog("正在加载...", BindZFBActivity.this);
                    bindZFB();
                }
            }
        });
    }

    private void initView() {
        tvBind = (TextView) findViewById(R.id.tv_bind_zfb);
        tvUnBind = (TextView) findViewById(R.id.tv_unBind_zfb);
        tvComfirm = (TextView) findViewById(R.id.tv_bind_zfb_comfirm);
        //设置确认不可点击
        tvComfirm.setEnabled(false);
        tvComfirm.setBackgroundResource(R.drawable.rectangle_solid_gray);
        tvHint = (TextView) findViewById(R.id.tv_bind_hint);
        etZFB = (EditText) findViewById(R.id.et_zfb);
        etName = (EditText) findViewById(R.id.et_name);
        //设置绑定的信息
        setBindContent();
    }

    /**
     * 获取用户输入的支付宝账号
     */
    private void getZFB() {
        etZFB.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                zfb = s + "";
                if (count > 2) {
                    //设置提交可点击
                    tvComfirm.setEnabled(true);
                    tvComfirm.setBackgroundResource(R.drawable.rectangle_solid_blue);
                }
                count++;
            }
        });
    }

    /**
     * 获取用户输入的真实姓名
     */
    private void getName() {
        etName.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                name = s + "";
                if (count > 2) {
                    //设置提交可点击
                    tvComfirm.setEnabled(true);
                    tvComfirm.setBackgroundResource(R.drawable.rectangle_solid_blue);
                }
                count++;
            }
        });
    }

    private void showDialog(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(content);
        final AlertDialog dialog = builder.create();
        dialog.show();
        //3秒后关闭dialog
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
    }

    /**
     * 绑定支付宝
     */
    private void bindZFB() {
        StringRequest request = new StringRequest(Request.Method.POST, Urls.BIND_ZFB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {
                                //绑定支付宝成功
                                getData();
                                showDialog("绑定成功");
                            } else {
                                //绑定支付宝失败
                                showDialog("绑定失败");
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(BindZFBActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("account", zfb);
                params.put("name", name);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 设置绑定的信息
     */
    private void setBindContent() {
        SpannableStringBuilder spannable = new SpannableStringBuilder(tvBind.getText());
        //设置“解除绑定”四个字的字体颜色为红色,并可点击
        tvBind.setMovementMethod(LinkMovementMethod.getInstance());
        spannable.setSpan(new TextClick(), 24, 28
                , Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvBind.setText(spannable);
    }

    /**
     * 获取数据
     */
    private void getData() {
        StringRequest request = new StringRequest(Request.Method.POST, Urls.BIND_ZFB_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {
                                JSONObject j2 = j1.optJSONObject("data");
                                if (j2 == null) {
                                    //没有绑定支付宝
                                    tvUnBind.setVisibility(View.VISIBLE);
                                    tvBind.setVisibility(View.GONE);
                                } else {
                                    //绑定了支付宝
                                    tvUnBind.setVisibility(View.GONE);
                                    tvBind.setVisibility(View.VISIBLE);

                                    zfb = j2.getString("account");
                                    name = j2.getString("name");
                                    etZFB.setText(zfb);
                                    etName.setText(name);
                                }
                            } else {
                                CommonMethod.toast(BindZFBActivity.this, "访问数据失败");
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(BindZFBActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                return params;
            }
        };
        mQueue.add(request);
    }

    private class TextClick extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            //解除绑定
            CommonMethod.showLoadingDialog("正在加载...", BindZFBActivity.this);
            unBindZFB();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(Color.RED);
            ds.setUnderlineText(true);
        }
    }

    /**
     * 解绑支付宝
     */
    private void unBindZFB() {
        StringRequest request = new StringRequest(Request.Method.POST, Urls.CANCLE_BIND_ZFB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {
                                //解绑成功
                                getData();
                                zfb = "";
                                name = "";
                                etZFB.setText("");
                                etName.setText("");
                                showDialog("解绑成功");
                            } else {
                                showDialog("解绑失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(BindZFBActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("sign", sign);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 输入的信息是否合法
     */
    private boolean isInputOk() {
        if (TextUtils.isEmpty(zfb)) {
            showHint("请输入支付宝账号");
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            showHint("请输入真实姓名");
            return false;
        }
        return true;
    }

    /**
     * 显示错误提示信息
     */
    private void showHint(String content) {
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(content);
    }
}
