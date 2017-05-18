package com.nuoxian.kokojia.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.api.AccessTokenManager;
import com.baidu.api.Baidu;
import com.baidu.api.BaiduDialog;
import com.baidu.api.BaiduDialogError;
import com.baidu.api.BaiduException;
import com.nuoxian.kokojia.activity.BindAccountActivity;
import com.nuoxian.kokojia.activity.LoginAndRegistActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.ProgressValues;
import com.nuoxian.kokojia.http.Urls;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/29.
 */
public class BaiDuLogin {

    private Activity activity;
    private Baidu baidu;

    public BaiDuLogin(Activity activity,Baidu baidu) {
        this.activity = activity;
        this.baidu = baidu;
    }

    public void login(){
        baidu.init(activity);
        baidu.authorize(activity,
                false,//是否每次授权都强制登录
                true,//是否确认登录
                new BaiduDialog.BaiduDialogListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        AccessTokenManager atm = baidu.getAccessTokenManager();
                        String accessToken = atm.getAccessToken();
                        baidu.init(activity);
                        final String url = "https://openapi.baidu.com/rest/2.0/passport/users/getLoggedInUser?access_token="+accessToken;
                        CommonMethod.showLoadingDialog("正在加载...",activity);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String json = baidu.request(url, null, "GET");
                                    JSONObject j1 = new JSONObject(json);
                                    String uid = j1.getString("uid");
                                    String nickname = j1.getString("uname");
                                    String avatar = "http://tb.himg.baidu.com/sys/portrait/item/"+j1.getString("portrait");
                                    String openid = CommonMethod.getMd5Value(uid);
                                    //发送用户信息到服务器
                                    sendUserInfo(nickname, avatar, openid);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (BaiduException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onBaiduException(BaiduException e) {
                    }

                    @Override
                    public void onError(BaiduDialogError baiduDialogError) {
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }

    /**
     * 发送用户信息
     */
    private void sendUserInfo(final String nickname, final String avatar, final String openid){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.THIRD_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.dismissLoadingDialog();
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){//登录成功
                                String uid = j1.getString("data");
                                //保存uid
                                SharedPreferences sp = activity.getSharedPreferences("flag", activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("data", j1.getString("data"));
                                editor.commit();
                                //发送广播，通知我的页面重新加载数据
                                BuyResult result = new BuyResult();
                                result.setBuyStatus("ok");
                                EventBus.getDefault().post(result);
                                //发送广播，通知我的下载页面重新加载
                                ProgressValues values = new ProgressValues();
                                values.setStatus("reload");
                                EventBus.getDefault().post(values);

                                activity.finish();
                                if(LoginAndRegistActivity.instance!=null){
                                    LoginAndRegistActivity.instance.finish();
                                }
                            }else if("2".equals(j1.getString("status"))){//跳转到绑定页面
                                String uid = j1.getString("account_id");
                                String nickname = j1.getString("nickname");
                                //绑定账号
                                Intent intent = new Intent(activity,BindAccountActivity.class);
                                intent.putExtra("uid",uid);
                                intent.putExtra("nickname",nickname);
                                activity.startActivity(intent);
                            } else{
                                //登录失败
                                CommonMethod.showAlerDialog("登录结果", "登录失败", activity);
                            }
                            //退出百度登录
                            if(baidu!=null){
                                baidu.clearAccessToken();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(activity);
                CommonMethod.dismissLoadingDialog();
                //退出百度登录
                if(baidu!=null){
                    baidu.clearAccessToken();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("openid",openid);
                params.put("type",CommonValues.LOGIN_TYPE_BAIDU);
                params.put("nickname",nickname);
                params.put("avatar",avatar);
                return params;
            }
        };
        mQueue.add(request);
    }
}
