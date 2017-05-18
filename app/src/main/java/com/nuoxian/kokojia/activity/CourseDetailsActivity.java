package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.alipay.security.mobile.module.commonutils.LOG;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.MainPagerAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.BuyResult;
import com.nuoxian.kokojia.fragment.CourseCommentFragment;
import com.nuoxian.kokojia.fragment.CourseDetailsFragment;
import com.nuoxian.kokojia.fragment.CourseDiscussFragment;
import com.nuoxian.kokojia.fragment.CourseLessonFragment;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.AccessTokenKeeper;
import com.nuoxian.kokojia.utils.BaseUiListener;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.CourseDetailsFragmentTab;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.Util;
import com.nuoxian.kokojia.utils.WeiBoLogin;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程详情页
 */
public class CourseDetailsActivity extends AutoLayoutActivity implements View.OnClickListener {

    private String id, lid, uid, imageUrl, targetUrl, classNum;
    private ImageView ivTitle;
    private TextView ivSearch, ivBack;
    private TextView tvCourseWare, tvShare, tvCollect;
    private TextView tvBuyStatus;
    private AutoLinearLayout tvDownLoad;
    private RadioGroup rg;
    private RadioButton rbComment;
    private List<Fragment> fragments = new ArrayList<>();
    private RequestQueue mQueue;
    private String title;
    private Bitmap bitmap;
    private ViewPager mViewPager;
    private int position;
    private String commentNum;
    private TextView tvClassNum,tvLearnNum;
    private String url;
    private ImageView courserefresh;
    private TextView coursetext;
    private RelativeLayout courseall;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //隐藏正在加载
                CommonMethod.dismissLoadingDialog();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this, R.color.titlebar);
        //显示正在加载
        CommonMethod.showLoadingDialog("正在加载...", this);
        //注册EventBus
        EventBus.getDefault().register(this);
        mQueue = MyApplication.getRequestQueue();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        initFragments();
        initView();
        //加载ViewPager
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        uid = CommonMethod.getUid(this);
        //获取显示的数据
        getData(Urls.getCourseDetialsUrl(id, uid));

        //这是RadioGroup和Viewpager的监听
        setListener();

//        CourseDetailsFragmentTab tab = new CourseDetailsFragmentTab(this, fragments, R.id.fl_course_details, rg);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
    }

    private void initFragments() {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        CourseDetailsFragment fragment1 = new CourseDetailsFragment();
        fragment1.setArguments(bundle);
        fragments.add(fragment1);
        CourseLessonFragment fragment2 = new CourseLessonFragment();
        fragment2.setArguments(bundle);
        fragments.add(fragment2);
        CourseCommentFragment fragment3 = new CourseCommentFragment();
        fragment3.setArguments(bundle);
        fragments.add(fragment3);
        CourseDiscussFragment fragment4 = new CourseDiscussFragment();
        fragment4.setArguments(bundle);
        fragments.add(fragment4);
    }

    private void initView() {
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.layout_course_ditails), iconFont);

        ivBack = (TextView) findViewById(R.id.iv_details_back);
        ivSearch = (TextView) findViewById(R.id.iv_details_search);
        ivTitle = (ImageView) findViewById(R.id.iv_details_title);
        tvBuyStatus = (TextView) findViewById(R.id.tv_details_buy_status);
        tvDownLoad = (AutoLinearLayout) findViewById(R.id.tv_details_download);
        rg = (RadioGroup) findViewById(R.id.rg_course_details);
        tvCourseWare = (TextView) findViewById(R.id.tv_course_ditails_courseware);
        tvShare = (TextView) findViewById(R.id.tv_course_details_share);
        tvCollect = (TextView) findViewById(R.id.tv_course_details_collect);
        mViewPager = (ViewPager) findViewById(R.id.vp_course_details);
        tvClassNum = (TextView) findViewById(R.id.tv_course_details_classNum);
        tvLearnNum = (TextView) findViewById(R.id.tv_course_details_learn);
//        linearlayout=(LinearLayout)findViewById(R.id.linearlayout1);
        courseall=(RelativeLayout)findViewById(R.id.course_all);

        courserefresh=(ImageView)findViewById(R.id.course_refresh);
        coursetext=(TextView)findViewById(R.id.course_text);
