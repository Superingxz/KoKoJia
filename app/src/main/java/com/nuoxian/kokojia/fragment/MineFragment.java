package com.nuoxian.kokojia.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.activity.CreateCouponActivity;
import com.nuoxian.kokojia.activity.LoginActivity;
import com.nuoxian.kokojia.activity.ManageCourseActivty;
import com.nuoxian.kokojia.activity.ManagePackageActivity;
import com.nuoxian.kokojia.activity.MyCollectActivity;
import com.nuoxian.kokojia.activity.MyCouponActivity;
import com.nuoxian.kokojia.activity.MyCourseActivity;
import com.nuoxian.kokojia.activity.MyOrderActivity;
import com.nuoxian.kokojia.activity.MyQuestionActivity;
import com.nuoxian.kokojia.activity.PayCenterActivity;
import com.nuoxian.kokojia.activity.RechargeActivity;
import com.nuoxian.kokojia.activity.RefundActivity;
import com.nuoxian.kokojia.activity.SellOrderActivity;
import com.nuoxian.kokojia.activity.TeacherAnswerActivity;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.enterty.MyCourse;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.view.RoundImageView;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chensilong on 2016/7/6.
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tvBalance, tvEmpirical, tvCredit, tvUserName,tvAnswerCount,tvRefundCount;
    private AutoLinearLayout llMyCourse, llExit, llRecharge, llOrder, llMyQuestion,llMyCollect,llMyCoupon,llMyLottery,llMyRedeem;//用户
    private AutoLinearLayout allManageCourse,allManagePackage,allSellOrder,allTeacherAnswer,allCoupon,allRefund;//老师
    private AutoLinearLayout allUser,allTeacher,allSchoolmaster;
    private RequestQueue mQueue;
    private RoundImageView ivHead;
    private String id,schoolId = "";
    private int gaoxiaohuodong=1;
    private int gaoxiaoadmin=1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.mine_fragment, container, false);
        mQueue = MyApplication.getRequestQueue();
        initView();
        //设置监听
        setListener();
        //获取用户id
        id = CommonMethod.getUid(getContext());
        //获取数据
        getData(Urls.MINE_URL, id);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //注册EventBus
        EventBus.getDefault().register(MineFragment.this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(view.findViewById(R.id.arl_menu), iconFont);
        //校长界面
        allSchoolmaster  = (AutoLinearLayout) view.findViewById(R.id.all_schoolmaster);
        //老师界面
        allTeacher = (AutoLinearLayout) view.findViewById(R.id.all_teacher);
        allManageCourse = (AutoLinearLayout) view.findViewById(R.id.ll_mine_manage_course);
        allManagePackage = (AutoLinearLayout) view.findViewById(R.id.ll_mine_manage_package);
        allSellOrder = (AutoLinearLayout) view.findViewById(R.id.ll_mine_sell_order);
        allTeacherAnswer = (AutoLinearLayout) view.findViewById(R.id.ll_mine_teacher_answer);
        allCoupon = (AutoLinearLayout) view.findViewById(R.id.ll_mine_coupon);
        allRefund = (AutoLinearLayout) view.findViewById(R.id.ll_mine_refund);
        tvAnswerCount = (TextView) view.findViewById(R.id.tv_teacher_answer_count);
        tvRefundCount = (TextView) view.findViewById(R.id.tv_manage_refund_count);
        //用户界面
        allUser = (AutoLinearLayout) view.findViewById(R.id.all_user);
        tvBalance = (TextView) view.findViewById(R.id.tv_mine_balance);
        tvEmpirical = (TextView) view.findViewById(R.id.tv_mine_empirical);
        tvCredit = (TextView) view.findViewById(R.id.tv_mine_credit);
        tvUserName = (TextView) view.findViewById(R.id.tv_mine_username);
        ivHead = (RoundImageView) view.findViewById(R.id.iv_mine_headimage);
        llMyCourse = (AutoLinearLayout) view.findViewById(R.id.ll_mine_mycourse);
        llRecharge = (AutoLinearLayout) view.findViewById(R.id.ll_mine_recharge);
        llExit = (AutoLinearLayout) view.findViewById(R.id.ll_mine_exit);
        llOrder = (AutoLinearLayout) view.findViewById(R.id.ll_mine_order);
        llMyQuestion = (AutoLinearLayout) view.findViewById(R.id.ll_mine_question);
        llMyCollect = (AutoLinearLayout) view.findViewById(R.id.ll_mine_collect);
        llMyCoupon = (AutoLinearLayout) view.findViewById(R.id.ll_mine_mycoupon);
        llMyLottery= (AutoLinearLayout) view.findViewById(R.id.ll_mine_lottery);
        llMyRedeem= (AutoLinearLayout) view.findViewById(R.id.ll_mine_redeem);
    }

    /**
     * 设置监听
     */
    private void setListener(){
        //用户界面监听
        llMyCourse.setOnClickListener(this);
        llRecharge.setOnClickListener(this);
        llExit.setOnClickListener(this);
        llOrder.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        llMyQuestion.setOnClickListener(this);
        llMyCollect.setOnClickListener(this);
        llMyCoupon.setOnClickListener(this);
        //老师界面监听
        allManageCourse.setOnClickListener(this);
        allManagePackage.setOnClickListener(this);
        allSellOrder.setOnClickListener(this);
        allTeacherAnswer.setOnClickListener(this);
        allCoupon.setOnClickListener(this);
        allRefund.setOnClickListener(this);
    }

    //获取登录的用户信息
    private void getData(String url, final String uid) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //解析数据
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {//获取数据失败
                CommonMethod.loadFailureToast(getContext());
                //从本地取用户信息
                SharedPreferences sp = getContext().getSharedPreferences(CommonValues.SP_USER_INFO, getContext().MODE_PRIVATE);
                if (id.equals(sp.getString("uid", "0"))) {//如果当前的用户id是存储的用户ID
                    //将本地存储的信息展示出来
                    tvUserName.setText(sp.getString("nickname", ""));
                    tvUserName.setBackground(null);
                    tvCredit.setText(sp.getString("credit", ""));
                    tvEmpirical.setText(sp.getString("experience", ""));
                    tvBalance.setText(sp.getString("money", ""));
                    schoolId = sp.getString("schoolId","");//身份
                    if("0".equals(schoolId)){//普通用户
                        //显示普通用户界面
                        allTeacher.setVisibility(View.GONE);
                    }else if("1".equals(schoolId)){//校长
                        //显示校长界面
                        allTeacher.setVisibility(View.VISIBLE);
                    }else if("2".equals(schoolId)){//老师
                        //显示老师界面
                        allTeacher.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(sp.getString("avatar", ""))) {
                        //如果本地中没有保存头像，就显示默认头像
                        ivHead.setImageResource(R.mipmap.user_head);
                    } else {
                        MyApplication.imageLoader.displayImage(sp.getString("avatar", ""), ivHead, MyApplication.options);
                    }
                } else {//如果当前的用户id不是存储的用户ID
                    //显示所有信息为空
                    tvUserName.setText("登录");
                    tvUserName.setBackgroundResource(R.drawable.mine_login_background);
                    tvCredit.setText("--");
                    tvEmpirical.setText("--");
                    tvBalance.setText("--");
                    ivHead.setImageResource(R.mipmap.user_head);
                    //显示普通用户的界面
                    schoolId = "0";
                    allTeacher.setVisibility(View.GONE);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                return params;
            }
        };
        mQueue.add(request);
    }

    private void parseJson(String json) {
        try {
            JSONObject j1 = new JSONObject(json);
            if ("1".equals(j1.getString("status"))) {
                JSONObject j2 = j1.getJSONObject("data");
                tvUserName.setText(j2.getString("nickname"));//用户昵称
                tvUserName.setBackground(null);
                tvCredit.setText(j2.getString("credit"));//学分
                schoolId = j2.getString("school_id");//身份
                gaoxiaohuodong=j2.getInt("gaoxiao_huodong");//高校活动学生身份
                gaoxiaoadmin=j2.getInt("gaoxiao_admin");//高校活动管理身份
                String refundCount = j2.optString("refund_count");

                //如果gaoxiaohuodong==1&&gaoxiaoadmin!=1就显示抽奖的按钮，隐藏兑奖管理按钮
                //如果gaoxiaoadmin==1&&gaoxiaohuodong!=1就显示兑奖管理按钮，隐藏抽奖按钮
                if(gaoxiaohuodong==1&&gaoxiaoadmin!=1) {
                    llMyLottery.setVisibility(View.VISIBLE);
                    llMyRedeem.setVisibility(View.GONE);
                }else if(gaoxiaoadmin==1&&gaoxiaohuodong!=1){
                    llMyLottery.setVisibility(View.GONE);
                    llMyRedeem.setVisibility(View.VISIBLE);
                }

                //退款数
                if(!TextUtils.isEmpty(refundCount)){
                    if(Integer.parseInt(refundCount)>0){
                        tvRefundCount.setVisibility(View.VISIBLE);
                        tvRefundCount.setText(refundCount);
                    }else{
                        tvRefundCount.setVisibility(View.GONE);
                    }
                }else{
                    tvRefundCount.setVisibility(View.GONE);
                }
                //讲师答疑数
                String answerCount = j2.optString("ask_count");
                if(!TextUtils.isEmpty(answerCount)){
                    if(Integer.parseInt(answerCount)>0){
                        tvAnswerCount.setVisibility(View.VISIBLE);
                        tvAnswerCount.setText(answerCount);
                    }else{
                        tvAnswerCount.setVisibility(View.GONE);
                    }
                }else{
                    tvAnswerCount.setVisibility(View.GONE);
                }
                if("0".equals(schoolId)){//普通用户
                    //显示普通用户界面
                    allTeacher.setVisibility(View.GONE);
                }else if("1".equals(schoolId)){//校长
                    //显示校长界面
                    allTeacher.setVisibility(View.VISIBLE);
                }else if("2".equals(schoolId)){//老师
                    //显示老师界面
                    allTeacher.setVisibility(View.VISIBLE);
                }
                tvEmpirical.setText(j2.getString("experience"));//经验值
                tvBalance.setText(j2.getString("money"));//余额
                MyApplication.imageLoader.displayImage(j2.getString("avatar"), ivHead, MyApplication.options);//头像
                //将用户信息保存
                SharedPreferences sp = getContext().getSharedPreferences(CommonValues.SP_USER_INFO, getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("uid", id);
                editor.putString("schoolId",schoolId);
                editor.putString("nickname", j2.getString("nickname"));
                editor.putString("credit", j2.getString("credit"));
                editor.putString("experience", j2.getString("experience"));
                editor.putString("money", j2.getString("money"));
                editor.putString("avatar", j2.getString("avatar"));
                editor.commit();
            } else {
                //没有登录
                tvUserName.setText("登录");
                tvUserName.setBackgroundResource(R.drawable.mine_login_background);
                tvCredit.setText("--");
                tvEmpirical.setText("--");
                tvBalance.setText("--");
                ivHead.setImageResource(R.mipmap.user_head);
                //显示普通用户界面
                allTeacher.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        id = CommonMethod.getUid(getContext());
        switch (v.getId()) {
            //用户界面
            case R.id.ll_mine_mycourse://我的课程
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else{
                    CommonMethod.startActivity(getContext(), MyCourseActivity.class);
                }
                break;
            case R.id.tv_mine_username://点击用户名
                if("登录".equals(tvUserName.getText())){
                    //如果是未登录跳转到登录界面
                    CommonMethod.startActivity(getContext(), LoginActivity.class);
                }
                break;
            case R.id.ll_mine_recharge://支付中心
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), PayCenterActivity.class);
                }
                break;
            case R.id.ll_mine_question://我的提问
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), MyQuestionActivity.class);
                }
                break;
            case R.id.ll_mine_collect://我的收藏
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), MyCollectActivity.class);
                }
                break;
            case R.id.ll_mine_mycoupon://我的优惠券
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), MyCouponActivity.class);
                }
                break;
            case R.id.ll_mine_exit://退出账号
                if(!"0".equals(id)){//登录的状态
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false);
                    builder.setTitle("警告");
                    builder.setMessage("您是否要退出登录");
                    builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exit();
                        }
                    });
                    builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;
            /** 老师界面 */
            case R.id.ll_mine_order://我的订单
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), MyOrderActivity.class);
                }
                break;
            case R.id.ll_mine_manage_course://课程管理
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), ManageCourseActivty.class);
                }
                break;
            case R.id.ll_mine_manage_package://套餐管理
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), ManagePackageActivity.class);
                }
                break;
            case R.id.ll_mine_sell_order://销售订单
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), SellOrderActivity.class);
                }
                break;
            case R.id.ll_mine_teacher_answer://老师答疑
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), TeacherAnswerActivity.class);
                }
                break;
            case R.id.ll_mine_coupon://创建优惠券
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), CreateCouponActivity.class);
                }
                break;
            case R.id.ll_mine_refund://退款管理
                if("0".equals(id)){
                    //没有登录，跳转到登录页面
                    CommonMethod.startActivity(getContext(),LoginActivity.class);
                }else {
                    CommonMethod.startActivity(getContext(), RefundActivity.class);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 退出
     */
    private void exit(){
        //将保存的uid改为0
        SharedPreferences sp = getContext().getSharedPreferences("flag", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("data", "0");
        editor.commit();
        //设置界面用户信息
        tvUserName.setText("登录");
        tvUserName.setBackgroundResource(R.drawable.mine_login_background);
        tvBalance.setText("--");
        tvCredit.setText("--");
        tvEmpirical.setText("--");
        ivHead.setImageResource(R.mipmap.user_head);//使用默认头像
        //将用户信息改为未登录的信息
        SharedPreferences sp1 = getContext().getSharedPreferences(CommonValues.SP_USER_INFO, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sp1.edit();
        editor1.putString("uid", "0");
        schoolId = "0";
        editor1.putString("schoolId",schoolId);
        editor1.putString("nickname", "登录");
        editor1.putString("credit", "--");
        editor1.putString("experience", "--");
        editor1.putString("money", "--");
        editor1.putString("avatar", "");
        editor1.commit();
        //显示普通用户的界面
        allTeacher.setVisibility(View.GONE);
    }

    /**
     * 支付购买后发过来的信息
     *
     * @param event
     */
    public void onEventMainThread(BuyResult event) {
        if ("ok".equals(event.getBuyStatus()) || "recharge".equals(event.getBuyStatus())) {
            //如果购买成功或者充值成功,刷新界面信息
            id = CommonMethod.getUid(getContext());
            getData(Urls.MINE_URL, id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册
        EventBus.getDefault().unregister(MineFragment.this);
    }
}
