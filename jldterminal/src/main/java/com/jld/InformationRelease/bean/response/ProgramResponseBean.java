package com.jld.InformationRelease.bean.response;

import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.bean.NamePriceBean;

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
public class ProgramResponseBean extends BaseResponse implements Serializable {


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

   public class Program{
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
        private ArrayList<NamePriceBean> videos;
        /**
         * 需要推送的终端mac地址
         */
        private ArrayList<String> deviceMacs;

        /**
         * 模板ID
         */
        private String modelId;
       /**
        * 背景封面
        */
        private String cover;

       public String getCover() {
           return cover;
       }

       public void setCover(String cover) {
           this.cover = cover;
       }

       public ArrayList<NamePriceBean> getVideos() {
            return videos;
        }

        public void setVideos(ArrayList<NamePriceBean> videos) {
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
                   ", modelId='" + modelId + '\'' +
                   ", cover='" + cover + '\'' +
                   '}';
       }
   }

}
