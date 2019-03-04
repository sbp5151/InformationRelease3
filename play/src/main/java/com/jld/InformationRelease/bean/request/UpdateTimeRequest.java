package com.jld.InformationRelease.bean.request;

/**
 * Created by boping on 2017/8/12.
 */

public class UpdateTimeRequest {

    private String id;
    private String time;
    private String sign;

    public UpdateTimeRequest(String id, String time, String sign) {
        this.id = id;
        this.time = time;
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "UpdateTimeRequest{" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
