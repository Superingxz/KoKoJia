package com.nuoxian.kokojia.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
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
import com.nuoxian.kokojia.enterty.AnswerDetails;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.nuoxian.kokojia.utils.FontManager;
import com.nuoxian.kokojia.utils.TextChangeListener;
import com.zhy.autolayout.AutoLinearLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/19.
 */
public class TeacherAnswerDetailsAdapter extends BaseAdapter {

    private List<AnswerDetails.CommentlistBean> list;
    private Context context;
    private String replyContent;
    private String id;

    public TeacherAnswerDetailsAdapter(List<AnswerDetails.CommentlistBean> list, Context context,String id) {
        this.list = list;
        this.context = context;
        this.id = id;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_answer_details,parent,false);
        }
        Typeface typeface = FontManager.getTypeface(context,FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(convertView.findViewById(R.id.layout_item_answer_details),typeface);
        TextView user = (TextView) convertView.findViewById(R.id.tv_item_answer_user);//提问者
        TextView replyUser = (TextView) convertView.findViewById(R.id.tv_item_answer_replyuser);//回复者
        TextView content = (TextView) convertView.findViewById(R.id.tv_item_answer_content);//回复内容
        TextView time = (TextView) convertView.findViewById(R.id.tv_item_answer_time);//时间
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_item_answer_replyuser);//头像
        AutoLinearLayout llReply = (AutoLinearLayout) convertView.findViewById(R.id.ll_item_answer_reply);

        replyUser.setText(list.get(position).getStudent_nickname());
        user.setText(list.get(position).getReply_name());
        content.setText(list.get(position).getContent());
        time.setText(list.get(position).getTime());
        MyApplication.imageLoader.displayImage(list.get(position).getStudent_avatar(), imageView, MyApplication.options);

        llReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CommonMethod.getUid(context).equals(list.get(position).getReply_id())){
                    CommonMethod.showAlerDialog("回复失败","不能回复自己",context);
                }else{
                    //回复
                    reply(position);
                }
            }
        });
        return convertView;
    }

    /**
     * 回复
     */
    private void reply(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //自定义的对话框
        View view  = View.inflate(context,R.layout.edittext_alertdialog,null);
        EditText reply = (EditText) view.findViewById(R.id.et_reply);
        Button cancle = (Button) view.findViewById(R.id.btn_reply_cancle);
        Button commit = (Button) view.findViewById(R.id.btn_reply_commit);
        final AlertDialog dialog = builder.create();
        dialog.setView(view);
        dialog.show();
        reply.addTextChangedListener(new TextChangeListener(){
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                //获取用户输入的内容
                replyContent = s.toString();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
                dialog.dismiss();
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(replyContent)) {
                    //提交
                    commit(Urls.COURSE_DISCUSS_REPLY,position);
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 回复
     */
    private void commit(String url, final int position){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                JSONObject j2 = j1.getJSONObject("data");
                                AnswerDetails.CommentlistBean bean = new AnswerDetails.CommentlistBean();
                                bean.setStudent_nickname(j2.getString("student_nickname"));
                                bean.setStudent_avatar(j2.getString("student_avatar"));
                                bean.setTime(j2.getString("time"));
                                bean.setReply_name(j2.getString("reply_name"));
                                bean.setReply_id(j2.getString("reply_id"));
                                bean.setContent(j2.getString("content"));
                                list.add(bean);
                                notifyDataSetChanged();
                            }else{
                                CommonMethod.showAlerDialog("回复失败", j1.optString("msg"), context);
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
                params.put("id",id);
                params.put("uid",CommonMethod.getUid(context));
                params.put("type","1");//回复，类型固定为1
                params.put("content",replyContent);
                params.put("user_id",list.get(position).getReply_id());
                return params;
            }
        };
        mQueue.add(request);
    }
}
