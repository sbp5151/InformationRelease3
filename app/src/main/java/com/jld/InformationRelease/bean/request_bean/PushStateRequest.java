package com.jld.InformationRelease.bean.request_bean;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/17 9:30
 */
public class PushStateRequest {

    private String sign;
    private String programId;

    public PushStateRequest(String sign, String programId) {
        this.sign = sign;
        this.programId = programId;
    }

    public PushStateRequest() {
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    @Override
    public String toString() {
        return "PushStateRequest{" +
                "sign='" + sign + '\'' +
                ", programId='" + programId + '\'' +
                '}';
    }
}
