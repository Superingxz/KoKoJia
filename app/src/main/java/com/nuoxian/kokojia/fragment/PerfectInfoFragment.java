package com.nuoxian.kokojia.fragment;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.BindAccountActivity;
import com.nuoxian.kokojia.activity.LoginActivity;
import com.nuoxian.kokojia.activity.ServiceProvisionActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.ProgressValues;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/8.
 */
public class PerfectInfoFragment extends Fragment implements View.OnClickListener {

    private EditText etAccount, etCode, etUserName, etPassword, etComfirmPassword;
    private TextView tvHint, tvCheck, tvProvision, tvRegist, tvCode;
    private AutoLinearLayout llPhone, llCheck;
    private String uid, nickname, account, inputCode="",code="", password, rePassword;
    private RequestQueue mQueue;
    private String type = "1";
    private boolean run = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfect_info, container, false);

        Typeface tf = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(view.findViewById(R.id.layout_perfect), tf);

        mQueue = MyApplication.getRequestQueue();
        uid = getArguments().getString("uid");
        nickname = getArguments().getString("nickname");
        initView(view);
        getAccount();//获取账号
        getInputCode();//获取验证码
        getNickname();//获取昵称
        getPassword();//获取密码
        getConfirmPassword();//确认密码

        return view;
    }

    private void initView(View v) {
        etAccount = (EditText) v.findViewById(R.id.et_perfect_emaile_or_phone);
        etCode = (EditText) v.findViewById(R.id.et_perfect_code);
        etUserName = (EditText) v.findViewById(R.id.et_perfect_username);
        etUserName.setText(nickname);
        etPassword = (EditText) v.findViewById(R.id.et_perfect_password);
        etComfirmPassword = (EditText) v.findViewById(R.id.et_perfect_confirm_password);
        tvCode = (TextView) v.findViewById(R.id.tv_perfect_code);
        tvHint = (TextView) v.findViewById(R.id.tv_perfect_info_hint);
        tvCheck = (TextView) v.findViewById(R.id.tv_perfect_check);
        tvCheck.setTag("checked");
        tvProvision = (TextView) v.findViewById(R.id.tv_perfect_service_provision);
        tvRegist = (TextView) v.findViewById(R.id.tv_perfect_regist);
        llPhone = (AutoLinearLayout) v.findViewById(R.id.ll_perfect_phone);
        llCheck = (AutoLinearLayout) v.findViewById(R.id.ll_perfect_check);
        //设置监听
        tvCode.setOnClickListener(this);
        tvProvision.setOnClickListener(this);
        tvRegist.setOnClickListener(this);
        llCheck.setOnClickListener(this);
        //设置etAccount的监听
        etAccount.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                account = s + "";
                //判断用户输入的账号
                if (CommonMethod.isMobileNO(account)) {
                    //如果是手机号码，显示获取验证码
                    llPhone.setVisibility(View.VISIBLE);
                } else if (CommonMethod.isEmail(account)) {
                    //如果是邮箱，隐藏获取验证码
                    llPhone.setVisibility(View.GONE);
                } else {
                    // 输入不正确,隐藏获取验证码
                    llPhone.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_perfect_code://获取验证码
                //按钮计时
                changeButton();
                getCode(Urls.GET_CODE_URL);
                break;
            case R.id.tv_perfect_service_provision://跳转到服务条款详情
                CommonMethod.startActivity(getContext(), ServiceProvisionActivity.class);
                break;
            case R.id.tv_perfect_regist://注册
                if(isInputOk()){//输入的信息正确
                    tvHint.setVisibility(View.GONE);
                    //设置重新获取验证码
                    run = false;
                    //注册
                    regist(Urls.BIND_EMAIL_OR_PHONE);
                }
                break;
            case R.id.ll_perfect_check://选择服务条款
                if("checked".equals(tvCheck.getTag())){
                    //设置不选中
                    tvCheck.setText(R.string.unChecked);
                    tvCheck.setTag("unchecked");
                }else{
                    //设置选中
                    tvCheck.setText(R.string.checked);
                    tvCheck.setTag("checked");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证码
     *
     * @param url
     */
    private void getCode(String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);
                    if ("1".equals(j1.getString("status"))) {//可以得到验证码
                        code = j1.getString("data");
                    } else if ("0".equals(j1.getString("status"))) {//不能得到验证码
                        //停止计时
                        run = false;

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
                Toast.makeText(getContext(), "网络请求失败，请检查网络", Toast.LENGTH_SHORT).show();
                //中断计时
                run = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", account);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 注册
     * @param url
     */
    private void regist(String url){
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){//登录成功
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
                            }else{//出错
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
                params.put("type",type);
                params.put("username",account);
                params.put("password",password);
                params.put("nickname",nickname);
                params.put("code",inputCode);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 获取用户输入的账号
     */
    private void getAccount() {
        etAccount.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                account = s + "";
            }
        });
    }

        /**
         * 获取用户输入的验证码
         */

    private void getInputCode() {
        etCode.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                inputCode = s + "";
            }
        });
    }

    /**
     * 获取用户输入的昵称
     */
    private void getNickname() {
        etUserName.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                nickname = s + "";
            }
        });
    }

    /**
     * 获取用户输入的密码
     */
    private void getPassword() {
        etPassword.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                password = s + "";
            }
        });
    }

    /**
     * 获取确认密码
     */
    private void getConfirmPassword() {
        etComfirmPassword.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                rePassword = s + "";
            }
        });
    }

    /**
     * 获取验证码按钮计时
     */
    private void changeButton(){
        //设置按钮不可点击并为灰色
        tvCode.setEnabled(false);
        tvCode.setBackgroundResource(R.drawable.rectangle_solid_gray);
        tvCode.setText("90s后重新获取");
        //修改button
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1){
                    if(msg.arg1>=0&&msg.arg2==1&&run){
                        tvCode.setText(msg.arg1+"s后重新获取");
                    }else{
                        //重新获取验证码
                        code = "";
                        tvCode.setText("获取验证码");
                        tvCode.setEnabled(true);
                        tvCode.setBackgroundResource(R.drawable.rectangle_solid_orange);
                    }
                }
            }
        };
        //计时
        Thread thread = new Thread(new Runnable() {
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

    /**
     * 判断用户输入的信息是否完善
     * @return
     */
    private boolean isInputOk(){
        if(TextUtils.isEmpty(account)){//账号为空
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请输入手机号码或邮箱");
            return false;
        }
        if(!CommonMethod.isMobileNO(account)&&!CommonMethod.isEmail(account)){//账号既不是手机号码也不是邮箱
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请输入正确的手机号码或邮箱");
            return false;
        }
        if(View.VISIBLE==llPhone.getVisibility()){//显示了获取验证码
            type = "2";//绑定的手机
            if(TextUtils.isEmpty(inputCode)){//输入的验证码为空
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText("请输入验证码");
                return false;
            }
            if(!code.equals(inputCode)){//输入的验证码和获取的不一致
                tvHint.setVisibility(View.VISIBLE);
                tvHint.setText("输入的验证码不正确");
                return false;
            }
        }else{
            type = "1";//绑定邮箱
        }
        if(TextUtils.isEmpty(nickname)){//昵称为空
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请设置昵称");
            return false;
        }
        if(TextUtils.isEmpty(password)){//密码为空
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请输入密码");
            return false;
        }
        if(TextUtils.isEmpty(rePassword)){//确认密码为空
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请确认密码");
            return false;
        }
        if(!password.equals(rePassword)){//密码和确认密码不一致
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("两次输入的密码不一致,请重新输入");
            return false;
        }
        if("unchecked".equals(tvCheck.getTag())){//没选择同意服务条款
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText("请勾选同意服务条款");
            return false;
        }
        return true;
    }

}
