package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.api.Baidu;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.LoginResult;
import com.nuoxian.kokojia.enterty.MyPackage;
import com.nuoxian.kokojia.enterty.ProgressValues;
import com.nuoxian.kokojia.fragment.MineFragment;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.BaiDuLogin;
import com.nuoxian.kokojia.utils.BaseUiListener;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.nuoxian.kokojia.utils.UserInfoListener;
import com.nuoxian.kokojia.utils.WXLogin;
import com.nuoxian.kokojia.utils.WeiBoLogin;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录页面
 */
public class LoginActivity extends AutoLayoutActivity implements View.OnClickListener {

    private ImageView ivRemenber;
    private EditText etPhone,etPassword;
    private TextView tvRegist,tvHint,tvForgetPassword,ivBack;
    private Button btnLogin;
    private String phone,password;
    private RequestQueue mQueue;
    private LinearLayout llRemenberPassword;
    private AutoLinearLayout allQQ,allWX,allBaidu;
    public static LoginActivity instance;
    private boolean isUpdate = false;
    private BaseUiListener uiListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //使用Font Awesome
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.ll_login), iconFont);
        //设置标题栏的颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        instance = this;
        mQueue = MyApplication.getRequestQueue();
        EventBus.getDefault().register(this);

        initView();
        //查看是否有保存的账号
        SharedPreferences sp = getSharedPreferences("flag", MODE_PRIVATE);
        if(!TextUtils.isEmpty(sp.getString("userphone",""))){
            ivRemenber.setImageResource(R.mipmap.square_frame2);
            llRemenberPassword.setTag("remember");
            //有，自动填写账号密码
            etPhone.setText(sp.getString("userphone",""));
            etPassword.setText("password");
            phone = sp.getString("userphone","");
            password = "password";
        }
        getEditTextPhone();//获取账号
        getEditTextPassword();//获取密码
    }

    /**
     * 初始化视图
     */
    private void initView() {
        ivBack =(TextView) findViewById(R.id.iv_login_back);
        ivRemenber = (ImageView) findViewById(R.id.iv_login_remember);
        llRemenberPassword = (LinearLayout) findViewById(R.id.ll_login_remember_password);
        llRemenberPassword.setTag("unremember");
        etPhone = (EditText) findViewById(R.id.et_Login_phone);
        etPassword = (EditText) findViewById(R.id.et_login_password);
        tvHint = (TextView) findViewById(R.id.tv_login_hint);
        tvRegist = (TextView) findViewById(R.id.tv_regist);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        allBaidu = (AutoLinearLayout) findViewById(R.id.all_login_by_baidu);
        allQQ = (AutoLinearLayout) findViewById(R.id.all_login_by_qq);
        allWX = (AutoLinearLayout) findViewById(R.id.all_login_by_wx);
        //设置监听
        ivBack.setOnClickListener(this);
        tvRegist.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        llRemenberPassword.setOnClickListener(this);
        allQQ.setOnClickListener(this);
        allWX.setOnClickListener(this);
        allBaidu.setOnClickListener(this);
    }

    /**
     * 点击监听回调
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_login_back://返回
                this.finish();
                break;
            case R.id.tv_regist://跳转到注册
                CommonMethod.startActivity(this,RegistActivity.class);
                this.finish();
                break;
            case R.id.ll_login_remember_password://点击记住密码
                if("unremember".equals(llRemenberPassword.getTag())){
                    //如果是没有记住密码状态就变成记住密码状态
                    ivRemenber.setImageResource(R.mipmap.square_frame2);
                    llRemenberPassword.setTag("remember");
                }else{
                    //如果是记住密码状态就变成没有记住密码状态
                    ivRemenber.setImageResource(R.mipmap.square_frame1);
                    llRemenberPassword.setTag("unremember");
                }
                break;
            case R.id.tv_forget_password://忘记密码
                CommonMethod.startActivity(this, FindPasswordActivity.class);
                break;
            case R.id.all_login_by_baidu://百度登录
                CommonMethod.showLoadingDialog("正在加载...",this);
                StringRequest request = new StringRequest(Request.Method.POST, Urls.BAIDU_APPID,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                CommonMethod.dismissLoadingDialog();
                                try {
                                    JSONObject j1 = new JSONObject(response);
                                    //关闭登录注册页面
                                    if(LoginAndRegistActivity.instance!=null){
                                        LoginAndRegistActivity.instance.finish();
                                    }
                                    //初始化百度
                                    MyApplication.baidu = new Baidu(j1.getString("data"),instance);
                                    BaiDuLogin baiDuLogin = new BaiDuLogin(instance,MyApplication.baidu);
                                    baiDuLogin.login();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonMethod.loadFailureToast(instance);
                        CommonMethod.dismissLoadingDialog();
                    }
                });
                mQueue.add(request);
                break;
            case R.id.all_login_by_qq://QQ登录
                //关闭登录注册页面
                if(LoginAndRegistActivity.instance!=null){
                    LoginAndRegistActivity.instance.finish();
                }
                if (!MyApplication.mTencent.isSessionValid())
                {
                    //显示正在加载
                    showDialogThreeSecond();
                    //登录
                    String Scope = "all";
                    uiListener = new BaseUiListener(this);
                    MyApplication.mTencent.login(this, Scope, uiListener);
                }
                break;
            case R.id.all_login_by_wx://微信登录
                //关闭登录注册页面
                if(LoginAndRegistActivity.instance!=null){
                    LoginAndRegistActivity.instance.finish();
                }
                if(MyApplication.wx==null){
                    MyApplication.wx = WXAPIFactory.createWXAPI(this, CommonValues.APP_ID_WEIXIN, true);
                    MyApplication.wx.registerApp(CommonValues.APP_ID_WEIXIN);
                }
                //检测是否安装了微信
                if(MyApplication.wx.isWXAppInstalled()){
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "kokojia_chat_test";
                    MyApplication.wx.sendReq(req);
                    //显示正在加载2秒钟
                    showDialogThreeSecond();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("请您先安装微信");
                    builder.setCancelable(true);
                    builder.show();
                }
                break;
            case R.id.btn_login://点击登录
                //关闭软键盘
                if(etPhone.isFocusable()){
                    CommonMethod.hideInput(this,etPhone);
                }else if(etPassword.isFocusable()){
                    CommonMethod.hideInput(this,etPassword);
                }
                if(loginIsOk()){
                    //关闭登录注册页面
                    if(LoginAndRegistActivity.instance!=null){
                        LoginAndRegistActivity.instance.finish();
                    }
                    tvHint.setVisibility(View.GONE);
                    //显示正在登录
                    CommonMethod.showLoadingDialog("正在登录...", this);
                    //设置登录按钮不可点击
                    btnLogin.setEnabled(false);
                    btnLogin.setBackgroundColor(Color.GRAY);
                    SharedPreferences sp = getSharedPreferences("flag", MODE_PRIVATE);
                    //如果有保存的账号说明之前是记住密码的
                    if(!TextUtils.isEmpty(sp.getString("userphone",""))&&!isUpdate){
                        //输入的和保存的一致
                        if(phone.equals(sp.getString("userphone",""))){
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("data", sp.getString("userid", "0"));
                            editor.commit();
                            //发送广播，通知我的页面重新加载
                            BuyResult result = new BuyResult();
                            result.setBuyStatus("ok");
                            EventBus.getDefault().post(result);
                            //发送广播，通知我的下载页面重新加载
                            ProgressValues values = new ProgressValues();
                            values.setStatus("reload");
                            EventBus.getDefault().post(values);

                            LoginActivity.this.finish();
                            CommonMethod.dismissLoadingDialog();
                            //判断是否选择了记住密码
                            if("unremember".equals(llRemenberPassword.getTag())){
                                //没有记住密码
                                SharedPreferences sp1 = getSharedPreferences("flag",MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sp1.edit();
                                editor1.putString("userphone","");
                                editor1.putString("userid","0");
                                editor1.commit();
                            }
                        }else{
                            //没有记住密码
                            login();
                        }
                    }else{//没有保存的账号
                        login();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 获取输入的电话号码
     */
    private void getEditTextPhone(){
        etPhone.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                phone = s + "";
            }
        });
    }

    /**
     * 获取输入的密码
     */
    private void getEditTextPassword(){
        etPassword.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                //重新写入了密码
                password = s + "";
                isUpdate = true;
            }
        });
    }

    /**
     * 显示正在加载3秒钟
     */
    private void showDialogThreeSecond(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在加载...");
        dialog.setCancelable(false);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //腾讯
        Tencent.onActivityResultData(requestCode, resultCode, data, uiListener);
    }

    /**
     * 是否完善登录信息
     * @return
     */
    private boolean loginIsOk(){
        if(TextUtils.isEmpty(phone)){
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请输入账号");
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
     * 登录
     */
    private void login(){
        StringRequest request = new StringRequest(Request.Method.POST, Urls.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){//登录成功
                                //保存用户id
                                SharedPreferences sp = getSharedPreferences("flag", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("data", j1.getString("data"));
                                if("unremember".equals(llRemenberPassword.getTag())){
                                    //如果没有记住密码
                                    editor.putString("userphone","");
                                    editor.putString("userid","0");
                                }else{
                                    //如果记住密码了就保存用户账号
                                    editor.putString("userphone",phone);
                                    editor.putString("userid",j1.getString("data"));
                                }
                                editor.commit();
                                //发送广播，通知我的页面重新加载
                                BuyResult result = new BuyResult();
                                result.setBuyStatus("ok");
                                EventBus.getDefault().post(result);
                                //发送广播，通知我的下载页面重新加载
                                ProgressValues values = new ProgressValues();
                                values.setStatus("reload");
                                EventBus.getDefault().post(values);

                                LoginActivity.this.finish();
                            }else if("0".equals(j1.getString("status"))){//登录失败
                                tvHint.setText(j1.getString("msg"));
                                tvHint.setVisibility(View.VISIBLE);
                            }
                            //隐藏正在登录
                            CommonMethod.dismissLoadingDialog();
                            //设置登录按钮可点击
                            btnLogin.setEnabled(true);
                            btnLogin.setBackgroundColor(0xff02b81b);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //隐藏正在登录
                            CommonMethod.dismissLoadingDialog();
                            //设置登录按钮可点击
                            btnLogin.setEnabled(true);
                            btnLogin.setBackgroundColor(0xff02b81b);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(LoginActivity.this);
                //隐藏正在登录
                CommonMethod.dismissLoadingDialog();
                //设置登录按钮可点击
                btnLogin.setEnabled(true);
                btnLogin.setBackgroundColor(0xff02b81b);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",phone);
                params.put("password",password);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 接收正在登录的方式
     * @param event
     */
    public void onEventMainThread(LoginResult event) {
        if("wx".equals(event.getLoginWay())){//微信登录
            WXLogin login = new WXLogin(this,mQueue);
            login.login();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(MyApplication.mTencent!=null){
            //退出qq登录
            MyApplication.mTencent.logout(this);
        }
        if(MyApplication.baidu!=null){
            //退出百度登录
            MyApplication.baidu.clearAccessToken();
        }
        EventBus.getDefault().unregister(this);
    }
}
