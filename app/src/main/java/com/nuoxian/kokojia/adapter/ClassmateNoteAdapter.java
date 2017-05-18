package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/24.
 */
public class ClassmateNoteAdapter extends BaseAdapter {

    private List<Note.CoursenoteBean> mlist;
    private Context context;

    public ClassmateNoteAdapter(List<Note.CoursenoteBean> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_classmate_note,parent,false);
            ViewHoder vh = new ViewHoder();
            vh.imageView = (ImageView) convertView.findViewById(R.id.iv_classmate_note);
            vh.username = (TextView) convertView.findViewById(R.id.tv_classmate_note_username);
            vh.content = (TextView) convertView.findViewById(R.id.tv_classmate_note_content);
            vh.time = (TextView) convertView.findViewById(R.id.tv_classmate_note_time);
            vh.ding = (TextView) convertView.findViewById(R.id.tv_classmate_note_ding);
            convertView.setTag(vh);
        }
        convertView.setClickable(true);
        CommonMethod.setFontAwesome(convertView.findViewById(R.id.layout_item_classmate_note), context);
        ViewHoder vh = (ViewHoder) convertView.getTag();
        vh.username.setText(mlist.get(position).getNickname());//用户名
        vh.content.setText(mlist.get(position).getContent());//笔记内容
        vh.time.setText(mlist.get(position).getTime());//时间
        vh.ding.setText("(" + mlist.get(position).getVote_num() + ")");//顶的数量
        MyApplication.imageLoader.displayImage(mlist.get(position).getStudent_avatar(), vh.imageView, MyApplication.options);

        //顶
        final AutoLinearLayout llDing = (AutoLinearLayout) convertView.findViewById(R.id.ll_classmate_note_zan);
        llDing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dingNote(position);
            }
        });
        return convertView;
    }

    /**
     * 笔记点赞
     * @param position
     */
    private void dingNote(final int position){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.DING_NOTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                //点赞成功
                                Note.CoursenoteBean noteBean = mlist.get(position);
                                noteBean.setVote_num(j1.getString("count"));
                                mlist.set(position,noteBean);
                                notifyDataSetChanged();
                            }else{
                                //点赞失败
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
                params.put("id",mlist.get(position).getId());
                params.put("uid",CommonMethod.getUid(context));
                return params;
            }
        };
        mQueue.add(request);
    }

    private class ViewHoder{
        public TextView username,content,time,ding;
        public ImageView imageView;
    }
}
