package com.jld.InformationRelease.bean.response_bean;

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

    private ArrayList<String> fileUrls;

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
                '}';
    }
}
