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

import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.adapter.LocalVideoAdapter;
import com.nuoxian.kokojia.adapter.MyDownLoadAdapter;
import com.nuoxian.kokojia.enterty.LocalVideo;
import com.nuoxian.kokojia.enterty.MyDownLoadCourse;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.FontManager;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.AutoLinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
    我的下载页面
 */
public class MyDownLoadActivity extends AutoLayoutActivity implements View.OnClickListener{

    private TextView tvEdit,tvAllCheck,tvDelete,ivBack;
    private ListView listView;
    private List<MyDownLoadCourse> videoList;
    private MyDownLoadAdapter adapter;
    private LinearLayout layout;
    private boolean isEdit=false;
    private boolean isAllCheck=true;
    private AutoLinearLayout llNoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_down_load);

        //设置标题栏颜色
        CommonMethod.setTitleBarBackground(this,R.color.titlebar);
        initView();
        //获取本地视频
        videoList = new ArrayList<>();
        videoList = getLocalVideo();
        adapter = new MyDownLoadAdapter(videoList,this);
        listView.setAdapter(adapter);

        if(videoList.isEmpty()){//没有本地视频
            tvEdit.setVisibility(View.GONE);
            llNoContent.setVisibility(View.VISIBLE);
        }else{//有
            tvEdit.setVisibility(View.VISIBLE);
            llNoContent.setVisibility(View.GONE);
        }

        setListener();
    }

    private void initView(){
        Typeface typeface = FontManager.getTypeface(this,FontManager.FONTAWESOME);
        tvEdit = (TextView) findViewById(R.id.tv_my_download_edit);
        ivBack = (TextView) findViewById(R.id.iv_my_download_back);
        ivBack.setTypeface(typeface);
        listView = (ListView) findViewById(R.id.lv_my_download);
        layout = (LinearLayout) findViewById(R.id.ll_my_download);
        tvAllCheck = (TextView) findViewById(R.id.tv_my_download_allcheck);
        tvDelete = (TextView) findViewById(R.id.tv_my_download_delete);
        llNoContent = (AutoLinearLayout) findViewById(R.id.ll_no_content);
        CommonMethod.setFontAwesome(llNoContent,this);
    }

    /**
     * 设置监听
     */
    private void setListener(){
        tvEdit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvAllCheck.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isEdit){//在编辑的状态下
                    MyDownLoadCourse course = videoList.get(position);
                    course.setCheck(!course.isCheck());
                    videoList.set(position, course);
                    adapter.notifyDataSetChanged();
                }else{//在没有编辑的状态下
                    //跳转
                    Intent intent = new Intent(MyDownLoadActivity.this,LocalVideoActivity.class);
                    intent.putExtra("idDirPath", videoList.get(position).getCurrentDirPath());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_my_download_edit://编辑
                isEdit = !isEdit;
                if(isEdit){//点击了编辑
                    tvEdit.setText("完成");
                    //显示所有checkBox
                    for(int i=0;i<videoList.size();i++){
                        MyDownLoadCourse course = videoList.get(i);
                        course.setVisible(true);
                        videoList.set(i,course);
                    }
                    layout.setVisibility(View.VISIBLE);
                }else{//点击了完成
                    tvEdit.setText("编辑");
                    //让所有的checkBox隐藏并且没被选中
                    for(int i=0;i<videoList.size();i++){
                        MyDownLoadCourse course = videoList.get(i);
                        course.setVisible(false);
                        course.setCheck(false);
                        videoList.set(i,course);
                    }
                    layout.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.iv_my_download_back://返回
                finish();
                break;
            case R.id.tv_my_download_allcheck://点击全选
                if(isAllCheck){//全选
                    //让所有checkBox被选中
                    allCheck(true,"取消全选");
                }else{//取消全选
                    //让所有checkBox不被选中
                    allCheck(false,"全选");
                }
                adapter.notifyDataSetChanged();
                isAllCheck = !isAllCheck;
                break;
            case R.id.tv_my_download_delete://删除
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
                        //获取哪些被选中了
                        List<String> dirPath = new ArrayList<String>();
                        for(int i=0;i<videoList.size();i++){
                            if(videoList.get(i).isCheck()){
                                dirPath.add(videoList.get(i).getCurrentDirPath());
                            }
                        }
                        //删除文件
                        for(int i=0;i<dirPath.size();i++){
                            deleteFiles(dirPath.get(i));
                        }
                        //重新获取
                        videoList = new ArrayList<MyDownLoadCourse>();
                        videoList=getLocalVideo();
                        adapter = new MyDownLoadAdapter(videoList,MyDownLoadActivity.this);
                        listView.setAdapter(adapter);
                        //将编辑变成完成状态
                        tvEdit.setText("编辑");
                        isEdit = false;
                        layout.setVisibility(View.GONE);
                        if(videoList.isEmpty()){
                            //没有本地视频了，隐藏编辑
                            tvEdit.setVisibility(View.GONE);
                        }else{
                            //还有,显示
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
        File idFile = new File(path);
        //这里是有的手机删除文件后不能再下载，是FAT32 system的原因，所以先重命名再删除
        File to = new File(idFile.getAbsolutePath()+System.currentTimeMillis());
        idFile.renameTo(to);
        if(to.exists()){
            if(to.length()==0){
                to.delete();
                return;
            }else{
                File[] lidFiles = to.listFiles();
                for(int i=0;i<lidFiles.length;i++){
                    if(lidFiles[i].isFile()){
                        lidFiles[i].delete();
                    }else if(lidFiles[i].isDirectory()){
                        File[] videos = lidFiles[i].listFiles();
                        for(int j=0;j<videos.length;j++){
                            videos[j].delete();
                        }
                        lidFiles[i].delete();
                    }
                }
                to.delete();
            }
        }
    }

    /**
     * 全选或者取消全选
     * @param check
     * @param text
     */
    private void allCheck(boolean check,String text){
        for(int i=0;i<videoList.size();i++){
            MyDownLoadCourse course = videoList.get(i);
            course.setCheck(check);
            videoList.set(i,course);
        }
        tvAllCheck.setText(text);
    }

    /**
     * 获取本地的视频文件
     */
    private List<MyDownLoadCourse> getLocalVideo(){
        List<MyDownLoadCourse> list = new ArrayList<>();
        File file = new File(CommonValues.DOWNLOAD_PATH);
        if(file.exists()){
            File[] idFiles = file.listFiles();
            if(idFiles.length!=0){
                for(int i=0;i<idFiles.length;i++){
                    if(idFiles[i].isDirectory()&&idFiles[i].length()!=0){//以id为名的文件夹
                        //获取id
                        String id = idFiles[i].getName();
                        File[] lidFiles = idFiles[i].listFiles();
                        MyDownLoadCourse course = new MyDownLoadCourse();
                        video:for(int j=0;j<lidFiles.length;j++){
                            if (lidFiles[j].isDirectory()&&lidFiles[j].length()!=0){//如果不是空的文件夹
                                File[] lidFile = lidFiles[j].listFiles();
                                //取出对应的完整的视频总数
                                SharedPreferences sp1 = getSharedPreferences(CommonValues.SP_DOWNLOAD_VIDEO_TOTAL,MODE_PRIVATE);
                                int filesTotal = sp1.getInt(lidFiles[j].getName(),-1);
                                //如果文件总数小于该文件的数量，该视频是完整的
                                if(filesTotal<=lidFile.length){
                                    for(int k=0;k<lidFile.length;k++){
                                        if(lidFile[k].isFile()){
                                            //判断是否为m3u8文件
                                            String fileName = lidFile[k].getName();
                                            if("m3u8".equals(fileName.substring(fileName.lastIndexOf(".")+1))){
                                                //如果是m3u8文件
//                                                SharedPreferences sp = MyDownLoadActivity.this.getSharedPreferences("fileName",MODE_PRIVATE);
                                                course.setImage(idFiles[i].getAbsolutePath()+File.separator+idFiles[i].getName()+".jpeg");
                                                try {
                                                    course.setName(CommonMethod.getVideoName(CommonValues.DOWNLOAD_PATH+File.separator+id+File.separator+CommonValues.FILE_NAME));
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                course.setCurrentDirPath(idFiles[i].getAbsolutePath());
                                                course.setVisible(false);
                                                course.setCheck(false);
                                                list.add(course);
                                                break video;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

}
