package com.jld.InformationRelease.bean.request_bean;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/22 10:07
 */
public class BindingRequest {
    //用户ID
    private String userid;
    //设备MAC地址
    private String devicemac;
    //设备名称
    private String devicename;
    //加密字符串md5($ts_skey + userId + devicemac)
    private String sign;

    public String getUserId() {
        return userid;
    }

    public void setUserId(String userId) {
        this.userid = userId;
    }

    public String getDevicemac() {
        return devicemac;
    }

    public void setDevicemac(String devicemac) {
        this.devicemac = devicemac;
    }

    public String getDevicename() {
        return devicename;
    }

    @Override
    public String toString() {
        return "BindingRequest{" +
                "userId='" + userid + '\'' +
                ", devicemac='" + devicemac + '\'' +
                ", devicename='" + devicename + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
