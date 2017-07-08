package com.jld.InformationRelease.bean.response_bean;

import com.jld.InformationRelease.base.BaseResponse;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/4 10:08
 */
public class ProgramLoadStateResponse extends BaseResponse {

    private ArrayList<String> loadDeviceMac;

    public ArrayList<String> getLoadDeviceMac() {
        return loadDeviceMac;
    }

    public void setLoadDeviceMac(ArrayList<String> loadDeviceMac) {
        this.loadDeviceMac = loadDeviceMac;
    }

    @Override
    public String toString() {
        return "ProgramLoadStateResponse{" +
                "loadDeviceMac=" + loadDeviceMac +
                '}';
    }
}
