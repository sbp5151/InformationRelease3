package com.jld.InformationRelease.bean.response_bean;

import com.jld.InformationRelease.base.BaseResponse;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/18 15:31
 */
public class GetModelResponse extends BaseResponse {

    private ArrayList<Model> models = new ArrayList();

    public ArrayList<Model> getModels() {
        return models;
    }

    public void setModels(ArrayList<Model> models) {
        this.models = models;
    }

    @Override
    public String toString() {
        return "GetModelResponse{" +
                "models=" + models +
                '}';
    }

    public class Model {
        /**
         * 模板ID
         */
        private String modelid;
        /**
         * 模板简介
         */
        private String intro;
        /**
         * 样式图
         */
        private String modelimg;

        public String getModelid() {
            return modelid;
        }

        public void setModelid(String modelid) {
            this.modelid = modelid;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getModelimg() {
            return modelimg;
        }

        public void setModelimg(String modelimg) {
            this.modelimg = modelimg;
        }

        @Override
        public String toString() {
            return "GetModelResponse{" +
                    "modelid='" + modelid + '\'' +
                    ", intro='" + intro + '\'' +
                    ", modelimg='" + modelimg + '\'' +
                    '}';
        }
    }

}
