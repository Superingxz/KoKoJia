package com.nuoxian.kokojia.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import com.nuoxian.kokojia.activity.RechargeActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.ypy.eventbus.EventBus;

/**
 * Created by Administrator on 2016/8/17.
 */
public class WXPay {

    private Activity activity;

    public WXPay(Activity activity) {
        this.activity = activity;
    }

    /**
     * 支付
     * @param appId
     * @param partnerId
     * @param prepayId
     * @param nonceStr
     * @param timeStamp
     * @param sign
     */
    public void pay(String appId,String partnerId,String prepayId,String nonceStr,String timeStamp,String sign){
        PayReq request = new PayReq();
        //appid
        request.appId = appId;
        //商户号
        request.partnerId = partnerId;
        request.prepayId= prepayId;
        //扩展字段
        request.packageValue = "Sign=WXPay";
        request.nonceStr= nonceStr;
        //时间戳
        request.timeStamp= timeStamp;
        //签名
        request.sign= sign;
        MyApplication.wx.sendReq(request);
    }

}