//        ivdetailtitle=(ImageView)findViewById(R.id.iv_details_title);

        RadioButton rb = (RadioButton) rg.getChildAt(0);
        rb.setChecked(true);
        rbComment = (RadioButton) rg.getChildAt(2);

        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvBuyStatus.setOnClickListener(this);
        tvDownLoad.setOnClickListener(this);
        tvCourseWare.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvCollect.setOnClickListener(this);
        ivTitle.setOnClickListener(this);

        //监听重新刷新按钮
        courserefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏刷新键
                courserefresh.setVisibility(View.GONE);
                coursetext.setVisibility(View.GONE);
                courseall.setVisibility(View.VISIBLE);
                //重新加载数据
                getCourseWare(Urls.getCourseDetialsUrl(id,uid));
                getData(Urls.getCourseDetialsUrl(id, uid));
            }
        });
    }

    /**
     * 设置监听
     */
    private void setListener(){
        //ViewPager滑动监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RadioButton rb = (RadioButton) rg.getChildAt(position);
                rb.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //RadioGroup监听
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_details_details://概述
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_details_lesson://目录
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_details_comment://评价
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_details_discuss://讨论
                        mViewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        uid = CommonMethod.getUid(this);
        switch (v.getId()) {
            case R.id.iv_details_back://返回
                this.finish();
                break;
            case R.id.iv_details_search://搜索
                CommonMethod.startActivity(this, SearchActivity.class);
                break;
            case R.id.iv_details_title://点击图片
                //显示试听的对话框
                showListenDialog();
                break;
            case R.id.tv_details_buy_status://购买
                if ("0".equals(uid)) {
                    //没有登录跳转到登录页面
                    CommonMethod.startActivity(this, LoginActivity.class);
                } else {
                    if ("开始学习".equals(tvBuyStatus.getTag())) {
                        //获取目录第一个视频的lid,跳转到播放页面
                        getLidToPlay(Urls.getCourseLessonUrl(id, uid, 1));
                    } else if ("立即购买".equals(tvBuyStatus.getTag())) {
                        //提交订单
                        sendOrder();
                    } else if ("已停售".equals(tvBuyStatus.getTag())) {
                        Toast.makeText(this, "已停售!", Toast.LENGTH_SHORT).show();
                    } else if ("已过期".equals(tvBuyStatus.getTag())) {
                        Toast.makeText(this, "已过期!", Toast.LENGTH_SHORT).show();
                    } else if ("已结束".equals(tvBuyStatus.getTag())) {
                        Toast.makeText(this, "已结束!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_details_download://缓存
                if("0".equals(uid)){
                    //跳转到登录页面
                    CommonMethod.startActivity(this,LoginActivity.class);
                }else{
                    //跳转到下载页面
                    Intent intent = new Intent(this, DownLoadActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("title", title);
                    startActivity(intent);
                }
                break;
            case R.id.tv_course_ditails_courseware://课件
                //获取课件
                getCourseWare(Urls.courseWareUrl(id, uid));
                break;
            case R.id.tv_course_details_share://分享
                showShareWay();
                break;
            case R.id.tv_course_details_collect://收藏
                if("0".equals(uid)){
                    //没有登录，跳转到登录界面
                    CommonMethod.startActivity(this,LoginActivity.class);
                }else{
                    tvCollect.setEnabled(false);
                    collet(Urls.COURSE_COLLECT);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示是否试听对话框
     */
    private void showListenDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_try_listen, null);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView yes = (TextView) view.findViewById(R.id.to_listen_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是
                //跳转到播放页面
                Intent intent = new Intent(CourseDetailsActivity.this, PlayOnlineActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("lid", lid);
                intent.putExtra("position", -1);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        TextView no = (TextView) view.findViewById(R.id.to_listen_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //否
                dialog.dismiss();
            }
        });
    }

    /**
     * 获取课件数据
     */
    private void getCourseWare(String url) {
        //显示正在加载
        CommonMethod.showLoadingDialog("正在加载...", this);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);

                    if ("1".equals(j1.getString("status"))) {
                        JSONArray j2 = j1.getJSONArray("lecture");
                        //显示课件
                        showCourseWare(j2);
                        CommonMethod.dismissLoadingDialog();
                    } else {
                        CommonMethod.toast(CourseDetailsActivity.this, "没有数据");
                        CommonMethod.dismissLoadingDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(CourseDetailsActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        });
        mQueue.add(request);
    }

    /**
     * 显示分享的途径
     */
    private void showShareWay() {
        //创建自定义dialog框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_share_way, null);
        Typeface typeface = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(view, typeface);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();
        //设置监听
        AutoLinearLayout wx = (AutoLinearLayout) view.findViewById(R.id.ll_share_by_wx);
        wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过微信分享
                shareByWX(SendMessageToWX.Req.WXSceneSession);
            }
        });
        AutoLinearLayout pyq = (AutoLinearLayout) view.findViewById(R.id.ll_share_by_pengyouquan);
        pyq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过朋友圈分享
                shareByWX(SendMessageToWX.Req.WXSceneTimeline);
            }
        });
        AutoLinearLayout qq = (AutoLinearLayout) view.findViewById(R.id.ll_share_by_qq);
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过QQ分享
                shareByQQ();
            }
        });
        TextView cancle = (TextView) view.findViewById(R.id.tv_share_cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭dialog
                dialog.dismiss();
            }
        });
    }

    /**
     * 通过qq分享
     */
    private void shareByQQ() {
        if (MyApplication.mTencent == null) {
            MyApplication.mTencent = Tencent.createInstance(CommonValues.APP_ID_TENCENT, this);
        }
        Bundle bundle = new Bundle();
        bundle.putString(CommonValues.SHARE_TARGET_URL, targetUrl);
        bundle.putString(CommonValues.SHARE_IMAGE_URL, imageUrl);
        bundle.putString(CommonValues.SHARE_TITLE, title);
        bundle.putString(CommonValues.SHARE_SUMMARY, "共" + classNum + "节视频教程，24小时内讲师答疑，与万千学友在线讨论，课件免费下载。");
        MyApplication.mTencent.shareToQQ(this, bundle, new BaseUiListener(this));
    }

    /**
     * 通过微信分享
     */
    private void shareByWX(int way) {
        //分享图片
        if (MyApplication.wx == null) {
            MyApplication.wx = WXAPIFactory.createWXAPI(CourseDetailsActivity.this, CommonValues.APP_ID_WEIXIN, true);
            MyApplication.wx.registerApp(CommonValues.APP_ID_WEIXIN);
        }
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = targetUrl;//分享地址
        WXMediaMessage message = new WXMediaMessage(webPage);
        message.title = title;//标题
        message.description = "共" + classNum + "节视频教程，24小时内讲师答疑，与万千学友在线讨论，课件免费下载。";//描述
        if(bitmap!=null){
            Bitmap thumb =Bitmap.createScaledBitmap(bitmap, 120, 90, true);//压缩Bitmap
            message.thumbData = Util.bmpToByteArray(thumb, true);//图片
        }
        //发送请求
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        req.scene = way;
        MyApplication.wx.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 把网络资源图片转化成bitmap
     * @param url  网络资源图片
     * @return  Bitmap
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     * 显示课件
     *
     * @param array
     */
    private void showCourseWare(JSONArray array) throws JSONException {
        View popupView = LayoutInflater.from(this).inflate(R.layout.courseware_popupwindow, null);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        final PopupWindow window = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        AutoLinearLayout content = (AutoLinearLayout) popupView.findViewById(R.id.ll_courseware_content);
        //关闭
        TextView close = (TextView) popupView.findViewById(R.id.tv_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });

        if (array == null || array.length() == 0) {//没有课件
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(30, 30, 20, 10);
            textView.setLayoutParams(params);
            textView.setSingleLine();
            textView.setTextColor(0xff00aeff);
            textView.setText("暂无资料");
            content.addView(textView);
        } else {
            for (int i = 0; i < array.length(); i++) {//显示课件
                JSONObject object = array.getJSONObject(i);
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 0);
                textView.setLayoutParams(params);
                textView.setSingleLine();
                textView.setTextColor(0xff00aeff);
                textView.setText(object.getString("title"));
                content.addView(textView);
            }
        }
        //点击外部popupwindow消失
        window.setBackgroundDrawable(new BitmapDrawable());
        int[] local = new int[2];
        tvCourseWare.getLocationOnScreen(local);
        int width = popupView.getMeasuredWidth();
        int height = popupView.getMeasuredHeight();
        //设置popupwindow的大小
        window.showAtLocation(tvCourseWare, Gravity.NO_GRAVITY, (local[0]) - width / 2, local[1] - height);
    }

    /**
     * 提交订单
     */
    private void sendOrder() {
        CommonMethod.showLoadingDialog("正在加载...", this);
        StringRequest requst = new StringRequest(Request.Method.POST, Urls.CONFIRM_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("2".equals(j1.getString("status"))) {
                                //免费课程，将立即购买按钮编程开始学习
                                tvBuyStatus.setText("开始学习");
                                tvBuyStatus.setTag("开始学习");
                            } else {
                                //付费课程，跳转到确认订单界面
                                Intent intent = new Intent(CourseDetailsActivity.this, ConfirmBuyActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("json", response);
                                startActivity(intent);
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(CourseDetailsActivity.this);
                CommonMethod.dismissLoadingDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("uid", uid);
                return params;
            }
        };
        mQueue.add(requst);
    }

    //获取数据
    private void getData(final String url) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                parseJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //将重新加载的图片显示出来。可以点击图片重新加载此页面
                courserefresh.setVisibility(View.VISIBLE);
                coursetext.setVisibility(View.VISIBLE);
//                getData(url);
                courseall.setVisibility(View.GONE);//如果没网络就隐藏课程详情整体控件

                Toast.makeText(CourseDetailsActivity.this, "请检查网络!", Toast.LENGTH_SHORT).show();
                //通知加载完成
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
        mQueue.add(request);
    }

    /**
     * 解析数据
     * @param json
     */
    private void parseJson(String json) {
        try {
            JSONObject j1 = new JSONObject(json);
            if ("1".equals(j1.getString("status"))) {
                JSONObject j2 = j1.getJSONObject("course");
                title = j2.getString("title");
                if ("0".equals(j2.getString("collect_course"))) {//未收藏
                    tvCollect.setTextColor(0xff999999);
                } else {//已收藏
                    tvCollect.setTextColor(0xffff002f);
                }
                if ("1".equals(j2.getString("buy_status"))) {//学习
                    tvBuyStatus.setText("开始学习");
                    tvBuyStatus.setTag("开始学习");
                } else if ("2".equals(j2.getString("buy_status"))) {//购买
                    tvBuyStatus.setText("立即购买");
                    tvBuyStatus.setTag("立即购买");
                } else if ("3".equals(j2.getString("buy_status"))) {//已过期
                    tvBuyStatus.setText(" 已过期");
                    tvBuyStatus.setTag("已过期");
                } else if ("4".equals(j2.getString("buy_status"))) {//已停售
                    tvBuyStatus.setText(" 已停售");
                    tvBuyStatus.setTag("已停售");
                } else if ("5".equals(j2.getString("buy_status"))) {//已结束
                    tvBuyStatus.setText(" 已结束");
                    tvBuyStatus.setTag("已结束");
                }
                targetUrl = j2.getString("course_invite");//分享地址
                Log.i(targetUrl, "targetUrl:");

                classNum = j2.getString("class_num");//课时
                tvClassNum.setText("共"+classNum+"节"+"·"+j2.getString("video_time"));
                tvLearnNum.setText(j2.getString("trial_num")+"人学习");
                imageUrl = j2.getString("image_url");
                lid = j2.getString("course_trial_id");
                commentNum = j2.getString("course_comment_total");//评价总数
                if("0".equals(commentNum)){
                    rbComment.setText("评价");
                }else {
                    rbComment.setText("评价("+commentNum+")");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bitmap = GetLocalOrNetBitmap(imageUrl);
                    }
                }).start();
                MyApplication.imageLoader.displayImage(imageUrl, ivTitle, MyApplication.options);
                //通知加载完成
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            } else {
                CommonMethod.toast(this, "没有数据!");
                //通知加载完成
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取目录中第一个的lid
     *
     * @param url
     */
    private void getLidToPlay(String url) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j1 = new JSONObject(response);
                    if ("1".equals(j1.getString("status"))) {
                        JSONArray j2 = j1.getJSONArray("lesson");
                        for (int i = 0; i < j2.length(); i++) {
                            JSONObject j3 = j2.getJSONObject(i);
                            if ("1".equals(j3.getString("level"))) {
                                lid = j3.getString("id");
                                position = i;
                                break;
                            }
                        }
                        //跳转到播放页面
                        Intent intent = new Intent(CourseDetailsActivity.this, PlayOnlineActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("lid", lid);
                        intent.putExtra("page", 1);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CourseDetailsActivity.this, "无法访问!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CourseDetailsActivity.this, "加载失败，请检查网络!", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(request);
    }

    /**
     * 收藏
     *
     * @param url
     */
    private void collet(String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tvCollect.setEnabled(true);
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {//收藏
                                tvCollect.setTextColor(0xffff002f);
                                CommonMethod.toast(CourseDetailsActivity.this, "收藏成功");
                            } else {//取消收藏
                                tvCollect.setTextColor(0xff999999);
                                CommonMethod.toast(CourseDetailsActivity.this, "取消收藏");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.toast(CourseDetailsActivity.this, "操作失败，请检查网络!");
                tvCollect.setEnabled(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("uid", uid);
                return params;
            }
        };
        mQueue.add(request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != MyApplication.mTencent){
            MyApplication.mTencent.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 支付购买后发过来的信息
     *
     * @param event
     */
    public void onEventMainThread(BuyResult event) {
        if ("ok".equals(event.getBuyStatus())) {
            //如果购买成功刷新界面信息
            uid = CommonMethod.getUid(this);
            getData(Urls.getCourseDetialsUrl(id, uid));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
