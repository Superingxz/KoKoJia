package com.nuoxian.kokojia.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
 * 重置密码
 */
public class ResetPayPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText etResetPasswd,etComfirmPasswd,etCode;
    private ImageView ivCode;
    private Button btnCommit;
    private TextView tvHint;
    private String uid,sign,resetPasswd,comfirmPasswd,inputCode,code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_find_pay_password);
        //设置标题
        setTitle("重置密码");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPayPasswordActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        sign = getIntent().getStringExtra("sign");

        initView();
        //获取验证
        ivCode.setImageBitmap(Code.getInstance().createBitmap());
        code = Code.getInstance().getCode();

        getResetPassword();
        getComfirmPasswd();
        getInputCode();
    }

    private void initView(){
        etResetPasswd = (EditText) findViewById(R.id.et_reset_pay_password);
        etComfirmPasswd = (EditText) findViewById(R.id.et_comfirm_pay_password);
        etCode = (EditText) findViewById(R.id.et_code);
        ivCode = (ImageView) findViewById(R.id.iv_get_code);
        btnCommit = (Button) findViewById(R.id.btn_commit_reset_pay_password);
        tvHint = (TextView) findViewById(R.id.tv_reset_pay_password_hint);

        ivCode.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_get_code://更换验证码
                //获取验证
                ivCode.setImageBitmap(Code.getInstance().createBitmap());
                code = Code.getInstance().getCode();
                break;
            case R.id.btn_commit_reset_pay_password://提交
                if(isInputOk()){
                    commit();
                }
                break;
        }
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
                                CommonMethod.toast(ResetPayPasswordActivity.this,"设置成功");
                                //关闭当前页
                                ResetPayPasswordActivity.this.finish();
                            }else{
                                //设置失败
                                CommonMethod.showAlerDialog("","设置失败,请重试!",ResetPayPasswordActivity.this);
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(ResetPayPasswordActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("sign",sign);
                params.put("password",resetPasswd);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 输入是否正确
     */
    private boolean isInputOk(){
        if(TextUtils.isEmpty(resetPasswd)){
            showHint("请重置密码");
            return false;
        }
        if(TextUtils.isEmpty(comfirmPasswd)){
            showHint("请确认密码");
            return false;
        }
        if(!resetPasswd.equals(comfirmPasswd)){
            showHint("两次输入的密码不一致，请重新输入");
            return false;
        }
        if(TextUtils.isEmpty(inputCode)){
            showHint("请输入验证码");
            return false;
        }
        if(!inputCode.equals(code.toLowerCase())&&!inputCode.equals(code.toUpperCase())){
            showHint("验证码错误！");
            //获取验证
            ivCode.setImageBitmap(Code.getInstance().createBitmap());
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

    /**
     * 获取用户输入的重置密码
     */
    private void getResetPassword(){
        etResetPasswd.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                resetPasswd = s+"";
            }
        });
    }

    /**
     * 获取用户输入的确认密码
     */
    private void getComfirmPasswd(){
        etComfirmPasswd.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                comfirmPasswd = s+"";
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
}
