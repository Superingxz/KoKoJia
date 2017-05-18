package com.nuoxian.kokojia.fragment;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.BindAccountActivity;
import com.nuoxian.kokojia.activity.LoginActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.ProgressValues;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/8.
 */
public class BindExistAccountFragment extends Fragment implements View.OnClickListener {

    private EditText etEmaile,etPassword;
    private TextView tvHint,btnCommit;
    private String email,password;
    private RequestQueue mQueue;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bind_exist_account, container, false);
        uid = getArguments().getString("uid");
        initView(view);
        mQueue = MyApplication.getRequestQueue();
        getAccount();//获取账号
        getPassword();//获取密码
        return view;
    }

    private void initView(View v){
        etEmaile = (EditText) v.findViewById(R.id.et_email);
        etPassword = (EditText) v.findViewById(R.id.et_password);
        btnCommit = (TextView) v.findViewById(R.id.btn_commit);
        tvHint = (TextView) v.findViewById(R.id.tv_hint);

        btnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_commit://提交
                if(isInfoOk()){
                    tvHint.setVisibility(View.GONE);
                    //发送信息到服务器
                    sendInfo();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取用户输入的账号
     */
    private void getAccount(){
        etEmaile.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                email = s+"";
            }
        });
    }

    /**
     * 获取用户输入的密码
     */
    private void getPassword(){
        etPassword.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                password = s+"";
            }
        });
    }

    /**
     * 判断用户输入的信息是否正确
     * @return
     */
    private boolean isInfoOk(){
        if(TextUtils.isEmpty(email)){
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请输入邮箱或手机号码");
            return false;
        }
        if(TextUtils.isEmpty(password)){
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请输入密码");
            return false;
        }
        return true;
    }

    /**
     * 发送用户信息
     */
    private void sendInfo(){
        StringRequest request = new StringRequest(Request.Method.POST, Urls.BIND_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){//提交成功
                                //保存用户id
                                SharedPreferences sp = getContext().getSharedPreferences("flag",getContext().MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("data",j1.getString("data"));
                                editor.commit();
                                //发送广播通知我的页面和课程详情页面
                                BuyResult result = new BuyResult();
                                result.setBuyStatus("ok");
                                EventBus.getDefault().post(result);
                                //发送广播，通知我的下载页面重新加载
                                ProgressValues values = new ProgressValues();
                                values.setStatus("reload");
                                EventBus.getDefault().post(values);
                                //关闭登录页面
                                if(LoginActivity.instance!=null){
                                    LoginActivity.instance.finish();
                                }
                                //关闭绑定账号页面
                                if(BindAccountActivity.instance!=null){
                                    BindAccountActivity.instance.finish();
                                }
                            }else{
                                //提交失败
                                tvHint.setVisibility(View.VISIBLE);
                                tvHint.setText(j1.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(getContext());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("username",email);
                params.put("password", password);
                return params;
            }
        };
        mQueue.add(request);
    }
}
