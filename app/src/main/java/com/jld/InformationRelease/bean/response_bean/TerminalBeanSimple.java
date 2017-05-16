package com.jld.InformationRelease.bean.response_bean;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/18 14:31
 */
public class TerminalBeanSimple {
    /**
     * 终端id
     */
    private String deviceid;
    /**
     * 终端状态
     */
    private String state;
    /**
     * 所在组名
     */
    private String group_name;
    /**
     * 所在组ID
     */
    private String group_id;
    /***
     * 设备Mac地址
     */
    private String device_mac;
    /**
     * 是否被选中
     */
    private Boolean isCheck = false;

    public TerminalBeanSimple(String id, String start, String group_name, String group_id, String device_mac) {
        this.deviceid = id;
        this.state = start;
        this.group_name = group_name;
        this.group_id = group_id;
        this.device_mac = device_mac;
    }

    public String getDevice_mac() {
        return device_mac;
    }

    public void setDevice_mac(String device_mac) {
        this.device_mac = device_mac;
    }

    public String getId() {
        return deviceid;
    }

    public void setId(String id) {
        this.deviceid = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    @Override
    public String toString() {
        return "TerminalBeanSimple{" +
                "id='" + deviceid + '\'' +
                ", state='" + state + '\'' +
                ", group_name='" + group_name + '\'' +
                ", group_id='" + group_id + '\'' +
                ", device_mac='" + device_mac + '\'' +
                ", isCheck=" + isCheck +
                '}';
    }

    public Boolean getCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }
}
