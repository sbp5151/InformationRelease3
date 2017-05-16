package com.jld.InformationRelease.bean.response;

import com.jld.InformationRelease.base.BaseResponse;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/25 11:16
 */
public class FileSingleResponseBean extends BaseResponse {

    private String fileUlr;

    public String getFileUlr() {
        return fileUlr;
    }

    public void setFileUlr(String fileUlr) {
        this.fileUlr = fileUlr;
    }

    @Override
    public String toString() {
        return "FileSingleResponseBean{" +
                "fileUlr='" + fileUlr + '\'' +
                '}';
    }
}
