package com.jld.InformationRelease.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jld.InformationRelease.base.BaseProgram;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/6 15:04
 *
 * 每日任务列表
 */
public class DayTaskBean extends BaseProgram implements Parcelable{

    public DayTaskBean(){}

    /**
     * 任务节目列表集合
     */
    private ArrayList<DayTaskItem> program_item = new ArrayList<>();

    protected DayTaskBean(Parcel in) {
        deviceMacs = in.createStringArrayList();
        loadDeviceMacs = in.createStringArrayList();
        isLoadSucceed = in.readString();
        userid = in.readString();
        programId = in.readString();
        sign = in.readString();
        creation_time = in.readString();
        table_id = in.readInt();
        tab = in.readString();
        upload_state = in.readString();
        model_img = in.readString();
        type = in.readString();

        in.readTypedList(program_item,DayTaskItem.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(deviceMacs);
        parcel.writeStringList(loadDeviceMacs);
        parcel.writeString(isLoadSucceed);
        parcel.writeString(userid);
        parcel.writeString(programId);
        parcel.writeString(sign);
        parcel.writeString(creation_time);
        parcel.writeInt(table_id);
        parcel.writeString(tab);
        parcel.writeString(upload_state);
        parcel.writeString(model_img);
        parcel.writeString(type);

        parcel.writeTypedList(program_item);
    }
    public static final Creator<DayTaskBean> CREATOR = new Creator<DayTaskBean>() {
        @Override
        public DayTaskBean createFromParcel(Parcel in) {
            return new DayTaskBean(in);
        }

        @Override
        public DayTaskBean[] newArray(int size) {
            return new DayTaskBean[size];
        }
    };

    public ArrayList<DayTaskItem> getProgram_item() {
        return program_item;
    }

    public void setProgram_item(ArrayList<DayTaskItem> program_item) {
        this.program_item = program_item;
    }
    @Override
    public String toString() {
        return "DayTaskBean{" +
                "deviceMacs=" + deviceMacs +
                ", loadDeviceMacs=" + loadDeviceMacs +
                ", isLoadSucceed='" + isLoadSucceed + '\'' +
                ", userid='" + userid + '\'' +
                ", programId='" + programId + '\'' +
                ", sign='" + sign + '\'' +
                ", creation_time='" + creation_time + '\'' +
                ", table_id=" + table_id +
                ", tab='" + tab + '\'' +
                ", upload_state='" + upload_state + '\'' +
                ", model_img='" + model_img + '\'' +
                "program_item=" + program_item +
                ", type='" + type + '\'' +
                '}';
    }
    public static class DayTaskItem implements Parcelable{
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
            return "DayTaskBean{" +
                    "programName='" + programName + '\'' +
                    ", stateTime='" + stateTime + '\'' +
                    ", stopTime='" + stopTime + '\'' +
                    ", programLocalId='" + programLocalId + '\'' +
                    '}';
        }
    }
}
