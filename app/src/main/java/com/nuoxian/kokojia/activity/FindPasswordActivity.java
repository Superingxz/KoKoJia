package com.nuoxian.kokojia.activity;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
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
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 找回登录密码
 */
public class FindPasswordActivity extends AutoLayoutActivity implements View.OnClickListener {

    private TextView ivBack;
    private Button btnGetCode,btnCommit;
    private EditText etPhone,etCode;
    private String phone,code="",inputCode;
    private TextView tvHint;
    private RequestQueue mQueue;
    private Thread thread;
    private boolean run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        mQueue = MyApplication.getRequestQueue();
        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        //初始化视图
        initView();
        //获取用户输入的电话号码
        etPhone.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                phone = s + "";
            }
        });
        //获取用户输入的验证码
        etCode.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                inputCode = s + "";
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView(){
        Typeface tf = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_find_password_back);
        ivBack.setTypeface(tf);
        btnCommit = (Button) findViewById(R.id.btn_find_password_commit);
        btnGetCode = (Button) findViewById(R.id.btn_find_password_get_code);
        etPhone = (EditText) findViewById(R.id.et_find_password_phone);
        etCode = (EditText) findViewById(R.id.et_find_password_identifying_code);
        tvHint = (TextView) findViewById(R.id.tv_find_password_hint);

        //设置监听
        ivBack.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        btnGetCode.setOnClickListener(this);
    }

    /**
     * 点击监听
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_find_password_back://返回
                finish();
                break;
            case R.id.btn_find_password_get_code://获取验证码
                if(!CommonMethod.isMobileNO(phone)){
                    tvHint.setVisibility(View.VISIBLE);
                    tvHint.setText("手机号码为空或不正确!");
                }else{
                    run = true;
                    tvHint.setVisibility(View.GONE);
                    //获取验证码
                    getCode(Urls.FIND_PASSWORD_CODE);
                    //按钮计时
                    changeButton();
                }
                break;
            case R.id.btn_find_password_commit://提交
                if(isInforOk()){//如果填写的信息正确
                    //关闭登录界面
                    if(LoginActivity.instance!=null){
                        LoginActivity.instance.finish();
                    }
                    //关闭登录和注册界面
                    if(LoginAndRegistActivity.instance!=null){
                        LoginAndRegistActivity.instance.finish();
                    }
                    //发送验证信息给服务器
                    sendCode(Urls.FIND_PASSWORD_SECOND);
                    CommonMethod.showLoadingDialog("正在提交请稍后...",this);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 判断输入的信息是否正确
     * @return
     */
    private boolean isInforOk(){
        if(CommonMethod.isMobileNO(phone)){//手机号码输入正确
            if(!TextUtils.isEmpty(inputCode)){//验证码不为空
                if(inputCode.equals(code)){//输入的验证码和获取的验证码一致
                    tvHint.setVisibility(View.GONE);
                    return true;
                }else{
                    tvHint.setVisibility(View.VISIBLE);
                    tvHint.setText("验证码不正确!");
                }
            }else{
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText("验证码不能为空!");
            }
        }else{
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("手机号码为空或不正确!");
        }
        return false;
    }

    /**
     * 获取验证码
     */
    private void getCode(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //获取验证码
                                code = j1.getString("data");
                            }else{
                                //显示错误信息
                                tvHint.setVisibility(View.VISIBLE);
                                tvHint.setText(j1.optString("msg"));
                                //停止计时
                                run=false;
                                btnGetCode.setText("获取验证码");
                                btnGetCode.setEnabled(true);
                                btnGetCode.setBackgroundColor(0xfffd9a34);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.toast(FindPasswordActivity.this,"获取验证码失败,请检查网络!");
                //停止计时
                run=false;
                btnGetCode.setText("获取验证码");
                btnGetCode.setEnabled(true);
                btnGetCode.setBackgroundColor(0xfffd9a34);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name",phone);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 发送验证信息给服务器
     * @param url
     */
    private void sendCode(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                JSONObject j2 = j1.getJSONObject("data");
                                //跳转到设置密码页面
                                Intent intent = new Intent(FindPasswordActivity.this,SetPasswordActivity.class);
                                intent.putExtra("username",j2.getString("username"));
                                intent.putExtra("code",j2.getString("code"));
                                intent.putExtra("user_id", j2.getString("user_id"));
                                startActivity(intent);
                                CommonMethod.dismissLoadingDialog();
                                FindPasswordActivity.this.finish();
                            }else{
                                CommonMethod.toast(FindPasswordActivity.this,j1.optString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.toast(FindPasswordActivity.this,"提交信息失败!");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",phone);
                params.put("code",inputCode);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取验证码按钮计时
     */
    private void changeButton(){
        //设置按钮不可点击并为灰色
        btnGetCode.setEnabled(false);
        btnGetCode.setBackgroundColor(0xffbebebe);
        btnGetCode.setText("90s后重新获取");
        //修改button
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1){
                    if(msg.arg1>=0&&msg.arg2==1&&run){
                        btnGetCode.setText(msg.arg1+"s后重新获取");
                    }else{
                        //重新获取验证码
                        code = "";
                        btnGetCode.setText("获取验证码");
                        btnGetCode.setEnabled(true);
                        btnGetCode.setBackgroundColor(0xfffd9a34);
                    }
                }
            }
        };
        //计时
        thread = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                int i=89;
                while (run){
                    SystemClock.sleep(1000);
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.arg1=i;
                    msg.arg2=1;
                    handler.sendMessage(msg);
                    i--;
                    if(i<0){
                        run = false;
                    }
                }
            }
        });
        thread.start();
    }
}
