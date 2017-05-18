package com.nuoxian.kokojia.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.ypy.eventbus.EventBus;

/**
 * Created by Administrator on 2016/8/3.
 */
public class ZFBPay {

    public ZFBPay(Activity activity,String activityName) {
        this.activity = activity;
        this.activityName = activityName;
    }

    private Activity activity;
    private String activityName;
    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    //设置提示框
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("支付结果");
                    builder.setCancelable(false);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        builder.setMessage("支付成功");
                        builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if("RechargeActivity".equals(activityName)){//充值界面进行的操作
                                    //显示正在加载提示框
                                    CommonMethod.showLoadingDialog("正在加载请稍后...", activity);
                                    //1.5秒后跳转
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //隐藏正在加载
                                            CommonMethod.dismissLoadingDialog();
                                            //发送广播，充值成功
                                            BuyResult result = new BuyResult();
                                            result.setBuyStatus("recharge");
                                            EventBus.getDefault().post(result);
                                            //关闭支付页面
                                            activity.finish();
                                        }
                                    }, 1500);
                                }else if("ThirdPayActivity".equals(activityName)){//第三方支付界面进行的操作
                                    //显示正在加载提示框
                                    CommonMethod.showLoadingDialog("正在加载请稍后...", activity);
                                    //3秒后返回
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //隐藏正在加载
                                            CommonMethod.dismissLoadingDialog();
                                            //发送广播，支付成功
                                            BuyResult result = new BuyResult();
                                            result.setBuyStatus("ok");
                                            EventBus.getDefault().post(result);
                                            //关闭当前页
                                            activity.finish();
                                        }
                                    }, 3000);
                                }
                            }
                        });
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            builder.setMessage("支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            builder.setMessage("支付失败");
                        }
                        builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    }
                    builder.show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay(final String payInfo) {
        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(activity);
        String version = payTask.getVersion();
        Toast.makeText(activity, version, Toast.LENGTH_SHORT).show();
    }

}
