package com.jld.InformationRelease.bean.request_bean;

/**
 * Created by boping on 2017/8/9.
 */

/**
 * 昵称修改
 */
public class ChangeNickRequest {

    private String userid;
    private String mobile;
    private String nike;
    private String sign;//md5($ts_skey + mobile + userid)

    public ChangeNickRequest() {
    }

    public ChangeNickRequest(String userid, String mobile, String nike, String sign) {
        this.userid = userid;
        this.mobile = mobile;
        this.nike = nike;
        this.sign = sign;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNike() {
        return nike;
    }

    public void setNike(String nike) {
        this.nike = nike;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "ChangeNickRequest{" +
                "userid='" + userid + '\'' +
                ", mobile='" + mobile + '\'' +
                ", nike='" + nike + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
