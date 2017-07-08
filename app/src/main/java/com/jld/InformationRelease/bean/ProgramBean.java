package com.jld.InformationRelease.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jld.InformationRelease.base.BaseProgram;
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
public class ProgramBean extends BaseProgram implements Parcelable {
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
     * 模板ID
     */
    private String modelId;
    /**
     * 是否未选中状态
     */
    private boolean isCheck;
    /**
     * 封面
     */
    private String cover = "";
    public ProgramBean(){
        super();
    }
    protected ProgramBean(Parcel in) {
        super(in);
        images = in.createStringArrayList();
        videos = in.createStringArrayList();
        modelId = in.readString();
        isCheck = in.readByte() != 0;
        cover = in.readString();
    }

    public static final Creator<ProgramBean> CREATOR = new Creator<ProgramBean>() {
        @Override
        public ProgramBean createFromParcel(Parcel in) {
            return new ProgramBean(in);
        }

        @Override
        public ProgramBean[] newArray(int size) {
            return new ProgramBean[size];
        }
    };

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


    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(images);
        parcel.writeStringList(videos);
        parcel.writeString(modelId);
        parcel.writeByte((byte) (isCheck ? 1 : 0));
        parcel.writeString(cover);
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
            LogUtil.d("name:", name);
            LogUtil.d("price:", price);
            LogUtil.d("com.getName():", com.getName());
            LogUtil.d("com.getPrice():", com.getPrice());
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


    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }


    @Override
    public String toString() {
        return "ProgramBean{" +
                "images=" + images +
                ", texts=" + texts +
                ", videos=" + videos +
                ", creation_time='" + creation_time + '\'' +
                ", modelId='" + modelId + '\'' +
                ", isCheck=" + isCheck +
                ", cover='" + cover + '\'' +
                '}';
    }
}
