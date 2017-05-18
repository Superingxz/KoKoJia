package com.nuoxian.kokojia.enterty;

/**
 * Created by Administrator on 2016/8/2.
 * 支付方式
 */
public class PayWay {
    private int payWay,paySelect;
    private boolean select;
    private String payName;

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public int getPayWay() {
        return payWay;
    }

    public void setPayWay(int payWay) {
        this.payWay = payWay;
    }

    public int getPaySelect() {
        return paySelect;
    }

    public void setPaySelect(int paySelect) {
        this.paySelect = paySelect;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
