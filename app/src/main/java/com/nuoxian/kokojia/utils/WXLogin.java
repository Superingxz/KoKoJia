package com.nuoxian.kokojia.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.activity.BindAccountActivity;
import com.nuoxian.kokojia.activity.LoginAndRegistActivity;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.ProgressValues;
import com.nuoxian.kokojia.http.Urls;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/26.
 */
public class WXLogin {

    private Activity activity;
    private RequestQueue mQueue;

    public WXLogin(Activity activity, RequestQueue mQueue) {
        this.activity = activity;
        this.mQueue = mQueue;
    }

    public void login() {
        CommonMethod.showLoadingDialog("正在加载...", activity);
        //如果是微信登录
        SharedPreferences sp = activity.getSharedPreferences(CommonValues.SP_WX_INFO, activity.MODE_PRIVATE);
        String code = sp.getString("code", "");
        //获取用户信息
        getUserInfo(code, Urls.WX_KEY_URL);
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo(final String code, String url) {
        //获取微信密钥
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);
                    String secret = j1.getString("data");
                    //获取access_token
                    getAccessToken(code, secret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(activity);
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }

    /**
     * 获取access_token
     *
     * @param code
     * @param secret
     */
    private void getAccessToken(String code, String secret) {
        StringRequest request = new StringRequest(Urls.getAccessTokenUrl(CommonValues.APP_ID_WEIXIN, secret, code),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            String access_token = j1.optString("access_token");
                            String expires_in = j1.optString("expires_in");
                            String refresh_token = j1.optString("refresh_token");
                            String openid = j1.optString("openid");
                            String scope = j1.optString("scope");
                            String errcode = j1.optString("errcode");
                            if ("".equals(errcode)) {
                                refreshAccessToken(CommonValues.APP_ID_WEIXIN, refresh_token);
                            } else {
                                //出错了
                                loginFaile();
                                CommonMethod.dismissLoadingDialog();
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
            }
        });
        mQueue.add(request);
    }

    /**
     * 刷新AccessToken有效期
     *
     * @param appid
     * @param refresh_token
     */
    private void refreshAccessToken(String appid, String refresh_token) {
        StringRequest request = new StringRequest(Urls.refreshAccessTokenUrl(appid, refresh_token),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j1 = null;
                        try {
                            j1 = new JSONObject(response);
                            String access_token = j1.optString("access_token");
                            String expires_in = j1.optString("expires_in");
                            String refresh_token = j1.optString("refresh_token");
                            String openid = j1.optString("openid");
                            String scope = j1.optString("scope");
                            String errcode = j1.optString("errcode");
                            if ("".equals(errcode)) {
                                //检查accesstoken有效期
                                checkAccessToken(access_token, openid);
                            } else {
                                //出错了
                                loginFaile();
                                CommonMethod.dismissLoadingDialog();
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
            }
        });
        mQueue.add(request);
    }

    /**
     * 检查access_token是否有效
     *
     * @param access_token
     * @param openid
     */
    private void checkAccessToken(final String access_token, final String openid) {
        StringRequest request = new StringRequest(Urls.checkAccessTokenUrl(access_token, openid),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("ok".equals(j1.getString("errmsg"))) {
                                //获取微信用户信息
                                getWXUserInfo(access_token, openid);
                            } else {
                                //出错了
                                loginFaile();
                                CommonMethod.dismissLoadingDialog();
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
            }
        });
        mQueue.add(request);
    }

    /**
     * 获取微信用户信息
     */
    private void getWXUserInfo(String access_token, String openid) {
        StringRequest request = new StringRequest(Urls.getWXUserInfoUrl(access_token, openid),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String json = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                            JSONObject j1 = new JSONObject(json);
                            if ("".equals(j1.optString("errcode"))) {
                                String openid = CommonMethod.getMd5Value(j1.optString("openid"));
                                getUserId(openid, CommonValues.LOGIN_TYPE_WEIXIN, j1.optString("nickname"), j1.optString("headimgurl"));
                            } else {
                                //出错了
                                loginFaile();
                                CommonMethod.dismissLoadingDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(activity);
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }

    /**
     * 获取uid
     */
    private void getUserId(final String openid, final String type, final String nickname, final String avatar) {
        StringRequest request = new StringRequest(Request.Method.POST, Urls.THIRD_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonMethod.dismissLoadingDialog();
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {
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
                                if (LoginAndRegistActivity.instance != null) {
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
                            } else {
                                //登录失败
                                loginFaile();
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("openid", openid);
                params.put("type", type);
                params.put("nickname", nickname);
                params.put("avatar", avatar);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 登录失败
     */
    private void loginFaile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("登录结果");
        builder.setMessage("登录失败");
        builder.setCancelable(true);
        builder.show();
    }
}
