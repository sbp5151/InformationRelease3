package com.jld.InformationRelease.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jld.InformationRelease.base.BaseProgram;
import com.jld.InformationRelease.util.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

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
     * 封面
     */
    private String cover = "";

    @Override
    public int describeContents() {
        return 0;
    }

    public ProgramBean() {
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStringList(images);
        parcel.writeStringList(videos);
        parcel.writeString(modelId);
        parcel.writeString(cover);
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

        parcel.writeTypedList(texts);
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

    protected ProgramBean(Parcel in) {
        images = in.createStringArrayList();
        videos = in.createStringArrayList();
        modelId = in.readString();
        cover = in.readString();
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

        in.readTypedList(texts, Commodity.CREATOR);
    }

    public static class Commodity implements Parcelable {
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

        protected Commodity(Parcel in) {
            name = in.readString();
            price = in.readString();
        }

        public static final Creator<Commodity> CREATOR = new Creator<Commodity>() {
            @Override
            public Commodity createFromParcel(Parcel in) {
                return new Commodity(in);
            }

            @Override
            public Commodity[] newArray(int size) {
                return new Commodity[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(name);
            parcel.writeString(price);
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }


    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
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
                "images=" + images +
                ", texts=" + texts +
                ", videos=" + videos +
                ", modelId='" + modelId + '\'' +
                ", cover='" + cover + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
