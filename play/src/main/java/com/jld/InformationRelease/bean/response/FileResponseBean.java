package com.jld.InformationRelease.bean.response;

import com.jld.InformationRelease.base.BaseResponse;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/25 11:16
 */
public class FileResponseBean extends BaseResponse {

    //多张图片url存储
    private ArrayList<String> fileUrls;
    //单张图片存储
    private String fileUrl;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public ArrayList<String> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(ArrayList<String> fileUrls) {
        this.fileUrls = fileUrls;
    }

    @Override
    public String toString() {
        return "FileResponseBean{" +
                "fileUrls=" + fileUrls +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }
}
