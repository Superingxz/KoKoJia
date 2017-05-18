package com.nuoxian.kokojia.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.LoginResult;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ypy.eventbus.EventBus;

import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI wxAPI;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wxentry);

        wxAPI = WXAPIFactory.createWXAPI(this, CommonValues.APP_ID_WEIXIN, true);
        wxAPI.registerApp(CommonValues.APP_ID_WEIXIN);
        wxAPI.handleIntent(getIntent(), this);
        mQueue = MyApplication.getRequestQueue();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        wxAPI.handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if(resp.getType()== ConstantsAPI.COMMAND_SENDAUTH){//登录返回的结果
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("登录结果");
            switch (resp.errCode){
                case BaseResp.ErrCode.ERR_OK://成功
                    String code = ((SendAuth.Resp) resp).code;
                    SharedPreferences sp = getSharedPreferences(CommonValues.SP_WX_INFO,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("code",code);
                    editor.commit();
                    //通知正在用微信登录,返回给LoginActivity
                    LoginResult result = new LoginResult();
                    result.setLoginWay("wx");
                    EventBus.getDefault().post(result);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                    builder.setMessage("登录失败,您拒绝授权!");
                    builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WXEntryActivity.this.finish();
                        }
                    });
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                    builder.setMessage("登录失败,您取消了登录!");
                    builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WXEntryActivity.this.finish();
                        }
                    });
                    break;
                default:
                    builder.setMessage("登录失败!");
                    builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WXEntryActivity.this.finish();
                        }
                    });
                    break;
            }
            builder.show();
        }else if(resp.getType()== ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
            switch (resp.errCode){
                case BaseResp.ErrCode.ERR_OK://成功
                    WXEntryActivity.this.finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://取消
                    WXEntryActivity.this.finish();
                    break;
                default:
                    CommonMethod.showAlerDialog("分享结果","分享失败",this);
                    WXEntryActivity.this.finish();
                    break;
            }
        }
    }

}
