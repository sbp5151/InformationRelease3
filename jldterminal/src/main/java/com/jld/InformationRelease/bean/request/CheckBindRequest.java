package com.jld.InformationRelease.bean.request;

/**
 * Created by boping on 2017/8/10.
 */

public class CheckBindRequest {

    private String devicemac;
    private String sign;

    public CheckBindRequest(String devicemac, String sign) {
        this.devicemac = devicemac;
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDevicemac() {
        return devicemac;
    }

    public void setDevicemac(String devicemac) {
        this.devicemac = devicemac;
    }
}
