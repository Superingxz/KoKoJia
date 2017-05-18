package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置登录密码
 */
public class SetPasswordActivity extends AutoLayoutActivity implements View.OnClickListener {

    private ImageView ivBack;
    private EditText etPassword,etRepassword;
    private Button btnCommit;
    private TextView tvHint;
    private String userName,code,userId,password,rePassword;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        mQueue = MyApplication.getRequestQueue();
        //获取需要的参数
        getParams();
        //初始化视图
        initView();
        //获取密码
        etPassword.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                password = s+"";
            }
        });
        //获取确认密码
        etRepassword.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                rePassword = s+"";
            }
        });
    }

    /**
     * 获取参数
     */
    private void getParams(){
        Intent intent = getIntent();
        userName = intent.getStringExtra("username");
        code = intent.getStringExtra("code");
        userId = intent.getStringExtra("user_id");
    }

    /**
     * 初始化视图
     */
    private void initView(){
        ivBack = (ImageView) findViewById(R.id.iv_set_password_back);
        etPassword = (EditText) findViewById(R.id.et_set_password);
        etRepassword = (EditText) findViewById(R.id.et_set_repassword);
        btnCommit = (Button) findViewById(R.id.btn_set_password_commit);
        tvHint = (TextView) findViewById(R.id.tv_set_password_hint);

        ivBack.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_set_password_back://返回
                finish();
                break;
            case R.id.btn_set_password_commit://提交
                if (isInfoOk()){
                    //提交信息
                    sendInfo(Urls.FIND_PASSWORD_THIRD);
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
    private boolean isInfoOk(){
        if(!TextUtils.isEmpty(password)){//输入的密码不为空
            if(!TextUtils.isEmpty(rePassword)){//确认密码不为空
                if(password.equals(rePassword)){
                    tvHint.setVisibility(View.GONE);
                    return true;
                }else{
                    tvHint.setVisibility(View.VISIBLE);
                    tvHint.setText("两次输入的密码不一致，请重新输入!");
                }
            }else{
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText("请再次输入密码!");
            }
        }else{
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请输入密码!");
        }
        return false;
    }

    /**
     * 提交信息
     */
    private void sendInfo(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //跳转到登录页面
                        AlertDialog.Builder builder = new AlertDialog.Builder(SetPasswordActivity.this);
                        builder.setTitle("设置结果");
                        builder.setMessage("设置成功");
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CommonMethod.startActivity(SetPasswordActivity.this, LoginActivity.class);
                                SetPasswordActivity.this.finish();
                            }
                        });
                        builder.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(SetPasswordActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",userName);
                params.put("code",code);
                params.put("password",rePassword);
                params.put("uid",userId);
                return params;
            }
        };
        mQueue.add(request);
    }
}
