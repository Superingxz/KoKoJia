package com.nuoxian.kokojia.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.nuoxian.kokojia.application.MyApplication;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/23.
 */
public class BaseUiListener implements IUiListener {

    private Activity activity;

    public BaseUiListener(Activity activity) {
        this.activity = activity;
    }

    protected void doComplete(JSONObject values) {
        String openid = values.optString("openid");
        String access_token = values.optString("access_token");
        String expires = values.optString("expires_in");
        MyApplication.mTencent.setOpenId(openid);
        MyApplication.mTencent.setAccessToken(access_token,expires);
        UserInfo info = new UserInfo(activity, MyApplication.mTencent.getQQToken());
        info.getUserInfo(new UserInfoListener(activity,openid));
        CommonMethod.showLoadingDialog("正在加载...", activity);
    }

    @Override
    public void onComplete(Object o) {
        if(o!=null){
            try {
                doComplete(new JSONObject(o.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(UiError e) {
    }

    @Override
    public void onCancel() {
    }

}
