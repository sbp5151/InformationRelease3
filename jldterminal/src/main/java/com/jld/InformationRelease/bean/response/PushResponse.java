package com.jld.InformationRelease.bean.response;

import com.jld.InformationRelease.base.BaseResponse;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/13 11:35
 */
public class PushResponse extends BaseResponse {

    /**
     * 音量大小
     */
    private String volume;
    /**
     * 开机时间
     */
    private String ontime;
    /**
     * 关机时间
     */
    private String offtime;
    /**
     * 节目ID
     */
    private String programID;

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getOntime() {
        return ontime;
    }

    public void setOntime(String ontime) {
        this.ontime = ontime;
    }

    public String getOfftime() {
        return offtime;
    }

    public void setOfftime(String offtime) {
        this.offtime = offtime;
    }

    public String getProgramID() {
        return programID;
    }

    public void setProgramID(String programID) {
        this.programID = programID;
    }

    @Override
    public String toString() {
        return "PushResponse{" +
                "volume='" + volume + '\'' +
                ", ontime='" + ontime + '\'' +
                ", offtime='" + offtime + '\'' +
                ", programID='" + programID + '\'' +
                '}';
    }
}
