package com.jld.InformationRelease.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.DayTaskItem;
import com.jld.InformationRelease.bean.NamePriceBean;
import com.jld.InformationRelease.util.LogUtil;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/19 15:09
 */
public class ProgramResponseBean extends BaseResponse implements Parcelable {
    private Program item;

    public Program getItem() {
        return item;
    }

    public void setItem(Program item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "ProgramResponseBean{" +
                "item=" + item +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(item,i);
        LogUtil.d("ProgramResponseBean","writeToParcel:"+item);
    }

    protected ProgramResponseBean(Parcel in) {
        item = in.readParcelable(Program.class.getClassLoader());
        LogUtil.d("ProgramResponseBean","ProgramResponseBean:"+item);
    }
    public static final Creator<ProgramResponseBean> CREATOR = new Creator<ProgramResponseBean>() {
        @Override
        public ProgramResponseBean createFromParcel(Parcel in) {
            return new ProgramResponseBean(in);
        }

        @Override
        public ProgramResponseBean[] newArray(int size) {
            return new ProgramResponseBean[size];
        }
    };
    public static class Program implements Parcelable {
        /**
         * 图片集
         */
        private ArrayList<String> images;
        /**
         * 商品名、价格
         */
        private ArrayList<NamePriceBean> texts;
        /**
         * 视频集合
         */
        private ArrayList<String> videos;
        /**
         * 需要推送的终端mac地址
         */
        private ArrayList<String> deviceMacs;

        /**
         * 每日任务数据
         */
        private ArrayList<DayTaskItem> dayProgram;
        /**
         * 模板ID
         */
        private String modelId;
        /**
         * 背景封面
         */
        private String cover;

        /**
         * 节目类型
         */
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ArrayList<DayTaskItem> getDayProgram() {
            return dayProgram;
        }

        public void setDayProgram(ArrayList<DayTaskItem> dayProgram) {
            this.dayProgram = dayProgram;
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

        public ArrayList<NamePriceBean> getTexts() {
            return texts;
        }

        public void setTexts(ArrayList<NamePriceBean> texts) {
            this.texts = texts;
        }

        public ArrayList<String> getDeviceMacs() {
            return deviceMacs;
        }

        public void setDeviceMacs(ArrayList<String> deviceMacs) {
            this.deviceMacs = deviceMacs;
        }


        public String getModelId() {
            return modelId;
        }

        public void setModelId(String modelId) {
            this.modelId = modelId;
        }

        @Override
        public String toString() {
            return "Program{" +
                    "images=" + images +
                    ", texts=" + texts +
                    ", videos=" + videos +
                    ", deviceMacs=" + deviceMacs +
                    ", dayProgram=" + dayProgram +
                    ", modelId='" + modelId + '\'' +
                    ", cover='" + cover + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeStringList(images);
            parcel.writeTypedList(texts);
            parcel.writeStringList(videos);
            parcel.writeStringList(deviceMacs);
            parcel.writeTypedList(dayProgram);
            parcel.writeString(modelId);
            parcel.writeString(cover);
            parcel.writeString(type);
        }
        protected Program(Parcel in){
            images = in.createStringArrayList();
            in.readTypedList(texts,NamePriceBean.CREATOR);
            videos = in.createStringArrayList();
            deviceMacs = in.createStringArrayList();
            in.readTypedList(dayProgram,DayTaskItem.CREATOR);
            modelId = in.readString();
            cover = in.readString();
            type = in.readString();
        }

        public static final Creator<Program> CREATOR = new Creator<Program>() {
            @Override
            public Program createFromParcel(Parcel in) {
                return new Program(in);
            }

            @Override
            public Program[] newArray(int size) {
                return new Program[size];
            }
        };
        @Override
        public int describeContents() {
            return 0;
        }
    }
}
