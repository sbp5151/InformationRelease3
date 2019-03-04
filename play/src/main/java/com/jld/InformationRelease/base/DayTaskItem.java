package com.jld.InformationRelease.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/18 9:37
 */
public class DayTaskItem implements Parcelable {

    private String type;
    private String programName;
    private String stateTime;
    private String stopTime;
    private String programLocalId;

    public DayTaskItem() {
    }

    protected DayTaskItem(Parcel in) {
        programName = in.readString();
        stateTime = in.readString();
        stopTime = in.readString();
        programLocalId = in.readString();
        type = in.readString();
    }

    public static final Creator<DayTaskItem> CREATOR = new Creator<DayTaskItem>() {
        @Override
        public DayTaskItem createFromParcel(Parcel in) {
            return new DayTaskItem(in);
        }

        @Override
        public DayTaskItem[] newArray(int size) {
            return new DayTaskItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(programName);
        parcel.writeString(stateTime);
        parcel.writeString(stopTime);
        parcel.writeString(programLocalId);
        parcel.writeString(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProgramLocalId() {
        return programLocalId;
    }

    public void setProgramLocalId(String programLocalId) {
        this.programLocalId = programLocalId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getStateTime() {
        return stateTime;
    }

    public void setStateTime(String stateTime) {
        this.stateTime = stateTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    @Override
    public String toString() {
        return "DayTaskItem{" +
                "type='" + type + '\'' +
                ", programName='" + programName + '\'' +
                ", stateTime='" + stateTime + '\'' +
                ", stopTime='" + stopTime + '\'' +
                ", programLocalId='" + programLocalId + '\'' +
                '}';
    }
}
