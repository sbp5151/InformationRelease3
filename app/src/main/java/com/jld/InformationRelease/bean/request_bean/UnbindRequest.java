package com.jld.InformationRelease.bean.request_bean;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/22 10:07
 */
public class UnbindRequest {
    //用户ID
    private String userId;
    //设备MAC地址
    private ArrayList<String> deviceMacs;
    //加密字符串md5($ts_skey + userId)
    private String sign;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getDeviceMacs() {
        return deviceMacs;
    }

    public void setDeviceMacs(ArrayList<String> deviceMacs) {
        this.deviceMacs = deviceMacs;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "UnbindRequest{" +
                "userId='" + userId + '\'' +
                ", deviceMacs=" + deviceMacs +
                ", sign='" + sign + '\'' +
                '}';
    }
}
