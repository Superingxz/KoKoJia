package com.nuoxian.kokojia.adapter;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nuoxian.kokojia.R;
import com.nuoxian.kokojia.application.MyApplication;
import com.nuoxian.kokojia.enterty.Coupon;
import com.nuoxian.kokojia.http.Urls;
import com.nuoxian.kokojia.utils.CommonMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/20.
 */
public class GetCouponAdapter extends BaseAdapter {

    private Context context;
    private List<Coupon.DataBean> list;

    public GetCouponAdapter(Context context, List<Coupon.DataBean> list) {
        this.context = context;
        this.list = list;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_get_coupon, parent, false);
        }
        CommonMethod.setFontAwesome(convertView.findViewById(R.id.ll_item_get_coupon), context);

        TextView way = (TextView) convertView.findViewById(R.id.tv_coupon_way);// 优惠方式
        way.setText(list.get(position).getCoupon_sale());
        TextView price = (TextView) convertView.findViewById(R.id.tv_coupon_price);//优惠价格
        price.setText(list.get(position).getSale_price() + "元");
        TextView useRange = (TextView) convertView.findViewById(R.id.tv_coupon_use_range);// 使用范围
        if ("0".equals(list.get(position).getUser_range())) {
            //全部课程
            useRange.setText("适用于本机构（讲师）所有课程（价格不低于" + list.get(position).getCourse_price() + "元）");
        } else {
            //指定课程
            useRange.setText("适用于 " + list.get(position).getCourse_title());
        }
        TextView endTime = (TextView) convertView.findViewById(R.id.tv_coupon_end_time);//有效期
        endTime.setText("有效期至" + list.get(position).getEnd_date());
        final TextView receive = (TextView) convertView.findViewById(R.id.tv_coupon_receive);//领取优惠券
        receive.setText(list.get(position).getCoupon_name());
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("1".equals(list.get(position).getCoupon_status())){
                    CommonMethod.showLoadingDialog("正在领取...", context);
                    //领取
                    receiveCoupon(position,receive);
                }
            }
        });

        return convertView;
    }

    private void receiveCoupon(final int position, final TextView receive) {
        RequestQueue mQueue = MyApplication.getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, Urls.RECEIVE_COUPON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject j1 = new JSONObject(response);
                            if ("1".equals(j1.getString("status"))) {
                                //领取成功
                                CommonMethod.showAlerDialog("领取结果", "领取成功!", context);
                                receive.setText("已领取");
                            } else {
                                //领取失败
                                CommonMethod.showAlerDialog("领取结果", j1.getString("msg"), context);
                            }
                            CommonMethod.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.dismissLoadingDialog();
                CommonMethod.loadFailureToast(context);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", list.get(position).getId());
                params.put("uid", CommonMethod.getUid(context));
                return params;
            }
        };
        mQueue.add(request);
    }
}
