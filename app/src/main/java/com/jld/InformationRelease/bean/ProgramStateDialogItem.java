package com.jld.InformationRelease.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/4 19:45
 */
public class ProgramStateDialogItem implements Parcelable {

    private String id;
    private String name;
    private boolean isLoad;

    public ProgramStateDialogItem() {
    }

    protected ProgramStateDialogItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        isLoad = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeByte((byte) (isLoad ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProgramStateDialogItem> CREATOR = new Creator<ProgramStateDialogItem>() {
        @Override
        public ProgramStateDialogItem createFromParcel(Parcel in) {
            return new ProgramStateDialogItem(in);
        }

        @Override
        public ProgramStateDialogItem[] newArray(int size) {
            return new ProgramStateDialogItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    @Override
    public String toString() {
        return "ProgramStateDialogItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isLoad=" + isLoad +
                '}';
    }
}
