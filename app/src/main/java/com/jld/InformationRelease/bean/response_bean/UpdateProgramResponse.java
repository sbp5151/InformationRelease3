package com.jld.InformationRelease.bean.response_bean;

import com.jld.InformationRelease.base.BaseResponse;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/6/5 19:04
 */
public class UpdateProgramResponse extends BaseResponse {

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        super.toString();
        return "UpdateProgramResponse{" +
                "data='" + data + '\'' +
                '}';
    }
}
