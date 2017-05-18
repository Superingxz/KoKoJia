package com.nuoxian.kokojia.enterty;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class RechargeRecord {

    /**
     * id : 3072
     * uniqid : 20160809133312114707207921865010554000000000000000
     * user_id : 1
     * money : 0.01
     * activities_money : 0
     * user_money : 1.21
     * create_time : 2016/08/09 13:33:32
     * status : 0
     * trade_sn : 2016080921001004280278106683
     * type : 1
     * pay_type : 1
     * payment : 3
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String money;
        private String create_time;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
    }
}
