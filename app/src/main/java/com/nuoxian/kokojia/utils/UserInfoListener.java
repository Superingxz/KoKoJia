package com.nuoxian.kokojia.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.activity.BindAccountActivity;
import com.nuoxian.kokojia.activity.LoginAndRegistActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.ProgressValues;
import com.nuoxian.kokojia.http.Urls;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/26.
 */
public class UserInfoListener implements IUiListener {

    private Activity activity;
    private String openid;
    private RequestQueue mQueue = MyApplication.getRequestQueue();

    public UserInfoListener(Activity activity) {
        this.activity = activity;
    }

    public UserInfoListener(Activity activity, String openid) {
        this.activity = activity;
        this.openid = openid;
    }

    @Override
    public void onComplete(Object o) {
        try {
            JSONObject j1 = new JSONObject(o.toString());
            String nickname = j1.optString("nickname");
            String figureurl_qq_2 = j1.optString("figureurl_qq_2");
            //发送用户信息给服务器
            sendUserInfo(nickname,figureurl_qq_2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(UiError uiError) {
        CommonMethod.showAlerDialog("登录结果",uiError.toString(),activity);
    }

    @Override
    public void onCancel() {
        CommonMethod.showAlerDialog("登录结果","您取消了登录",activity);
    }

    /**
     * 发送用户信息给服务器
     */
    private void sendUserInfo(final String nickname, final String avatar){
        StringRequest request = new StringRequest(Request.Method.POST, Urls.THIRD_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){//登录成功
                                String uid = j1.getString("data");
                                //保存uid
                                SharedPreferences sp = activity.getSharedPreferences("flag", activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("data", uid);
                                editor.commit();
                                //发送广播，通知我的页面和课程详情页面重新加载数据
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
                                CommonMethod.showAlerDialog("登录结果","登录失败",activity);
                                if(MyApplication.mTencent!=null){
                                    //退出qq登录
                                    MyApplication.mTencent.logout(activity);
                                }
                            }
                            if(MyApplication.mTencent!=null){
                                //退出qq登录
                                MyApplication.mTencent.logout(activity);
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(activity);
                CommonMethod.dismissLoadingDialog();
                if(MyApplication.mTencent!=null){
                    //退出qq登录
                    MyApplication.mTencent.logout(activity);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("openid",openid);
                params.put("type",CommonValues.LOGIN_TYPE_QQ);
                params.put("nickname",nickname);
                params.put("avatar",avatar);
                return params;
            }
        };
        mQueue.add(request);
    }
}
