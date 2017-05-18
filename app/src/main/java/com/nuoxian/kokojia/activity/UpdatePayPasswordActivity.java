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
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.Code;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.TextChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改支付密码
 */
public class UpdatePayPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText etOldPasswd,etNewPasswd,etRePasswd,etCode;
    private TextView tvHint;
    private ImageView ivCode;
    private Button btnCommit;
    private String uid,newPasswd,rePasswd,code,inputCode,oldPasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_updata_password);
        //设置标题
        setTitle("修改支付密码");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePayPasswordActivity.this.finish();
            }
        });

        uid = CommonMethod.getUid(this);
        initView();
        //获取验证码
        ivCode.setImageBitmap(Code.getInstance().createBitmap());
        code = Code.getInstance().getCode();

        getOldPassword();
        getNewPassword();
        getRePassword();
        getInputCode();
    }

    private void initView(){
        etOldPasswd = (EditText) findViewById(R.id.et_old_pay_password);
        etNewPasswd = (EditText) findViewById(R.id.et_new_pay_password);
        etRePasswd = (EditText) findViewById(R.id.et_comfirm_pay_password);
        etCode = (EditText) findViewById(R.id.et_code);
        ivCode = (ImageView) findViewById(R.id.iv_get_code);
        btnCommit = (Button) findViewById(R.id.btn_commit_updata_pay_password);
        tvHint = (TextView) findViewById(R.id.tv_update_pay_password_hint);

        ivCode.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_get_code://更换验证码
                ivCode.setImageBitmap(Code.getInstance().createBitmap());
                code = Code.getInstance().getCode();
                break;
            case R.id.btn_commit_updata_pay_password://提交
                if(isInputOk()){
                    CommonMethod.showLoadingDialog("正在加载...",this);
                    tvHint.setVisibility(View.GONE);
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
        StringRequest request = new StringRequest(Request.Method.POST, Urls.UPDATA_PAY_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //修改密码成功
                                UpdatePayPasswordActivity.this.finish();
                                CommonMethod.toast(UpdatePayPasswordActivity.this,"修改成功");
                            }else {
                                //修改失败
                                CommonMethod.showAlerDialog("",j1.getString("msg"),UpdatePayPasswordActivity.this);
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(UpdatePayPasswordActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("old_password",oldPasswd);
                params.put("new_password",newPasswd);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 输入的信息是否正确
     * @return
     */
    private boolean isInputOk(){
        if(TextUtils.isEmpty(oldPasswd)){
            showHint("请输入当前密码");
            return false;
        }
        if(TextUtils.isEmpty(newPasswd)){
            showHint("请输入新密码");
            return false;
        }
        if(TextUtils.isEmpty(rePasswd)){
            showHint("请确认密码");
            return false;
        }
        if(!newPasswd.equals(rePasswd)){
            showHint("新密码和确认密码不一致，请重新输入");
            return false;
        }
        if(TextUtils.isEmpty(inputCode)){
            showHint("请输入验证码");
            return false;
        }
        if(!inputCode.equals(code.toLowerCase())&&!inputCode.equals(code.toUpperCase())){
            showHint("验证码错误，请重新输入");
            //获取验证码
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
     * 获取用户输入的新密码
     */
    private void getNewPassword(){
        etNewPasswd.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                newPasswd = s+"";
            }
        });
    }

    /**
     * 获取用户输入的当前密码
     */
    private void getOldPassword(){
        etOldPasswd.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                oldPasswd = s+"";
            }
        });
    }

    /**
     * 获取用户输入的确认密码
     */
    private void getRePassword(){
        etRePasswd.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                rePasswd = s+"";
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
