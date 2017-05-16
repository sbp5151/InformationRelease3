package com.jld.InformationRelease.bean.request_bean;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/13 16:19
 */
public class VerifyCodeRequestBean {

    /** 电话*/
    private String mobile;
    /** 区号*/
    private String da;
    /** 加密字符串+手机号*/
    private String sign;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDa() {
        return da;
    }

    public void setDa(String da) {
        this.da = da;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "VerifyCodeRequestBean{" +
                "mobile='" + mobile + '\'' +
                ", da='" + da + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
