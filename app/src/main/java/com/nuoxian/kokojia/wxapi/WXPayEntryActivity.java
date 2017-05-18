package com.nuoxian.kokojia.wxapi;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.RechargeActivity;
import com.nuoxian.kokojia.activity.ThirdPayActivity;
import com.nuoxian.kokojia.enterty.BuyResult;
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
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, CommonValues.APP_ID_WEIXIN);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {//支付返回的结果
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("支付结果");
			builder.setCancelable(false);
			if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
				//支付成功
				builder.setMessage("支付成功");
				builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonMethod.showLoadingDialog("正在加载...", WXPayEntryActivity.this);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								//关闭购买页面
								if (ThirdPayActivity.instance != null) {
									ThirdPayActivity.instance.finish();
								}
								//发送广播，充值成功
								BuyResult result = new BuyResult();
								result.setBuyStatus("ok");
								EventBus.getDefault().post(result);
								//关闭充值页面
								if (RechargeActivity.instance != null) {
									RechargeActivity.instance.finish();
								}
								//关闭当前页面
								WXPayEntryActivity.this.finish();
								CommonMethod.dismissLoadingDialog();
							}
						}, 3000);
					}
				});
			} else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
				//支付取消
				builder.setMessage("支付被取消");
				builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//退出当前页面
						WXPayEntryActivity.this.finish();
					}
				});
			} else {
				//支付失败
				builder.setMessage("支付失败");
				builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//退出当前页面
						WXPayEntryActivity.this.finish();
					}
				});
			}
			builder.show();
		}
	}
}