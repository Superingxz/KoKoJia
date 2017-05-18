package com.nuoxian.kokojia.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nuoxian.kokojia.adapter.DetailsDowmLoadAdapter;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.CourseFile;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/8.
 */
public class CourseDetialsDownLoadFragment extends Fragment {

    private View view;
    private ListView lv;
    private List<CourseFile> list = new ArrayList<>();
    private String id,uid;
    private String buyStatus;
    private DetailsDowmLoadAdapter adapter;
    private RequestQueue mQueue;
    private String downLoadUrl,fileName;
//    private LinearLayout llProgress;
//    private View listHead;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_download, container, false);
//        listHead = inflater.inflate(R.layout.details_down_load_list_head,container,false);
        mQueue = MyApplication.getRequestQueue();
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        SharedPreferences sp = getContext().getSharedPreferences("flag", getContext().MODE_PRIVATE);
        uid = sp.getString("data","0");

//        llProgress = (LinearLayout) listHead.findViewById(R.id.ll_details_progress);
        lv = (ListView) view.findViewById(R.id.lv_details_download);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //获取下载地址,和文件名
//                getDownLoadUrl(Urls.DOWN_LOAD_COURSE, list.get(position).getId(), position);
//            }
//        });
//        lv.addHeaderView(listHead);
        //加载数据
        adapter = new DetailsDowmLoadAdapter(list,getContext());
        lv.setAdapter(adapter);
        //获取数据
        getData(Urls.getCourseLectureUrl(id, uid));

        return view;
    }

    /**
     * 获取数据
     * @param url
     */
    private void getData(String url){
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJson(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"加载失败，请检查网络!",Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(request);
    }

    /**
     * 解析json数据
     * @param json
     */
    private void parseJson(String json){
        List<CourseFile> tempList = new ArrayList<>();
        try {
            JSONObject j1 = new JSONObject(json);
            if("1".equals(j1.getString("status"))){
                JSONObject j2 = j1.getJSONObject("course");
                buyStatus = j2.getString("buy_status");
                JSONArray j3 = j1.getJSONArray("lecture");
                for (int i =0;i<j3.length();i++){
                    JSONObject j4 = j3.getJSONObject(i);
                    CourseFile file = new CourseFile();
                    file.setId(j4.getString("id"));
                    file.setTitle(j4.getString("title"));
                    tempList.add(file);
                }
                list.addAll(tempList);
                if(list.isEmpty()){
                    Toast.makeText(getContext(),"暂时没有课件!",Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getContext(),"没有数据!",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取下载地址和文件名
     * @param url
     */
//    private void getDownLoadUrl(String url, final String downLoadId,final int position){
//        StringRequest request = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject j1 = new JSONObject(response);
//                            if("1".equals(j1.getString("status"))){
//                                downLoadUrl = j1.getString("data");
//                                fileName = j1.getString("title");
//                                //下载
//                                CommonMethod.makeDownLoadDir(id);//创建下载文件夹
//                                downLoad(downLoadUrl,id,fileName,position);
//                            }else if("0".equals(j1.getString("status"))){
//                                Toast.makeText(getContext(),j1.getString("msg"),Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(),"加载失败,请检查网络!",Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("id",downLoadId);
//                params.put("uid",uid);
//                return params;
//            }
//        };
//        mQueue.add(request);
//    }

    /**
     * 下载
     * @param url
     */
//    private void downLoad(String url,String dirName, final String fileName,final int position){
//        final TextView progress = new TextView(getContext());
//        HttpUtils http = new HttpUtils();
//        HttpHandler handler = http.download(url,
//                Environment.getExternalStorageDirectory()+File.separator+"kokojia"+File.separator+dirName+File.separator+fileName,
//                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
//                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
//                new RequestCallBack<File>() {
//
//                    @Override
//                    public void onStart() {//开始下载
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//                        progress.setLayoutParams(params);
//                        llProgress.addView(progress);
//                        lv.getChildAt(position).setSelected(true);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onLoading(long total, long current, boolean isUploading) {
//                        progress.setText(fileName + ":已下载 "+(int)(((double)current/total)*100)+"%");
//                    }
//
//                    @Override
//                    public void onSuccess(ResponseInfo<File> responseInfo) {
//                        Toast.makeText(getContext(),"下载完成!",Toast.LENGTH_SHORT).show();
//                        llProgress.removeView(progress);
//                    }
//
//
//                    @Override
//                    public void onFailure(HttpException error, String msg) {
//                        if("maybe the file has downloaded completely".equals(msg)){
//                            Toast.makeText(getContext(),"该文件已下载了",Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(getContext(),"下载失败",Toast.LENGTH_SHORT).show();
//                        }
//                        llProgress.removeView(progress);
//                    }
//                });
//    }

}
