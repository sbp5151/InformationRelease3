package com.jld.InformationRelease.bean.request_bean;

/**
 * Created by boping on 2017/8/9.
 */

/**
 * 意见反馈
 */
public class FeedBackRequest {

    private String mobile;
    private String type;
    private String info;
    private String sign;//md5($ts_skey + mobile)

    public FeedBackRequest() {
    }

    public FeedBackRequest(String mobile, String type, String info, String sign) {
        this.mobile = mobile;
        this.type = type;
        this.info = info;
        this.sign = sign;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "FeedBackRequest{" +
                "mobile='" + mobile + '\'' +
                ", type='" + type + '\'' +
                ", info='" + info + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
