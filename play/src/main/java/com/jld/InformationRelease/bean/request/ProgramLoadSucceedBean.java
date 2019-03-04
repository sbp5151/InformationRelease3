package com.jld.InformationRelease.bean.request;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/15 10:56
 */
public class ProgramLoadSucceedBean{

    private String deviceId;
    private String programId;
    private String sign;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "ProgramLoadSucceedBean{" +
                "deviceId='" + deviceId + '\'' +
                ", programId='" + programId + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
