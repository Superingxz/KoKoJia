package com.nuoxian.kokojia.activity;

import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CalendarView;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCouponDetailsActivity extends BaseActivity implements View.OnClickListener {

    private Spinner sDiscussWay, sUseWay, sSendWay, sUseCourse;
    private String[] discussWays = {"抵扣金额", "实付金额"};
    private String[] useWays = {"全部课程", "指定课程"};
    private String[] sendWays = {"站内发放", "站外发放"};
    private AutoLinearLayout allCourse,getTime;
    private String uid;
    private int currentMonth;//当前查看的月份
    private int currentYear;//当前查看的年份
    private TextView tvStartTime, tvEndTime, tvUseTime, save, cancle, hint;
    private Date startLastDate, endLastDate, useLastDate;
    private EditText etSendCount, etDiscuntPrice, etCoursePrice;
    private List<String> courseIdList = new ArrayList<>();
    private static String DISCOUNT_PRICE = "0";
    private static String PAY_PRICE = "1";
    private static String ALL_COURSE = "0";
    private static String APPOINT_COURSE = "1";
    private static String INSIDE = "0";
    private static String OUTSIDE = "1";
    private String sendCount;//发放数量
    private String discountPrice;//优惠价格
    private String coursePrice;//课程价格
    private String discountWay;//优惠方式
    private String useRange;//使用范围
    private String courseId;//课程id
    private String sendWay;//发放方式
    private String startTime="";//开始时间
    private String endTime="";//结束时间
    private String useTime;//使用时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_create_coupon_details);

        CommonMethod.showLoadingDialog("正在加载...", this);
        uid = CommonMethod.getUid(this);
        //设置标题
        setTitle("创建优惠券");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCouponDetailsActivity.this.finish();
            }
        });
        //获取付费课程
        getAllCourse(Urls.COUPON_ALL_COURSE);

        initView();

        getSendCount();//获取发放数量
        getDiscountPrice();//获取优惠金额
        getCoursePrice();//获取课程价格

        //初始化参数
        initValues();
    }

    /**
     * 初始化参数
     */
    private void initValues() {
        discountWay = DISCOUNT_PRICE;
        useRange = ALL_COURSE;
        if (!courseIdList.isEmpty()) {//有发布的课程
            courseId = courseIdList.get(0);
        }
        sendWay = INSIDE;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        sDiscussWay = (Spinner) findViewById(R.id.spinner_discuss_way);
        sUseWay = (Spinner) findViewById(R.id.spinner_use_range);
        sSendWay = (Spinner) findViewById(R.id.spinner_send_way);
        sUseCourse = (Spinner) findViewById(R.id.spinner_use_course);
        //设置Spinner
        setSpinner();
        hint = (TextView) findViewById(R.id.tv_hint);//错误提示
        allCourse = (AutoLinearLayout) findViewById(R.id.ll_all_course);
        getTime = (AutoLinearLayout) findViewById(R.id.ll_get_time);//领取时间
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);//开始时间
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);//结束时间
        tvUseTime = (TextView) findViewById(R.id.tv_use_time);//使用时限
        etSendCount = (EditText) findViewById(R.id.et_send_count);//发放数量
        etDiscuntPrice = (EditText) findViewById(R.id.et_discount_price);//优惠金额
        etCoursePrice = (EditText) findViewById(R.id.et_course_price);//课程金额
        save = (TextView) findViewById(R.id.tv_save);//保存
        cancle = (TextView) findViewById(R.id.tv_cancle);//取消

        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        tvUseTime.setOnClickListener(this);
        save.setOnClickListener(this);
        cancle.setOnClickListener(this);
    }

    /**
     * 获取发放数量
     */
    private void getSendCount() {
        etSendCount.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                sendCount = s + "";
            }
        });
    }

    /**
     * 获取优惠价格
     */
    private void getDiscountPrice() {
        etDiscuntPrice.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                discountPrice = s + "";
            }
        });
    }

    /**
     * 获取课程价格
     */
    private void getCoursePrice() {
        etCoursePrice.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                coursePrice = s + "";
            }
        });
    }

    /**
     * 设置下拉列表
     */
    private void setSpinner() {
        //优惠方式
        setSpinnerAdapter(sDiscussWay, discussWays);
        sDiscussWay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//抵扣金额
                    discountWay = DISCOUNT_PRICE;
                } else {//实付金额
                    discountWay = PAY_PRICE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //使用范围
        setSpinnerAdapter(sUseWay, useWays);
        sUseWay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//全部课程
                    allCourse.setVisibility(View.VISIBLE);
                    sUseCourse.setVisibility(View.GONE);
                    useRange = ALL_COURSE;
                } else {//指定课程
                    sUseCourse.setVisibility(View.VISIBLE);
                    allCourse.setVisibility(View.GONE);
                    useRange = APPOINT_COURSE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //发放方式
        setSpinnerAdapter(sSendWay, sendWays);
        sSendWay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//站内发放
                    sendWay = INSIDE;
                    getTime.setVisibility(View.VISIBLE);
                } else {//站外发放
                    sendWay = OUTSIDE;
                    getTime.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_time://开始时间
                showCalendar(tvStartTime, "start");
                break;
            case R.id.tv_end_time://结束时间
                showCalendar(tvEndTime, "end");
                break;
            case R.id.tv_use_time://使用时限
                showCalendar(tvUseTime, "use");
                break;
            case R.id.tv_save://保存
                if(isInputOk()){
                    hint.setVisibility(View.GONE);
                    if(ALL_COURSE.equals(useRange)){//全部课程
                        save("course_price",coursePrice);
                    }else{//指定课程
                        save("course_id",courseId);
                    }
                }
                break;
            case R.id.tv_cancle://取消
                this.finish();
                break;
        }
    }

    /**
     * 保存
     * @param key
     * @param value
     * 根据选择的使用范围来决定参数是课程id还是课程价格
     */
    private void save(final String key, final String value) {
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.CREATE_COUPON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("0".equals(j1.getString("status"))){//错误
                                showHint(j1.getString("msg"));
                            }else if("1".equals(j1.getString("status"))){//创建成功
                                Bundle bundle = new Bundle();
                                bundle.putString("ok","refresh");
                                EventBus.getDefault().post(bundle);
                                CreateCouponDetailsActivity.this.finish();
                            }else{//创建失败
                                showHint("创建失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(CreateCouponDetailsActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",uid);
                params.put("num",sendCount);
                params.put("sale",discountWay);
                params.put("sale_price",discountPrice);
                params.put("user_range",useRange);
                params.put(key,value);
                params.put("doled",sendWay);
                params.put("start_date",startTime);
                params.put("end_date",endTime);
                params.put("couponDate",useTime);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 显示日历
     *
     * @param tv
     */
    private void showCalendar(final TextView tv, final String type) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_calendar, null);
        Typeface typeFace = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(popupView.findViewById(R.id.layout_calendar), typeFace);
        //自定义popupwindow，显示日历
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final PopupWindow window = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAsDropDown(sSendWay);
        //初始化视图
        final CalendarView calendar = (CalendarView) popupView.findViewById(R.id.cv_calendar);
        //显示上一次选择的日期
        switch (type) {
            case "start"://开始时间
                if (startLastDate != null) {
                    calendar.setLastTime(startLastDate);
                }
                break;
            case "end"://结束时间
                if (endLastDate != null) {
                    calendar.setLastTime(endLastDate);
                }
                break;
            case "use"://使用时间
                if (useLastDate != null) {
                    calendar.setLastTime(useLastDate);
                }
                break;
        }

        final TextView month = (TextView) popupView.findViewById(R.id.tv_calendar_month);
        //获取日历中年月
        Date date = calendar.getSelectedEndDate();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] d = format.format(date).split("-");
        currentMonth = Integer.parseInt(d[1]);
        currentYear = Integer.parseInt(d[0]);
        month.setText(currentYear+"年"+currentMonth + "月");
        //记录选择的日期
        calendar.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void OnItemClick(Date date) {
                switch (type) {
                    case "start"://开始时间
                        startLastDate = date;
                        startTime = format.format(date);
                        break;
                    case "end"://结束时间
                        endLastDate = date;
                        endTime = format.format(date);
                        break;
                    case "use"://使用时间
                        useLastDate = date;
                        useTime = format.format(date);
                        break;
                }
                //关闭窗口，设置时间
                window.dismiss();
                tv.setText(format.format(date));
            }
        });

        //上个月
        TextView left = (TextView) popupView.findViewById(R.id.tv_calendar_left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.clickLeftMonth();
                if (currentMonth == 1) {
                    currentMonth = 12;
                    currentYear--;
                } else {
                    currentMonth--;
                }
                month.setText(currentYear+"年"+currentMonth + "月");
            }
        });

        //下个月
        TextView right = (TextView) popupView.findViewById(R.id.tv_calendar_right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.clickRightMonth();
                if (currentMonth == 12) {
                    currentMonth = 1;
                    currentYear++;
                } else {
                    currentMonth++;
                }
                month.setText(currentYear+"年"+currentMonth + "月");
            }
        });
    }

    /**
     * 设置spinner适配器
     *
     * @param spinner
     * @param array
     */
    private void setSpinnerAdapter(Spinner spinner, String[] array) {
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array);
        //设置下拉列表的风格
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter1);
        //设置默认值
        spinner.setVisibility(View.VISIBLE);
    }

    /**
     * 获取所有课程
     *
     * @param url
     */
    private void getAllCourse(String url) {
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {//有课程
                                JSONArray j2 = j1.getJSONArray("data");
                                String[] course = new String[j2.length()];
                                for (int i = 0; i < j2.length(); i++) {
                                    JSONObject j3 = j2.getJSONObject(i);
                                    course[i] = j3.getString("title");
                                    courseIdList.add(j3.getString("id"));
                                }
                                //加载列表
                                setSpinnerAdapter(sUseCourse, course);
                                sUseCourse.setVisibility(View.GONE);
                            } else {//没有已发布的课程
                                String[] course = {j1.getString("msg")};
                                //加载列表
                                setSpinnerAdapter(sUseCourse, course);
                                sUseCourse.setVisibility(View.GONE);
                            }
                            //设置列表的监听
                            sUseCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (!courseIdList.isEmpty()) {//有课程
                                        courseId = courseIdList.get(position);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CommonMethod.dismissLoadingDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(CreateCouponDetailsActivity.this);
                CommonMethod.dismissLoadingDialog();
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

    /**
     * 输入是否正确
     */
    private boolean isInputOk() {
        if (TextUtils.isEmpty(sendCount)) {
            showHint("请填写发放数量");
            return false;
        }
        if (Integer.parseInt(sendCount) < 1 || Integer.parseInt(sendCount) > 9999) {
            showHint("发放数量只能在1~9999之间");
            return false;
        }
        if(TextUtils.isEmpty(discountPrice)){
            showHint("请填写优惠金额");
            return false;
        }
        if(ALL_COURSE.equals(useRange)){//全部课程
            if(TextUtils.isEmpty(coursePrice)){
                showHint("请输入课程的最低价");
                return false;
            }
        }else{//指定课程
            if(courseIdList.isEmpty()){
                showHint("你没有已发布的付费课程");
                return false;
            }
        }
        if(INSIDE.equals(sendWay)){//站内发放
            if(TextUtils.isEmpty(startTime)){
                showHint("请选择领取的开始时间");
                return false;
            }
            //获取当天的时间
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            if(timeInterval(format.format(date),startTime)<0){
                showHint("领取开始时间必须大于当天时间");
                return false;
            }
            if(TextUtils.isEmpty(endTime)){
                showHint("请选择领取的结束时间");
                return false;
            }
            if(timeInterval(startTime,endTime)<1){
                showHint("结束时间必须大于开始时间");
                return false;
            }
            if(timeInterval(startTime,endTime)>30){
                showHint("有效期限不能超过30天");
                return false;
            }
            if(TextUtils.isEmpty(useTime)){
                showHint("请选择使用时限");
                return false;
            }
            if(timeInterval(endTime,useTime)<1){
                showHint("使用时限必须大于领取的结束时间");
                return false;
            }
        }else{//站外发放
            if(TextUtils.isEmpty(useTime)){
                showHint("请选择使用时限");
                return false;
            }
            //获取当天的时间
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            if(timeInterval(format.format(date),useTime)<1){
                showHint("使用时限必须大于当天");
                return false;
            }
        }

        return true;
    }

    /**
     * 显示错误信息
     * @param content
     */
    private void showHint(String content){
        hint.setVisibility(View.VISIBLE);
        hint.setText(content);
    }

    /**
     * 计算时间间隔
     * @param str1  开始时间
     * @param str2  结束时间
     */
    private double timeInterval(String str1,String str2){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//输入日期的格式
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(str1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(date1);
        cal2.setTime(date2);
        double dayCount = (cal2.getTimeInMillis()-cal1.getTimeInMillis())/(1000*3600*24);//从间隔毫秒变成间隔天数
        return dayCount;
    }

}
