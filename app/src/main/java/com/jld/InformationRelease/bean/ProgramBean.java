package com.jld.InformationRelease.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/19 15:09
 */
public class ProgramBean implements Serializable {

    /**
     * 图片集
     */
    private ArrayList<String> images;
    /**
     * 商品名、价格
     */
    private ArrayList<Commodity> commoditys;
    /**
     * 视频集
     */
    private ArrayList<String> videos;
    /**
     * 需要推送的终端mac地址
     */
    private ArrayList<String> deviceMacs;
    /**
     * 用户ID
     */
    private String userID;
    /**
     * 模板ID
     */
    private String modelId;
    /**
     * 节目ID 用于获取节目
     */
    private String programid;
    /**
     * 加密字符串 md5(key+mobile)
     */
    private String sign;
    /**
     * 是否异常上传服务器 0位没有1位上传
     */
    private String isLoad = "0";

    /**
     * 创建时间
     */
    private String creation_time;

    /**
     * 数据库字段ID
     */
    private int table_id = -1;

    /**
     * 用户输入的标签
     */
    private String tab;

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    public static class Commodity implements Serializable {
        /**
         * 商品名
         */
        String name;
        /**
         * 价格
         */
        String price;

        public Commodity() {
        }

        public Commodity(String name, String price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "" +
                    "name='" + name + '\'' +
                    ", price='" + price + '\''
                    ;
        }
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<Commodity> getCommoditys() {
        return commoditys;
    }

    public void setCommoditys(ArrayList<Commodity> commoditys) {
        this.commoditys = commoditys;
    }

    public ArrayList<String> getDeviceMacs() {
        return deviceMacs;
    }

    public void setDeviceMacs(ArrayList<String> deviceMacs) {
        this.deviceMacs = deviceMacs;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String mobile) {
        this.userID = mobile;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getProgramid() {
        return programid;
    }

    public void setProgramid(String programid) {
        this.programid = programid;
    }

    public String getIsLoad() {
        return isLoad;
    }

    public void setIsLoad(String isLoad) {
        this.isLoad = isLoad;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    @Override
    public String toString() {
        return "ProgramBean{" +
                "images=" + images +
                ", commoditys=" + commoditys +
                ", videos=" + videos +
                ", deviceMacs=" + deviceMacs +
                ", userID='" + userID + '\'' +
                ", modelId='" + modelId + '\'' +
                ", programid='" + programid + '\'' +
                ", sign='" + sign + '\'' +
                ", isLoad='" + isLoad + '\'' +
                ", creation_time='" + creation_time + '\'' +
                ", table_id=" + table_id +
                ", tab='" + tab + '\'' +
                '}';
    }
}
