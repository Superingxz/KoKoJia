package com.nuoxian.kokojia.activity;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.fragment.MineFragment;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册页面
 */
public class RegistActivity extends AutoLayoutActivity implements View.OnClickListener {

    private TextView tvHint, tvLogin,ivBack;
    private EditText etPhone, etSetPassword, etConfirmPassword, etCode, etUserName;
    private ImageView ivAgree;
    private Button btnRegist, btnGetCode, btnReGet;
    private String phone, code, userName, inputCode, inputPassword, rePassword;
    private RequestQueue mQueue;
    private List<EditText> editTexts = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        mQueue = MyApplication.getRequestQueue();
        initView();
        initList();
        getEditTextPhone();//获取电话号码
        getEditTextCode();//获取验证码
        getEditTextUserName();//获取用户昵称
        getEditTextPassword();//获取密码
        getEditTextRePassword();//获取确认密码
    }

    /**
     * 初始化EditText集合，为关闭软件盘使用
     */
    private void initList(){
        editTexts.add(etPhone);
        editTexts.add(etSetPassword);
        editTexts.add(etConfirmPassword);
        editTexts.add(etCode);
        editTexts.add(etUserName);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        Typeface tf = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_regist_back);
        ivBack.setTypeface(tf);
        tvHint = (TextView) findViewById(R.id.tv_regist_hint);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        etPhone = (EditText) findViewById(R.id.et_regist_phone);
        etSetPassword = (EditText) findViewById(R.id.et_regist_set_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_regist_confirm_password);
        etCode = (EditText) findViewById(R.id.et_regist_identifying_code);
        etUserName = (EditText) findViewById(R.id.et_regist_set_username);
        ivAgree = (ImageView) findViewById(R.id.iv_regist_agree);
        ivAgree.setTag("agree");
        btnRegist = (Button) findViewById(R.id.btn_regist);
        btnGetCode = (Button) findViewById(R.id.btn_get_code);
        btnReGet = (Button) findViewById(R.id.btn_re_get);

        ivBack.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        btnRegist.setOnClickListener(this);
        ivAgree.setOnClickListener(this);
        btnGetCode.setOnClickListener(this);

    }

    /**
     * 监听回调
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_regist_back://返回
                this.finish();
                break;
            case R.id.tv_login://登录
                CommonMethod.startActivity(this,LoginActivity.class);
                this.finish();
                break;
            case R.id.btn_regist://注册
                for(int i=0;i<editTexts.size();i++){//关闭软键盘
                    if(editTexts.get(i).isFocusable()){
                        CommonMethod.hideInput(this,editTexts.get(i));
                        break;
                    }
                }
                if (registIsOk()) {//如果信息填写正确
                    //关闭登录注册页面
                    if(LoginAndRegistActivity.instance!=null){
                        LoginAndRegistActivity.instance.finish();
                    }
                    tvHint.setVisibility(View.GONE);
                    //提交注册信息
                    sendRegistInfo();
                }
                break;
            case R.id.iv_regist_agree://同意
                if ("agree".equals(ivAgree.getTag())) {
                    ivAgree.setImageResource(R.mipmap.square_frame1);
                    ivAgree.setTag("unAgree");
                } else if ("unAgree".equals(ivAgree.getTag())) {
                    ivAgree.setImageResource(R.mipmap.square_frame2);
                    ivAgree.setTag("agree");
                }
                break;
            case R.id.btn_get_code://点击获取验证码
                if (phone == null) {
                    tvHint.setVisibility(View.VISIBLE);
                    tvHint.setText("手机号码不能为空");
                } else if (CommonMethod.isMobileNO(phone)) {
                    tvHint.setVisibility(View.GONE);
                    //设置按钮不可点击
                    btnGetCode.setEnabled(false);
                    //获取验证码
                    getCode(Urls.GET_CODE_URL);
                } else {
                    tvHint.setVisibility(View.VISIBLE);
                    tvHint.setText("输入的手机号码格式不正确");
                }
                break;
        }
    }

    /**
     * 提交注册信息
     */
    private void sendRegistInfo() {
        StringRequest request = new StringRequest(Request.Method.POST, Urls.REGIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){//注册成功
                                //保存信息
                                SharedPreferences sp = getSharedPreferences("flag", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("data", j1.getString("data"));
                                editor.commit();
                                //发送广播，通知我的页面重新加载数据
                                BuyResult result = new BuyResult();
                                result.setBuyStatus("ok");
                                EventBus.getDefault().post(result);

                                RegistActivity.this.finish();
                            }else if("0".equals(j1.getString("status"))){
                                Toast.makeText(RegistActivity.this,j1.getString("msg"),Toast.LENGTH_SHORT).show();
                            } else{//注册失败
                                Toast.makeText(RegistActivity.this,"注册失败,请重新注册",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistActivity.this,"请检查网络！",Toast.LENGTH_SHORT).show();
                    }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",phone);
                params.put("nickname",userName);
                params.put("code",inputCode);
                params.put("password",rePassword);

                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取输入的手机号码
     */
    private void getEditTextPhone() {
        etPhone.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                phone = s + "";
            }
        });
    }

    /**
     * 获取输入的验证码
     */
    private void getEditTextCode() {
        etCode.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                inputCode = s + "";
            }
        });
    }

    /**
     * 获取输入的用户名
     */
    private void getEditTextUserName() {
        etUserName.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                userName = s + "";
            }
        });
    }

    /**
     * 获取输入的密码
     */
    private void getEditTextPassword() {
        etSetPassword.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                inputPassword = s + "";
            }
        });
    }

    /**
     * 获取输入的确认密码
     */
    private void getEditTextRePassword() {
        etConfirmPassword.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                rePassword = s + "";
            }
        });
    }

    /**
     * 获取验证码
     * @param url
     */
    private void getCode(String url) {
        StringRequest request = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //设置获取验证码按钮可点击
                btnGetCode.setEnabled(true);
                try {
                    JSONObject j1 = new JSONObject(response);
                    if ("1".equals(j1.getString("status"))) {//可以得到验证码
                        //按钮不能再点击
                        changeButton();
                        code = j1.getString("data");
                    } else if ("0".equals(j1.getString("status"))) {//不能得到验证码
                        Toast.makeText(RegistActivity.this, j1.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistActivity.this, "网络请求失败，请检查网络", Toast.LENGTH_SHORT).show();
                //设置获取验证码按钮可点击
                btnGetCode.setEnabled(true);
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
     * 判断注册信息是否正确
     * @return
     */
    private boolean registIsOk() {
        if (TextUtils.isEmpty(phone)) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("手机号码不能为空");
            return false;
        } else if (!CommonMethod.isMobileNO(phone)) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("手机号码不正确");
            return false;
        }
        if (TextUtils.isEmpty(inputCode)) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("验证码不能为空");
            return false;
        } else if (!inputCode.equals(code)) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("验证码不正确");
            return false;
        }
        if (TextUtils.isEmpty(userName)) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("用户名不能为空");
            return false;
        }
        if (TextUtils.isEmpty(inputPassword)) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请设置密码");
            return false;
        }
        if (TextUtils.isEmpty(rePassword)) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请确认密码");
            return false;
        }
        if (!inputPassword.equals(rePassword)) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("两次输入的密码不一致");
            return false;
        }
        if ("unAgree".equals(ivAgree.getTag())) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请选择我同意“服务条款”");
            return false;
        }
        return true;
    }

    /**
     * 验证码按钮计时
     */
    private void changeButton() {
        btnReGet.setVisibility(View.VISIBLE);
        btnReGet.setText("重新获取(90s)");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == 0) {
                    //隐藏重新获取按钮
                    btnReGet.setVisibility(View.GONE);
                    //将保存的验证码为空
                    code = null;
                } else {
                    btnReGet.setText("重新获取(" + msg.arg1 + "s)");
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 89; i >= 0; i--) {
                    SystemClock.sleep(1000);
                    Message message = Message.obtain();
                    message.arg1 = i;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
}
