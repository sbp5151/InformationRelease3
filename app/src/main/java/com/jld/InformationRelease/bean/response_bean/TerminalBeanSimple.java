package com.jld.InformationRelease.bean.response_bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/18 14:31
 */
public class TerminalBeanSimple implements Parcelable {
    /**
     * 终端id
     */
    private String id;
    /**
     * 终端状态
     *  0：离线
     *  1：在线
     *  2：关机
     *  3：故障
     */
    private String state = "1";
    /**
     * 所在组名
     */
    private String group_name ="";
    /**
     * 所在组ID
     */
    private String group_id ="";
    /***
     * 设备Mac地址
     */
    private String mac;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 是否被选中
     */
    private Boolean isCheck = false;

    public TerminalBeanSimple(String id, String state, String group_name, String group_id, String mac, String name) {
        this.id = id;
        this.state = state;
        this.group_name = group_name;
        this.group_id = group_id;
        this.mac = mac;
        this.name = name;
    }

    public TerminalBeanSimple() {
    }

    protected TerminalBeanSimple(Parcel in) {
        id = in.readString();
        state = in.readString();
        group_name = in.readString();
        group_id = in.readString();
        mac = in.readString();
        name = in.readString();
    }

    public static final Creator<TerminalBeanSimple> CREATOR = new Creator<TerminalBeanSimple>() {
        @Override
        public TerminalBeanSimple createFromParcel(Parcel in) {
            return new TerminalBeanSimple(in);
        }

        @Override
        public TerminalBeanSimple[] newArray(int size) {
            return new TerminalBeanSimple[size];
        }
    };

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                "id='" + id + '\'' +
                ", upload_state='" + state + '\'' +
                ", group_name='" + group_name + '\'' +
                ", group_id='" + group_id + '\'' +
                ", mac='" + mac + '\'' +
                ", name='" + name + '\'' +
                ", isCheck=" + isCheck +
                '}';
    }

    public Boolean getCheck() {
        return isCheck;
    }
    public void setCheck(Boolean check) {
        isCheck = check;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(state);
        parcel.writeString(group_name);
        parcel.writeString(group_id);
        parcel.writeString(mac);
        parcel.writeString(name);
    }
}
