package com.jld.InformationRelease.bean.request_bean;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/31 11:11
 */
public class RetrievePWRequestBean {


    private String mobile;
    private String newpasswd;
    private String newpasswd2;
    private String sign;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNewpasswd() {
        return newpasswd;
    }

    public void setNewpasswd(String newpasswd) {
        this.newpasswd = newpasswd;
    }

    public String getNewpasswd2() {
        return newpasswd2;
    }

    public void setNewpasswd2(String newpasswd2) {
        this.newpasswd2 = newpasswd2;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "RetrievePWRequestBean{" +
                "mobile='" + mobile + '\'' +
                ", newpasswd='" + newpasswd + '\'' +
                ", newpasswd2='" + newpasswd2 + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
