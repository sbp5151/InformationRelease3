package com.jld.InformationRelease.bean.request;

/**
 * Created by boping on 2017/8/12.
 */

public class GetDevIdRequest {

    private String devicemac;
    private String sign;

    public GetDevIdRequest(String devicemac, String sign) {
        this.devicemac = devicemac;
        this.sign = sign;
    }

    public String getDevicemac() {
        return devicemac;
    }

    public void setDevicemac(String devicemac) {
        this.devicemac = devicemac;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
