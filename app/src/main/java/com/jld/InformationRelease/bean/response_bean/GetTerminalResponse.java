package com.jld.InformationRelease.bean.response_bean;

import com.jld.InformationRelease.base.BaseResponse;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 14:35
 */
public class GetTerminalResponse extends BaseResponse {

    public ArrayList<TerminalBeanSimple> item;

    public ArrayList<TerminalBeanSimple> getItems() {
        return item;
    }

    public void setItems(ArrayList<TerminalBeanSimple> items) {
        this.item = items;
    }

}
