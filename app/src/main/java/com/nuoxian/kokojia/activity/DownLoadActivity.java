package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.DownLoadAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseLessonDownload;
import com.nuoxian.kokojia.enterty.Download;
import com.nuoxian.kokojia.enterty.ProgressValues;
import com.nuoxian.kokojia.enterty.VideoDownLoad;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.DownLoadConfig;
import com.nuoxian.kokojia.utils.FontManager;
import com.ypy.eventbus.EventBus;
import com.zhy.autolayout.AutoLayoutActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 下载列表页面
 */
public class DownLoadActivity extends AutoLayoutActivity implements View.OnClickListener {

    private ListView listView;
    private TextView tvAllCheck, tvStartDownLoad, tvMyDownLoad, ivBack;
    private String id, uid, title;
    private RequestQueue mQueue;
    private List<CourseLessonDownload> list = new ArrayList<>();
    private List<CourseLessonDownload> checkedLesson;
    private DownLoadAdapter adapter;
    private Handler mainHandler;
    private int checkCount = 1;//记录点击全选的次数
    private List<String> video_ts;
    private List<Integer> checkedPosition;//记录被选中的位置
    private List<String> localVideo = new ArrayList<>();//本地视频
    private List<Integer> loaddingVideoPosition = new ArrayList<>();//正在下载的视频的位置
    private String imageUrl;
    private static int ERROR_COUNT = 1;
    private boolean isStartThisPosition = true; //当前下载位置是否开始下载

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        //注册EventBus
        EventBus.getDefault().register(this);
        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this, R.color.titlebar);
        mQueue = MyApplication.getRequestQueue();
        //获取ID和uid
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        uid = CommonMethod.getUid(this);

        initView();

        //加载数据
        adapter = new DownLoadAdapter(this, list);
        listView.setAdapter(adapter);

        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {//获取到了下载列表数据
                    if (msg.arg1 == 1) {
                        //存在已下载的文件
                        if (isExistLocalVideo()) {
                            //设置已下载的视频不可点击
                            setDownLoaddedVideoCanNotClick();
                        }
                        loaddingVideoPosition = isExistDownLoadingVideo();
                        if (!loaddingVideoPosition.isEmpty()) {
                            //存在正在下载的视频，设置正在下载的视频为选中状态
                            //存在正在下载的视频,设置正在下载的视频不可点击
                            setDownLoaddingVideoCanNotClick();
                            //显示正在下载的视频的进度条
                            showDownLoadProgress();
                        }
                        //设置全选和下载按钮可点击
                        tvAllCheck.setEnabled(true);
                        tvStartDownLoad.setEnabled(true);
                    }
                }
                if (msg.what == 2) {//下载完了一个任务
                    int position = msg.arg1;
                    position++;
                    if (position <= checkedPosition.size()) {//还有下载任务，继续下载视频
                        //下载视频
                        //获取下载地址
                        getDownLoadUrl(Urls.VIDEO_DOWN_LOAD, checkedLesson.get(position - 1).getCourse_id(),
                                uid, checkedLesson.get(position - 1).getId(), checkedPosition.get(position - 1), position);
                        //下载的那一项设置不可点击
                        CourseLessonDownload lesson = list.get(checkedPosition.get(position - 1));
                        lesson.setCheckedStatus(CourseLessonDownload.CAN_NOT_CHECKED);
                        lesson.setDownLoadStatus(CourseLessonDownload.DOWNLOADDED);
                        list.set(checkedPosition.get(position - 1), lesson);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };

        //获取数据
        if ("0".equals(uid)) {
            //跳转到登录页面
            CommonMethod.startActivity(this, LoginActivity.class);
        } else {
            //获取数据
            getData(Urls.DOWN_LOAD_LESSON);
        }
        //设置监听
        setListener();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        Typeface typeface = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        ivBack = (TextView) findViewById(R.id.iv_download_back);
        ivBack.setTypeface(typeface);
        tvAllCheck = (TextView) findViewById(R.id.tv_all_check);
        tvStartDownLoad = (TextView) findViewById(R.id.tv_start_download);
        tvMyDownLoad = (TextView) findViewById(R.id.tv_to_my_download);
        listView = (ListView) findViewById(R.id.lv_download);

    }

    private void setListener() {
        //设置监听
        ivBack.setOnClickListener(this);
        tvAllCheck.setOnClickListener(this);
        tvStartDownLoad.setOnClickListener(this);
        tvMyDownLoad.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //改变checkBox的选择状态
                CourseLessonDownload lesson = list.get(position);
                if (lesson.getCheckedStatus() == CourseLessonDownload.CHECKED) {
                    //如果之前的状态为选中，就改为没被选中
                    lesson.setCheckedStatus(CourseLessonDownload.UNCHECKED);
                } else if (lesson.getCheckedStatus() == CourseLessonDownload.UNCHECKED) {
                    //如果之前的状态为没选中，就改为选中
                    lesson.setCheckedStatus(CourseLessonDownload.CHECKED);
                }
                list.set(position, lesson);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setmProgressListener(new DownLoadAdapter.ProgressListener() {
            @Override
            public void VideoDownLoadProgress(int position, boolean isPause) {
                //下载视频
                //获取下载地址
                List<Integer>  mDownloadCourseLesson = isExistDownLoadingVideo(); //获取去正在下载视频位置列表
                CourseLessonDownload courseLessonDownload = list.get(position);  //当前点击的item 对应的CourseLessonDownload
                if (isPause) {
                    Download download = DownLoadConfig.findDownLoadByPositionAndCourseId(position, id, uid);
                    List<HttpHandler> httpHandlers = download.getHttpHandlerList();
                    if (httpHandlers != null && httpHandlers.size() > 0) {
                        for (int i = 0; i < httpHandlers.size(); i++) {
                            httpHandlers.get(i).cancel();
                        }
                        CourseLessonDownload lessson = list.get(position);
                        lessson.setCheckedStatus(CourseLessonDownload.CAN_NOT_CHECKED);
                        lessson.setDownLoadStatus(CourseLessonDownload.PAUSE);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (!mDownloadCourseLesson.isEmpty()) {
                        //显示正在下载的视频的进度条
                        showDownLoadProgress();
                    }
                    if (courseLessonDownload != null) {
                        if (mDownloadCourseLesson != null && mDownloadCourseLesson.size() > 0) {
                            for (int i = 0; i < mDownloadCourseLesson.size(); i++) {
                                int checkDownLoadPosition = mDownloadCourseLesson.get(i);
                                if (checkDownLoadPosition == position) {
                                    getDownLoadUrl(Urls.VIDEO_DOWN_LOAD, courseLessonDownload.getCourse_id(), uid, courseLessonDownload.getId(), position, i + 1);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 点击事件响应
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_download_back://返回
                finish();
                break;
            case R.id.tv_to_my_download://跳转到我的下载
                CommonMethod.startActivity(this, MyDownLoadActivity.class);
                break;
            case R.id.tv_all_check://全选
                setAllChecked();
                break;
            case R.id.tv_start_download://下载
                //获取哪些位置的checkBox被选中的
                checkedLesson = new ArrayList<>();
                checkedPosition = new ArrayList<>();
                List<Integer> positionList = new ArrayList<>();
                //把之前正在下载的加进来
                if (MyApplication.positionMap.get(id) != null) {
                    positionList.addAll(MyApplication.positionMap.get(id));
                    checkedPosition.addAll(MyApplication.positionMap.get(id));//把之前正在下载的加进来
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getCheckedStatus() == CourseLessonDownload.CHECKED) {
                        checkedLesson.add(list.get(i));
                        checkedPosition.add(i);
                        //保存下载的位置
                        positionList.add(i);
                    }
                }
                //将位置保存到map中
                MyApplication.positionMap.put(id, positionList);
                //判断是否选择了下载的内容
                if (checkedLesson.isEmpty()) {
                    CommonMethod.toast(this, "请选择要下载的视频");
                } else {
                    if (CommonMethod.isWifiConnected(this)) {
                        startDownLoad();
                    } else {
                        //弹出一个提示框
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle("警告");
                        dialog.setMessage("监测到您没有连接WiFi，下载视频会消耗您的流量，是否继续?");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //取消
                                dialog.cancel();
                            }
                        });
                        dialog.setNegativeButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //开始下载
                                startDownLoad();
                            }
                        });
                        dialog.create().show();
                    }
                }
                break;
        }
    }

    /**
     * 设置下载过的视频不可点击
     */
    private void setDownLoaddedVideoCanNotClick() {
        for (int i = 0; i < localVideo.size(); i++) {
            list:
            for (int j = 0; j < list.size(); j++) {
                if (localVideo.get(i).equals(list.get(j).getId())) {
                    CourseLessonDownload lesson = list.get(j);
                    lesson.setCheckedStatus(CourseLessonDownload.CAN_NOT_CHECKED);//设置不可点击
                    lesson.setDownLoadStatus(CourseLessonDownload.DOWNLOADDED);//设置已下载
                    list.set(j, lesson);
                    break list;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置正在下载的视频不可点击
     */
    private void setDownLoaddingVideoCanNotClick() {
        for (int i = 0; i < loaddingVideoPosition.size(); i++) {
            position:
            for (int j = 0; j < list.size(); j++) {
                if (loaddingVideoPosition.get(i) == j) {
                    //找到正在下载的位置,设置成不可点击的
                    CourseLessonDownload lesson = list.get(j);
                    lesson.setCheckedStatus(CourseLessonDownload.CAN_NOT_CHECKED);//设置不可点击
                    lesson.setDownLoadStatus(CourseLessonDownload.PRAPER_DOWNLOAD);//设置等待下载
                    list.set(j, lesson);
                    break position;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 显示下载的进度
     */
    private void showDownLoadProgress() {
        //取出下载的进度信息
        SharedPreferences sp = getSharedPreferences(CommonValues.SP_PROGRESS, MODE_PRIVATE);
        String downLoadId = sp.getString("id", "");
        int position = sp.getInt("position", 0);
        //如果正在下载的id和当前页面的id相同就显示进度
        if (downLoadId.equals(id)) {
            TextView tvProgress = (TextView) listView.findViewWithTag(sp.getInt("position", 0));
            if (tvProgress != null) {
                tvProgress.setVisibility(View.VISIBLE);
                int progress = sp.getInt("progress", 0);
                float total = sp.getFloat("total", 1);
                CourseLessonDownload courseLessonDownload = list.get(position);
                if (progress >= total) {
                    courseLessonDownload.setDownLoadStatus(CourseLessonDownload.DOWNLOADDED);
                } else if (progress > 0 && progress < total) {
                    courseLessonDownload.setDownLoadStatus(CourseLessonDownload.UNFINISH);
                    tvProgress.setText("已下载：" + (int) (progress / total * 100) + "%");
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 开始下载
     */
    private void startDownLoad() {
        //创建文件夹
        File file = new File(CommonValues.DOWNLOAD_PATH + File.separator + id);
        if (!file.exists()) {
            file.mkdir();
        }
        //保存课程名
        try {
            CommonMethod.saveVideoName(CommonValues.DOWNLOAD_PATH + File.separator + id + File.separator + CommonValues.FILE_NAME, title);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < checkedLesson.size(); i++) {
            //下载的那一项设置不可点击
            CourseLessonDownload lesson = list.get(checkedPosition.get(i));
            lesson.setCheckedStatus(CourseLessonDownload.CAN_NOT_CHECKED);
            lesson.setDownLoadStatus(CourseLessonDownload.PRAPER_DOWNLOAD);
            list.set(checkedPosition.get(i), lesson);
            adapter.notifyDataSetChanged();
        }

        //下载缩略图
        CommonMethod.downLoad(imageUrl, CommonValues.DOWNLOAD_PATH + File.separator + id + File.separator + id + ".jpeg");
        //下载视频
        //获取下载地址
        getDownLoadUrl(Urls.VIDEO_DOWN_LOAD, checkedLesson.get(0).getCourse_id(), uid, checkedLesson.get(0).getId(), checkedPosition.get(0), 1);

    }

    /**
     * 获取下载列表数据
     *
     * @param url
     */
    private void getData(String url) {
        //判断得到的URL是否为空，如果为空就加载空白页面，否则就直接加载得到的数据
//        if (url==null){
//            setContentView(R.layout.download_empty);
//        }else{
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseJson(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonMethod.loadFailureToast(DownLoadActivity.this);
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
//        }
    }

    /**
     * 解析json数据
     *
     * @param json
     */
    private void parseJson(String json) {
        List<CourseLessonDownload> tempList = new ArrayList<>();
        try {
            JSONObject j1 = new JSONObject(json);
            if ("1".equals(j1.getString("status"))) {
                JSONArray j2 = j1.getJSONArray("lesson");
                imageUrl = j1.getString("image_url");
                for (int i = 0; i < j2.length(); i++) {
                    JSONObject j3 = j2.getJSONObject(i);
                    if (!"0".equals(j3.getString("level"))) {
                        CourseLessonDownload lesson = new CourseLessonDownload();
                        lesson.setId(j3.getString("id"));
                        lesson.setTitle(j3.getString("title"));
                        lesson.setCourse_id(j3.getString("course_id"));
                        lesson.setPlayer_status(j3.getInt("player_status"));
                        lesson.setVideo_time(j3.getString("video_time"));
                        //默认设置没有下载
                        lesson.setDownLoadStatus(CourseLessonDownload.NO_DOWNLOAD);
                        //设置checkBox默认为没有选中
                        lesson.setCheckedStatus(CourseLessonDownload.UNCHECKED);
                        tempList.add(lesson);
                    }
                }
                list.addAll(tempList);
                adapter.notifyDataSetChanged();
                //通知主线程已经获取到数据
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = 1;
                mainHandler.sendMessage(msg);
            } else {
                CommonMethod.toast(this, "找不到数据");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将下载完的位置从正在下载中移除
     *
     * @param position
     */
    private void removeDownLoaddingPosition(int position, String downLoadId) {
        //取出保存的下载位置
        List<Integer> list = MyApplication.positionMap.get(downLoadId);
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (it.next() == position) {
                //将下载完的位置移除
                it.remove();
            }
        }
        //保存新的list
        MyApplication.positionMap.put(downLoadId, list);
    }

    /**
     * 下载
     *
     * @param downLoadUrl 下载地址
     * @param targetPath  下载的目的路径
     * @param total       下载的总数
     * @param position    在列表中的位置
     * @param downLoadId  下载的课程id
     * @param downLoadLid 下载的id
     * @param currentDownload           第几个下载任务
     */
    public HttpHandler downLoad(String downLoadUrl, final String targetPath, final float total, final int position, final String downLoadId, final String downLoadLid, final int currentDownload) {
        HttpUtils http = new HttpUtils();
        final HttpHandler handler = http.download(downLoadUrl, targetPath,
                true,// 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true,// 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public synchronized void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        //当前下载项刚开始下载
                        if (isStartThisPosition && current > 0) {
                            list.get(position).setCheckedStatus(CourseLessonDownload.CAN_NOT_CHECKED);
                            list.get(position).setDownLoadStatus(CourseLessonDownload.DOWNLOADING);
                            adapter.notifyDataSetChanged();
                            isStartThisPosition = false;
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        //将每项下载任务的进度单独存储
                        SharedPreferences sp1 = getSharedPreferences(downLoadLid + "progress", MODE_PRIVATE);
                        //如果是第一次下载默认当前进度为0
                        int downcount = sp1.getInt("downcount", 0);
                        //如果取出来的进度和下载的总数相等就让它从0开始
                        if (downcount >= total) {
                            downcount = 0;
                        }
                        downcount++;
                        if (downcount >= total) {
                            CourseLessonDownload lesson = list.get(position);
                            lesson.setCheckedStatus(CourseLessonDownload.CAN_NOT_CHECKED);//设置不可点击
                            lesson.setDownLoadStatus(CourseLessonDownload.DOWNLOADDED);//设置已下载
                            list.set(position, lesson);
                            adapter.notifyDataSetChanged();
                            //表示该项下载完了，将下载完成了的位置从正在下载中移除
                            removeDownLoaddingPosition(position, downLoadId);
                            CommonMethod.toast(DownLoadActivity.this, "有一个任务已下载完成!");
                            //告诉主线程下载完了
                            Message message = Message.obtain();
                            message.what = 2;
                            message.arg1 = currentDownload;
                            mainHandler.sendMessage(message);
                        }
                        //将当前下载完的进度存起来
                        SharedPreferences.Editor editor1 = sp1.edit();
                        editor1.putInt("downcount", downcount);
                        editor1.commit();
                        //将当前正在下载的item的位置和下载的进度通过EventBus发送,在退出下载页面后也会继续更新进度
                        ProgressValues values = new ProgressValues();
                        values.setPosition(position);
                        values.setDowncount(downcount);
                        values.setTotal(total);
                        values.setId(downLoadId);
                        EventBus.getDefault().post(values);
                        //将当前正在下载的item的位置和下载的进度保存，当再一次进来下载页面后可以直接显示上一次的进度，不需要等到更新了之后才显示进度
                        SharedPreferences sp = getSharedPreferences(CommonValues.SP_PROGRESS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("position", position);
                        editor.putInt("progress", downcount);
                        editor.putFloat("total", total);
                        editor.putString("id", downLoadId);
                        editor.commit();
                        if ("m3u8".equals(targetPath.substring(targetPath.lastIndexOf(".") + 1))) {
                            //修改m3u8文件
                            try {
                                upDataM3U8(targetPath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public synchronized void onFailure(HttpException error, String msg) {
                        //将下载失败的位置从正在下载中移除
                        removeDownLoaddingPosition(position, downLoadId);
                        TextView tvProgress = (TextView) listView.findViewWithTag(position);
                        if (tvProgress != null) {
                            if ("maybe the file has downloaded completely".equals(msg)) {
                                tvProgress.setVisibility(View.VISIBLE);
                                tvProgress.setText("下载完成");
                            } else {
                                tvProgress.setVisibility(View.VISIBLE);
                                tvProgress.setText("下载失败");
                            }
                        }
                        if (ERROR_COUNT == 1) {
                            Toast.makeText(DownLoadActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                        ERROR_COUNT++;
                    }
                });
        return handler;
    }

    /**
     * 获取下载视频的地址
     *
     * @param url
     * @param id
     * @param uid      用户id
     * @param lid      课程id
     * @param position 当前下载的item在listview中的位置
     * @param currentDownload        第几个下载任务
     */
    private void getDownLoadUrl(final String url, final String id, final String uid, final String lid, final int position, final int currentDownload) {
        isStartThisPosition = true;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public synchronized void onResponse(String response) {
                        Gson gson = new Gson();
                        VideoDownLoad video = gson.fromJson(response, VideoDownLoad.class);
                        if (video.getStatus() == 1) {
                            video_ts = video.getVideo_ts();
                            video_ts.add(video.getVideo_file());
                            //设置下载路径
                            String targetPath = CommonValues.DOWNLOAD_PATH + File.separator + id
                                    + File.separator + lid;
                            //创建下载路径文件夹
                            File file = new File(targetPath);
                            if (!file.exists()) {
                                file.mkdir();
                            }
                            //记录该视频下载的文件总数
                            SharedPreferences sp = getSharedPreferences(CommonValues.SP_DOWNLOAD_VIDEO_TOTAL, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt(lid, video_ts.size() + 1);
                            editor.commit();
                            //通过for循环逐个下载
                            List<HttpHandler> mHttpHandlerList = new ArrayList<>();

                            for (int i = 0; i < video_ts.size(); i++) {
                                if (i == video_ts.size() - 1) {//如果是m3u8文件
                                    HttpHandler httpHandler = downLoad(video_ts.get(i), targetPath + File.separator + video.getVideo_name(), video_ts.size(), position, id, lid, currentDownload);
                                    mHttpHandlerList.add(httpHandler);
                                } else {//如果是ts文件
                                    HttpHandler httpHandler = downLoad(video_ts.get(i), targetPath + File.separator + "video_" + i + ".ts", video_ts.size(), position, id, lid, currentDownload);
                                    mHttpHandlerList.add(httpHandler);
                                }
                            }
                            Download download = new Download(url,id,uid,lid,position,currentDownload,mHttpHandlerList);
                            DownLoadConfig.RemoveDownLoadByPositionAndCourseId(position,id,uid);
                            DownLoadConfig.downloadList.add(download);
                            //保存视频名称
                            try {
                                CommonMethod.saveVideoName(targetPath + File.separator + CommonValues.FILE_NAME, video.getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //创建ts文件
                            writeTSTFile(targetPath, video.getVideo_key());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(DownLoadActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("uid", uid);
                params.put("lid", lid);
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 设置全部选中和取消全选
     */
    private void setAllChecked() {
        if (checkCount % 2 == 1) {//要全选
            //把checkBox的状态全部设置为true
            for (int i = 0; i < list.size(); i++) {
                //判断是否为可以下载的视频
                if (list.get(i).getPlayer_status() == 1) {
                    CourseLessonDownload lesson = list.get(i);
                    if (CourseLessonDownload.CAN_NOT_CHECKED != lesson.getCheckedStatus()) {
                        //如果不是不可点击的选项则将它设置为选中状态
                        lesson.setCheckedStatus(CourseLessonDownload.CHECKED);
                        list.set(i, lesson);
                    }
                }
            }
            //重新加载数据
            adapter.notifyDataSetChanged();
            checkCount++;
            tvAllCheck.setText("取消全选");
        } else {//取消全选
            //把checkBox的状态全部设置为false
            for (int i = 0; i < list.size(); i++) {
                CourseLessonDownload lesson = list.get(i);
                if (CourseLessonDownload.CAN_NOT_CHECKED != lesson.getCheckedStatus()) {
                    //如果不是不可点击的选项则将它设置未没选中的状态
                    lesson.setCheckedStatus(CourseLessonDownload.UNCHECKED);
                    list.set(i, lesson);
                }
            }
            //重新加载数据
            adapter.notifyDataSetChanged();
            checkCount++;
            tvAllCheck.setText("全选");
        }
    }

    /**
     * 生成ts.text文件
     */
    private void writeTSTFile(String path, String key) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        File file = new File(path + File.separator + "ts.txt");
        //创建文件
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入内容
        try {
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(key);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否存在正在下载的视频
     *
     * @return
     */
    private List<Integer> isExistDownLoadingVideo() {
        List<Integer> temp = new ArrayList<>();
        //取出保存的正在下载的位置
        Map<String, List<Integer>> map = MyApplication.positionMap;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (id.equals(entry.getKey().toString())) {
                //如果取的是当前页面显示的id就显示正在下载的位置
                temp = (List<Integer>) entry.getValue();
            }
        }
        return temp;
    }

    /**
     * 是否存在下载过的视频
     *
     * @return
     */
    private boolean isExistLocalVideo() {
        boolean havaVideo = false;
        File idFile = new File(CommonValues.DOWNLOAD_PATH + File.separator + id);
        if (!idFile.exists()) {//如果这个文件夹不存在
            return false;
        } else {
            File[] lidFiles = idFile.listFiles();
            if (lidFiles.length == 0) {
                //如果这个文件夹为空的
                return false;
            } else {
                //查询文件夹里面的文件
                for (int i = 0; i < lidFiles.length; i++) {
                    if (lidFiles[i].isDirectory()) {
                        //如果是文件夹，就查询该文件夹里面文件的总数是否和下载的文件的总数一致
                        File[] lidFile = lidFiles[i].listFiles();
                        //获取完整视频文件的总数
                        SharedPreferences sp = getSharedPreferences(CommonValues.SP_DOWNLOAD_VIDEO_TOTAL, MODE_PRIVATE);
                        int videoTotal = sp.getInt(lidFiles[i].getName(), -1);
                        //该文件夹中是否是完整的视频文件
                        if (videoTotal <= lidFile.length) {
                            //查找该文件夹下的文件是否有m3u8文件
                            for (int j = 0; j < lidFile.length; j++) {
                                if ("m3u8".equals(lidFile[j].getName().substring(lidFile[j].getName().
                                        lastIndexOf(".") + 1))) {//如果存在m3u8文件
                                    localVideo.add(lidFiles[i].getName());
                                    havaVideo = true;
                                }
                            }
                        }
                    }
                }
                if (havaVideo) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修改m3u8文件
     */
    private void upDataM3U8(String filePath) throws IOException {
        int lineCount = 1;
        BufferedReader br = null;
        String line = null;
        StringBuffer sb = new StringBuffer();
        br = new BufferedReader(new FileReader(filePath));
        while ((line = br.readLine()) != null) {
            if (lineCount >= 8) {//修改ts路径
                if (line.startsWith("http://")) {//http开头的
                    sb.append(filePath.substring(0, filePath.lastIndexOf("/")) + "/" + line.substring(line.lastIndexOf("v")) + System.getProperty("line.separator"));
                } else {
                    sb.append(line + System.getProperty("line.separator"));
                }
            } else if (lineCount == 3) {//修改key路径
                String start = line.substring(0, line.lastIndexOf("I") + 1);
                sb.append(start + "=\"" + filePath.substring(0, filePath.lastIndexOf("/")) + "/ts.txt\"" + System.getProperty("line.separator"));
            } else {//保存原来的内容
                sb.append(line + System.getProperty("line.separator"));
            }
            lineCount++;
        }
        if (br != null) {
            br.close();
        }
        //删除原来的文件
        File file = new File(filePath);
        file.delete();
        //创建新的文件
        File newFile = new File(filePath);
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        //将内容写入文件中
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
        bw.write(sb.toString());
        bw.flush();
        if (bw != null) {
            bw.close();
        }
    }

    /**
     * 接收EventBus传过来的数据
     * 更新进度条
     *
     * @param values
     */
    public void onEventMainThread(ProgressValues values) {
        if ("reload".equals(values.getStatus())) {//被通知重新加载
            //重新获取uid
            uid = CommonMethod.getUid(this);
            //加载
            getData(Urls.DOWN_LOAD_LESSON);
        }
        if (id.equals(values.getId())) {
            //如果当前的id和正在下载的id相同就显示进度
            TextView tvProgress = (TextView) listView.findViewWithTag(values.getPosition());
            if (tvProgress != null) {
                tvProgress.setVisibility(View.VISIBLE);
                if (values.getDowncount() == values.getTotal()) {
                    //下载完成
                    tvProgress.setText("下载完成");
                } else {
                    //显示下载的进度
                    tvProgress.setText("已下载：" + (int) (values.getDowncount() / values.getTotal() * 100) + "%");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
