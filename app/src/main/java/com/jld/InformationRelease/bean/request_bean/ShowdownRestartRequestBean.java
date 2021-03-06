package com.jld.InformationRelease.bean.request_bean;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/24 10:35
 * <p>
 * 关机重启请求参数
 */
public class ShowdownRestartRequestBean {

    private String mobile;
    private ArrayList<String> macs;
    /**
     * 0为关机
     * 1为重启
     */
    private String tag;
    /***
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "ShowdownRestartRequestBean{" +
                "mobile='" + mobile + '\'' +
                ", macs=" + macs +
                ", tag='" + tag + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
