package com.jld.InformationRelease.base;


import java.util.ArrayList;
/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/7 11:26
 */
public class BaseProgram {
    /**
     * 需要推送的终端mac地址
     */
    protected ArrayList<String> deviceMacs = new ArrayList<>();
    /**
     * 已加载节目成功的设备mac
     */
    protected ArrayList<String> loadDeviceMacs = new ArrayList<>();

    /**
     * 设备是否已全部经加载完成
     * 1为加载完成0未完成
     */
    protected String isLoadSucceed = "0";
    /**
     * 用户ID
     */
    protected String userid;
    /**
     * 节目ID 用于获取节目
     */
    protected String programId;

    /**
     * 加密字符串 md5(key+mobile)
     */
    protected String sign;

    /**
     * 创建时间
     */
    protected String time = "";

    /**
     * 数据库字段ID
     */
    protected int table_id = -1;

    /**
     * 用户输入的标签
     */
    protected String tab = "";

    /**
     * 状态：0为未上传，1为已上传，-1为上传失败
     */
    protected String upload_state;

    /**
     * 模板缩略图
     */
    protected String model_img = "";
    /**
     * 是否未选中状态
     */
    protected boolean isCheck;

    /**
     * 节目类型 1为普通节目，2位每日任务
     */
    protected String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public BaseProgram() {
    }
    public ArrayList<String> getDeviceMacs() {
        return deviceMacs;
    }

    public void setDeviceMacs(ArrayList<String> deviceMacs) {
        this.deviceMacs = deviceMacs;
    }

    public ArrayList<String> getLoadDeviceMacs() {
        return loadDeviceMacs;
    }

    public void setLoadDeviceMacs(ArrayList<String> loadDeviceMacs) {
        this.loadDeviceMacs = loadDeviceMacs;
    }

    public String getIsLoadSucceed() {
        return isLoadSucceed;
    }

    public void setIsLoadSucceed(String isLoadSucceed) {
        this.isLoadSucceed = isLoadSucceed;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getUpload_state() {
        return upload_state;
    }

    public void setUpload_state(String upload_state) {
        this.upload_state = upload_state;
    }

    public String getModel_img() {
        return model_img;
    }

    public void setModel_img(String model_img) {
        this.model_img = model_img;
    }

    @Override
    public String toString() {
        return "BaseProgram{" +
                "deviceMacs=" + deviceMacs +
                ", loadDeviceMacs=" + loadDeviceMacs +
                ", isLoadSucceed='" + isLoadSucceed + '\'' +
                ", userid='" + userid + '\'' +
                ", programId='" + programId + '\'' +
                ", sign='" + sign + '\'' +
                ", time='" + time + '\'' +
                ", table_id=" + table_id +
                ", tab='" + tab + '\'' +
                ", upload_state='" + upload_state + '\'' +
                ", model_img='" + model_img + '\'' +
                ", isCheck=" + isCheck +
                ", type='" + type + '\'' +
                '}';
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeStringList(deviceMacs);
//        parcel.writeStringList(loadDeviceMacs);
//        parcel.writeString(isLoadSucceed);
//        parcel.writeString(userid);
//        parcel.writeString(programId);
//        parcel.writeString(sign);
//        parcel.writeString(time);
//        parcel.writeInt(table_id);
//        parcel.writeString(tab);
//        parcel.writeString(upload_state);
//        parcel.writeString(model_img);    }
//    protected BaseProgram(Parcel in) {
//        deviceMacs = in.createStringArrayList();
//        loadDeviceMacs = in.createStringArrayList();
//        isLoadSucceed = in.readString();
//        userid = in.readString();
//        programId = in.readString();
//        sign = in.readString();
//        time = in.readString();
//        table_id = in.readInt();
//        tab = in.readString();
//        upload_state = in.readString();
//        model_img = in.readString();
//    }
//    public static final Creator<BaseProgram> CREATOR = new Creator<BaseProgram>() {
//        @Override
//        public BaseProgram createFromParcel(Parcel in) {
//            return new BaseProgram(in);
//        }
//
//        @Override
//        public BaseProgram[] newArray(int size) {
//            return new BaseProgram[size];
//        }
//    };
}
