package com.nuoxian.kokojia.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.LocalVideoAdapter;
import com.nuoxian.kokojia.enterty.CourseCatalog;
import com.nuoxian.kokojia.enterty.LocalVideo;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本地视频列表
 */
public class LocalVideoActivity extends AutoLayoutActivity implements View.OnClickListener {

    private TextView ivBack;
    private TextView tvEdit;
    private ListView listView;
    private String idDirPath;
    private List<LocalVideo> videoList = new ArrayList<>();
    private LocalVideoAdapter adapter;
    private boolean isEdit=false;
    private boolean isAllCheck = true;
    private LinearLayout layout;
    private TextView tvAllCheck,tvDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video);

        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        initView();
        Intent intent = getIntent();
        idDirPath = intent.getStringExtra("idDirPath");
        videoList = CommonMethod.getM3U8Files(idDirPath,this);
        adapter = new LocalVideoAdapter(videoList,this);
        listView.setAdapter(adapter);

        if(videoList.isEmpty()){
            tvEdit.setVisibility(View.GONE);
        }else{
            tvEdit.setVisibility(View.VISIBLE);
        }

        setListener();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        Typeface tf = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        ivBack = (TextView)findViewById(R.id.iv_local_video_back);
        ivBack.setTypeface(tf);
        tvEdit = (TextView)findViewById(R.id.tv_local_video_edit);
        listView = (ListView) findViewById(R.id.lv_local_video);
        layout = (LinearLayout) findViewById(R.id.ll_local_video);
        tvAllCheck = (TextView) findViewById(R.id.tv_local_video_allcheck);
        tvDelete = (TextView) findViewById(R.id.tv_local_video_delete);
    }

    /**
     * 设置监听
     */
    private void setListener(){
        ivBack.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        tvAllCheck.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isEdit){//如果在编辑的状态下
                    LocalVideo video = videoList.get(position);
                    video.setCheck(!video.isCheck());
                    videoList.set(position, video);
                    adapter.notifyDataSetChanged();
                }else{//不在编辑状态下
                    //跳转到本地播放页面
                    Intent intent = new Intent(LocalVideoActivity.this,LocalPlayActivity.class);
                    intent.putExtra("path", videoList.get(position).getPath());
                    startActivity(intent);
                    //记录播放地址
                    SharedPreferences sp = getSharedPreferences(CommonValues.SP_PLAY,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(CommonValues.LAST_PLAY_PATH,videoList.get(position).getPath());
                    editor.commit();
                    //给选中的item设置字体颜色
                    for (int i=0;i<videoList.size();i++){
                        LocalVideo video = videoList.get(i);
                        if(i==position){
                            video.setSelect(true);
                        }else{
                            video.setSelect(false);
                        }
                        videoList.set(i,video);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_local_video_back://返回
                finish();
                break;
            case R.id.tv_local_video_edit://编辑
                isEdit = !isEdit;
                if(isEdit){//点击了编辑
                    setEdit(false,true,"完成");
                    layout.setVisibility(View.VISIBLE);
                }else{//点击了完成
                    setEdit(false,false,"编辑");
                    layout.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_local_video_allcheck://全选
                if(isAllCheck){//点击全选
                    setAllCheck(true,"取消全选");
                }else{//点击取消全选
                    setAllCheck(false,"全选");
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_local_video_delete://删除
                //弹出一个提示框
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("警告");
                dialog.setMessage("系统将删除您选中的文件，是否继续?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.setNegativeButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> dirPath = new ArrayList<>();
                        //获取哪些被选中了
                        for(int i=0;i<videoList.size();i++){
                            LocalVideo video = videoList.get(i);
                            if(video.isCheck()){
                                dirPath.add(video.getDirPath());
                            }
                        }
                        //删除文件
                        for(int i=0;i<dirPath.size();i++){
                            deleteFiles(dirPath.get(i));
                        }
                        //重新获取
                        videoList = new ArrayList<LocalVideo>();
                        videoList = CommonMethod.getM3U8Files(idDirPath,LocalVideoActivity.this);
                        adapter = new LocalVideoAdapter(videoList,LocalVideoActivity.this);
                        listView.setAdapter(adapter);
                        //将编辑变成完成状态
                        tvEdit.setText("编辑");
                        isEdit = false;
                        layout.setVisibility(View.GONE);
                        if(videoList.isEmpty()){
                            //没有本地视频了，隐藏编辑
                            tvEdit.setVisibility(View.GONE);
                        }else{
                            //还有视频，显示编辑
                            tvEdit.setVisibility(View.VISIBLE);
                        }
                    }
                });
                dialog.create().show();

                break;
        }
    }

    /**
     * 删除文件
     * @param path
     */
    private void deleteFiles(String path){
        File f = new File(path);
        if(f.length()==0){
            f.delete();
            return;
        }else{
            File[] files = f.listFiles();
            for(int i=0;i<files.length;i++){
                File file = files[i];
                file.delete();
            }
            f.delete();
        }
    }

    /**
     * 点击编辑的设置
     * @param isCheck
     * @param isVisible
     * @param text
     */
    private void setEdit(boolean isCheck,boolean isVisible,String text){
        for (int i=0;i<videoList.size();i++){
            LocalVideo video = videoList.get(i);
            video.setCheck(isCheck);
            video.setVisible(isVisible);
            videoList.set(i, video);
        }
        tvEdit.setText(text);
    }

    /**
     * 设置全选
     */
    private void setAllCheck(boolean isCheck,String text){
        for (int i=0;i<videoList.size();i++){
            LocalVideo video = videoList.get(i);
            video.setCheck(isCheck);
        }
        tvAllCheck.setText(text);
        isAllCheck = !isAllCheck;
    }

}
