package com.jld.InformationRelease.bean.request_bean;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/24 10:31
 * <p>
 * 定时开关机请求参数
 */
public class TimeShowdownRequestBean {

    private String mobile;
    private ArrayList<String> macs;
    private String offTime;
    private String onTime;
    /**
     * key+mobile
     */
    private String sign;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public ArrayList<String> getMacs() {
        return macs;
    }

    public void setMacs(ArrayList<String> macs) {
        this.macs = macs;
    }

    public String getOffTime() {
        return offTime;
    }

    public void setOffTime(String offTime) {
        this.offTime = offTime;
    }

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "TimeShowdownRequestBean{" +
                "mobile='" + mobile + '\'' +
                ", macs=" + macs +
                ", offTime='" + offTime + '\'' +
                ", onTime='" + onTime + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
