package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.RefundDetails;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 退款详情页
 */
public class RefundDetailsActivity extends BaseActivity {

    private String id, uid, password, refuseContent;
    private TextView title, status, solveTime, agree, refuse, username2, orderNum, price, time, username, price2, reason, introduce, status2;
    private ImageView imageView;
    private AutoLinearLayout llRefund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_refund_details);

        CommonMethod.showLoadingDialog("正在加载...", this);
        //设置标题
        setTitle("退款详情");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefundDetailsActivity.this.finish();
            }
        });

        id = getIntent().getStringExtra("id");
        uid = CommonMethod.getUid(this);
        initView();
        //获取数据
        getData(Urls.REFUND_DETAILS);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        Typeface typeface = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.layout_refund_details), typeface);
        title = (TextView) findViewById(R.id.tv_refund_details_title);
        status = (TextView) findViewById(R.id.tv_refund_details_status);
        solveTime = (TextView) findViewById(R.id.tv_refund_details_solve_time);
        agree = (TextView) findViewById(R.id.tv_refund_details_agree);
        refuse = (TextView) findViewById(R.id.tv_refund_details_refuse);
        username2 = (TextView) findViewById(R.id.tv_refund_details_username2);
        orderNum = (TextView) findViewById(R.id.tv_refund_details_orderNum);
        price = (TextView) findViewById(R.id.tv_refund_details_price);
        time = (TextView) findViewById(R.id.tv_refund_details_time);
        username = (TextView) findViewById(R.id.tv_refund_details_username);
        price2 = (TextView) findViewById(R.id.tv_refund_details_price2);
        reason = (TextView) findViewById(R.id.tv_refund_details_reason);
        introduce = (TextView) findViewById(R.id.tv_refund_details_introduce);
        status2 = (TextView) findViewById(R.id.tv_refund_details_status2);
        imageView = (ImageView) findViewById(R.id.iv_refund_details);
        llRefund = (AutoLinearLayout) findViewById(R.id.ll_prepare_solve);

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同意退款
                AlertDialog.Builder builder = new AlertDialog.Builder(RefundDetailsActivity.this);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                View view = LayoutInflater.from(RefundDetailsActivity.this).inflate(R.layout.popup_edit, null);
                TextView title = (TextView) view.findViewById(R.id.tv_popup_title);
                title.setText("同意退款");
                EditText et = (EditText) view.findViewById(R.id.et_popup);
                et.setHint("请输入密码");
                //获取输入的密码
                et.addTextChangedListener(new TextChangeListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        super.afterTextChanged(s);
                        password = s+"";
                    }
                });

                TextView commit = (TextView) view.findViewById(R.id.tv_popup_commit);
                commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(password)) {
                            //提交
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("uid", uid);
                            params.put("id", id);
                            params.put("status", "1");
                            params.put("pass", password);
                            commit(params);
                        }
                    }
                });

                TextView cancle = (TextView) view.findViewById(R.id.tv_popup_cancle);
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialog.dismiss();
                        password = "";
                    }
                });

                dialog.setView(view);
                dialog.show();
            }
        });

        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拒绝退款
                AlertDialog.Builder builder = new AlertDialog.Builder(RefundDetailsActivity.this);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                View view = LayoutInflater.from(RefundDetailsActivity.this).inflate(R.layout.popup_edit, null);
                TextView title = (TextView) view.findViewById(R.id.tv_popup_title);
                title.setText("拒绝退款");
                EditText et = (EditText) view.findViewById(R.id.et_popup);
                et.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
                et.setHint("请输入拒绝的理由");
                //获取输入的拒绝内容
                et.addTextChangedListener(new TextChangeListener() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        super.afterTextChanged(s);
                        refuseContent = s + "";
                    }
                });
                TextView commit = (TextView) view.findViewById(R.id.tv_popup_commit);
                commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //提交
                        if(!TextUtils.isEmpty(refuseContent)){
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("uid", uid);
                            params.put("id", id);
                            params.put("status", "2");
                            params.put("content", refuseContent);
                            commit(params);
                        }
                    }
                });

                TextView cancle = (TextView) view.findViewById(R.id.tv_popup_cancle);
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialog.dismiss();
                        refuseContent = "";
                    }
                });

                dialog.setView(view);
                dialog.show();
            }
        });
    }

    /**
     * 提交同意退款或拒绝退款
     */
    private void commit(final Map<String, String> params) {
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.SOLVE_REFUND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("0".equals(j1.getString("status"))) {//操作失败
                                CommonMethod.showAlerDialog("操作失败", j1.optString("msg"), RefundDetailsActivity.this);
                            } else {
                                CommonMethod.showAlerDialog("", "操作成功", RefundDetailsActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.toast(RefundDetailsActivity.this, "操作失败!");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取数据
     *
     * @param url
     */
    private void getData(String url) {
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {
                                Gson gson = new Gson();
                                RefundDetails details = gson.fromJson(response, RefundDetails.class);
                                if ("1".equals(details.getData().getStatus())) {//显示同意和拒绝
                                    llRefund.setVisibility(View.VISIBLE);
                                } else {//隐藏同意和拒绝
                                    llRefund.setVisibility(View.GONE);
                                }
                                title.setText(details.getData().getTitle());//标题
                                status.setText(details.getData().getStatus_name());//状态
                                status2.setText(details.getData().getStatus_name());//状态
                                solveTime.setText("学生提交的该退款申请，请你在" + details.getData().getTime() + "前做出处理");//处理时间
                                username.setText(details.getData().getStudent_name());//学生名
                                username2.setText(details.getData().getStudent_name());//学生名
                                orderNum.setText(details.getData().getOrder_id());//订单号
                                price.setText(details.getData().getPrice());//价格
                                price2.setText(details.getData().getPrice());//价格
                                time.setText(details.getData().getCreate_time());//下单时间
                                reason.setText(details.getData().getReason());//退款原因
                                introduce.setText(details.getData().getContent());//退款说明
                                MyApplication.imageLoader.displayImage(details.getData().getImage_url(), imageView, MyApplication.options);
                            } else {
                                CommonMethod.toast(RefundDetailsActivity.this, "找不到数据~");
                                //关闭页面
                                RefundDetailsActivity.this.finish();
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(RefundDetailsActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("uid", uid);
                return params;
            }
        };
        mQueue.add(request);
    }
}
