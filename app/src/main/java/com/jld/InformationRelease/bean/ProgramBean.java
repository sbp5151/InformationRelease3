package com.jld.InformationRelease.bean;

import com.jld.InformationRelease.util.LogUtil;

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
    private ArrayList<String> images = new ArrayList<>();
    /**
     * 商品名、价格
     */
    private ArrayList<Commodity> texts = new ArrayList<>();
    /**
     * 视频集
     */
    private ArrayList<String> videos = new ArrayList<>();
    /**
     * 需要推送的终端mac地址
     */
    private ArrayList<String> deviceMacs = new ArrayList<>();

    /**
     * 用户ID
     */
    private String userid;
    /**
     * 节目ID 用于获取节目
     */
    private String programId;

    /**
     * 加密字符串 md5(key+mobile)
     */
    private String sign;

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
    private String tab="";
    /**
     * 模板ID
     */
    private String modelId;

    /**
     * 状态：0为未上传，1为已上传，-1为上传失败
     */
    private String state;

    /**
     * 是否未选中状态
     */
    private boolean isCheck;

    /**
     * 封面
     */
    private String cover = "";

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

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
        @Override
        public boolean equals(Object obj) {
            Commodity com = (Commodity) obj;
            LogUtil.d("name:",name);
            LogUtil.d("price:",price);
            LogUtil.d("com.getName():",com.getName());
            LogUtil.d("com.getPrice():",com.getPrice());
            return name.equals(com.getName()) && price.equals(com.getPrice());
        }
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<Commodity> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<Commodity> texts) {
        this.texts = texts;
    }

    public ArrayList<String> getDeviceMacs() {
        return deviceMacs;
    }

    public void setDeviceMacs(ArrayList<String> deviceMacs) {
        this.deviceMacs = deviceMacs;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String mobile) {
        this.userid = mobile;
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

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    @Override
    public String toString() {
        return "ProgramBean{" +
                "images=" + images +
                ", texts=" + texts +
                ", videos=" + videos +
                ", deviceMacs=" + deviceMacs +
                ", userid='" + userid + '\'' +
                ", programId='" + programId + '\'' +
                ", sign='" + sign + '\'' +
                ", creation_time='" + creation_time + '\'' +
                ", table_id=" + table_id +
                ", tab='" + tab + '\'' +
                ", modelId='" + modelId + '\'' +
                ", state='" + state + '\'' +
                ", isCheck=" + isCheck +
                ", cover='" + cover + '\'' +
                '}';
    }
}
