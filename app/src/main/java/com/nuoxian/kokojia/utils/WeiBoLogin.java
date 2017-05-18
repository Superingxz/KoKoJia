package com.nuoxian.kokojia.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.BindAccountActivity;
import com.nuoxian.kokojia.activity.LoginAndRegistActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.ProgressValues;
import com.nuoxian.kokojia.http.Urls;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/26.
 */
public class WeiBoLogin {


}
