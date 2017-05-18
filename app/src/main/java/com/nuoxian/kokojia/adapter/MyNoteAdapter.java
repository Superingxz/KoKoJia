package com.nuoxian.kokojia.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Note;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.CommonValues;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/24.
 */
public class MyNoteAdapter extends BaseAdapter {

    private List<Note.MynoteBean> mList;
    private Context context;
    private String note;

    public MyNoteAdapter(List<Note.MynoteBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_note,parent,false);
            ViewHoder vh = new ViewHoder();
            vh.imageView = (ImageView) convertView.findViewById(R.id.iv_my_note);
            vh.userName = (TextView) convertView.findViewById(R.id.tv_my_note_username);
            vh.content = (TextView) convertView.findViewById(R.id.tv_my_note_content);
            vh.time = (TextView) convertView.findViewById(R.id.tv_my_note_time);
            convertView.setTag(vh);
        }
        CommonMethod.setFontAwesome(convertView.findViewById(R.id.layout_item_my_note), context);
        ViewHoder vh = (ViewHoder) convertView.getTag();
        vh.userName.setText(mList.get(position).getNickname());//用户名
        vh.content.setText(mList.get(position).getContent());//笔记
        vh.time.setText(mList.get(position).getTime());//时间
        MyApplication.imageLoader.displayImage(mList.get(position).getStudent_avatar(), vh.imageView, MyApplication.options);

        //编辑
        AutoLinearLayout edit = (AutoLinearLayout) convertView.findViewById(R.id.ll_my_note_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNote(mList.get(position).getContent(),position);
            }
        });
        //删除
        AutoLinearLayout delete = (AutoLinearLayout) convertView.findViewById(R.id.ll_my_note_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("警告");
                builder.setMessage("确定删除此条笔记?");
                builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote(position);
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
        });

        return convertView;
    }

    /**
     * 删除笔记
     * @param position
     */
    private void deleteNote(final int position){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.DELETE_NOTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //删除成功
                                mList.remove(position);
                                notifyDataSetChanged();
                            }else{
                                //删除失败
                                CommonMethod.showAlerDialog("",j1.getString("msg"),context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(context);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",mList.get(position).getId());
                params.put("uid",CommonMethod.getUid(context));
                return params;
            }
        };
        mQueue.add(request);
    }

    /**
     * 编辑笔记
     * @param content
     * @param position
     */
    private void editNote(String content, final int position){
        //自定义dialog，显示编辑笔记界面
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_my_note,null);
        EditText etNote = (EditText) view.findViewById(R.id.et_note);
        etNote.setText(content);
        Button cancle = (Button) view.findViewById(R.id.btn_cancle);
        Button commit = (Button) view.findViewById(R.id.btn_commit);
        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        //获取用户输入的笔记
        etNote.addTextChangedListener(new TextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                note = s + "";
            }
        });
        //取消
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
            }
        });
        //提交
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(note)){
                    dialog.dismiss();
                    commitNote(position);
                }
            }
        });
    }

    /**
     * 提交笔记
     */
    private void commitNote(final int position){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.COMMIT_NOTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //提交成功，刷新数据
                                Note.MynoteBean myNote = mList.get(position);
                                myNote.setContent(note);
                                mList.set(position,myNote);
                                notifyDataSetChanged();
                            }else{
                                //提交失败
                                CommonMethod.showAlerDialog("",j1.optString("msg"),context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(context);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid",CommonMethod.getUid(context));
                params.put("content",note);
                params.put("course_id",mList.get(position).getCourse_id());
                params.put("lesson_id",mList.get(position).getCourse_lesson_id());
                params.put("note_id",mList.get(position).getId());
                return params;
            }
        };
        mQueue.add(request);
    }

    private class ViewHoder{
        public ImageView imageView;
        public TextView userName,content,time;
    }
}