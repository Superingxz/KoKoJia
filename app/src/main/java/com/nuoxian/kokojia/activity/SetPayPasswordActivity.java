package com.nuoxian.kokojia.activity;

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
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.Code;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置支付密码
 */
public class SetPayPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText etPassword,etRePassword,etCode;
    private Button btnCommit;
    private ImageView ivCode;
    private String uid,sign,password,repassword,code,inputCode;
    private TextView tvHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_set_pay_password);
        //设置标题
        setTitle("设置支付密码");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPayPasswordActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        sign = getIntent().getStringExtra("sign");
        initView();
        //加载验证码图片
        ivCode.setImageBitmap(Code.getInstance().createBitmap());
        //获取验证码
        code = Code.getInstance().getCode();

        getInputCode();//获取用户输入的验证码
        getPassword();//获取用户输入的密码
        getRePassword();//获取用户输入的确认密码
    }

    private void initView(){
        etPassword = (EditText) findViewById(R.id.et_set_pay_password);
        etRePassword = (EditText) findViewById(R.id.et_comfirm_pay_password);
        etCode = (EditText) findViewById(R.id.et_code);
        ivCode = (ImageView) findViewById(R.id.iv_get_code);
        btnCommit = (Button) findViewById(R.id.btn_commit_pay_password);
        tvHint = (TextView) findViewById(R.id.tv_set_pay_password_hint);

        ivCode.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_get_code://验证码图片
                //加载验证码图片
                ivCode.setImageBitmap(Code.getInstance().createBitmap());
                //获取验证码
                code = Code.getInstance().getCode();
                break;
            case R.id.btn_commit_pay_password://提交
                if(isInputOk()){
                    CommonMethod.showLoadingDialog("正在加载...",this);
                    tvHint.setVisibility(View.GONE);
                    commit();
                }
                break;
        }
    }

    /**
     * 获取用户输入的密码
     */
    private void getPassword(){
        etPassword.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                password = s + "";
            }
        });
    }

    /**
     * 提交
     */
    private void commit(){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.SET_PAY_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){//设置成功
                                CommonMethod.toast(SetPayPasswordActivity.this,"设置成功");
                                //通知支付中心界面刷新
                                Bundle bundle = new Bundle();
                                bundle.putString("ok", "toRefresh");
                                EventBus.getDefault().post(bundle);
                                //关闭当前页
                                SetPayPasswordActivity.this.finish();
                            }else{
                                //设置失败
                                CommonMethod.showAlerDialog("","设置失败,请重试!",SetPayPasswordActivity.this);
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(SetPayPasswordActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("sign",sign);
                params.put("password",password);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取用户输入的确认密码
     */
    private void getRePassword(){
        etRePassword.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                repassword = s+"";
            }
        });
    }

    /**
     * 获取用户输入的验证码
     */
    private void getInputCode(){
        etCode.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                inputCode = s+"";
            }
        });
    }

    /**
     * 判断输入是否正确
     * @return
     */
    private boolean isInputOk(){
        if(TextUtils.isEmpty(password)){
            showHint("请设置密码");
            return false;
        }
        if(TextUtils.isEmpty(repassword)){
            showHint("请确认密码");
            return false;
        }
        if(!password.equals(repassword)){
            showHint("两次输入的密码不一致，请重新输入");
            return false;
        }
        if(TextUtils.isEmpty(inputCode)){
            showHint("请输入验证码");
            return false;
        }
        if(!inputCode.equals(code.toLowerCase())&&!inputCode.equals(code.toUpperCase())){//不区分大小写
            showHint("输入的验证码不正确");
            //加载验证码图片
            ivCode.setImageBitmap(Code.getInstance().createBitmap());
            //获取验证码
            code = Code.getInstance().getCode();
            return false;
        }
        return true;
    }

    /**
     * 显示错误信息
     * @param content
     */
    private void showHint(String content){
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(content);
    }
}
