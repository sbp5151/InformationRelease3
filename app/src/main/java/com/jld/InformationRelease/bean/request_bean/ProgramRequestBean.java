package com.jld.InformationRelease.bean.request_bean;

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
public class ProgramRequestBean implements Serializable {

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
     * 加密字符串 md5(key+mobile)
     */
    private String sign;

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
            return "Commodity{" +
                    "name='" + name + '\'' +
                    ", price='" + price + '\'' +
                    '}';
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

    @Override
    public String toString() {
        return "ProgramRequestBean{" +
                "images=" + images +
                ", commoditys=" + commoditys +
                ", videos=" + videos +
                ", deviceMacs=" + deviceMacs +
                ", userID='" + userID + '\'' +
                ", modelId='" + modelId + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
