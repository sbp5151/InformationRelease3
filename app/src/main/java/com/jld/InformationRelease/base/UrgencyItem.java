package com.jld.InformationRelease.base;

/**
 * Created by boping on 2017/8/11.
 * <p>
 * 紧急插播 item
 */

public class UrgencyItem {
    private String type;//播放类型 0为播放时长、1为播放次数、2为播放时间段
    private String num1;
    private String num2;
    private String serviceId;
    private String localId;

    public UrgencyItem() {
    }
    public UrgencyItem(String type, String num1, String num2, String serviceId, String loadId) {
        this.type = type;
        this.num1 = num1;
        this.num2 = num2;
        this.serviceId = serviceId;
        this.localId = loadId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNum1() {
        return num1;
    }

    public void setNum1(String num1) {
        this.num1 = num1;
    }

    public String getNum2() {
        return num2;
    }

    public void setNum2(String num2) {
        this.num2 = num2;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }
}
