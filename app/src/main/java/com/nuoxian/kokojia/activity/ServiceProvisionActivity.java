package com.nuoxian.kokojia.activity;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 服务条款
 */
public class ServiceProvisionActivity extends BaseActivity {

    private AutoLinearLayout llContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_service_provision);

        initView();
        //获取条款内容
        getData();
    }

    private void initView(){
        //设置标题
        setTitle("课课家服务条款");
        //返回
        setReturn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceProvisionActivity.this.finish();
            }
        });
        llContent = (AutoLinearLayout) findViewById(R.id.ll_provision_content);
    }

    /**
     * 获取条款内容
     */
    private void getData(){
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Urls.SERVICE_PROVISION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if("1".equals(j1.getString("status"))){
                                JSONArray j2 = j1.getJSONArray("data");
                                for (int i=0;i<j2.length();i++){
                                    JSONObject j3 = j2.getJSONObject(i);
                                    TextView textView = new TextView(ServiceProvisionActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    textView.setLayoutParams(params);
                                    textView.setText(j3.getString("name"));
                                    if(i==2||i==9||i==14||i==20||i==28||i==41||i==45||i==52){
                                        //设置字体加粗
                                        TextPaint tp = textView.getPaint();
                                        tp.setFakeBoldText(true);
                                    }
                                    llContent.addView(textView);
                                }
                            }else{
                                CommonMethod.toast(ServiceProvisionActivity.this,"没有数据");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.loadFailureToast(ServiceProvisionActivity.this);
            }
        });
        mQueue.add(request);
    }
}
