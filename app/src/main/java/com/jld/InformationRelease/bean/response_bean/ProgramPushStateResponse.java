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
public class ProgramPushStateResponse extends BaseResponse {

    private ArrayList<PushStateItem> item;

    public class PushStateItem{
        private String deviceid;
        private String time;

        public String getDeviceMacs() {
            return deviceid;
        }

        public void setDeviceMacs(String deviceMacs) {
            this.deviceid = deviceMacs;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "PushStateItem{" +
                    "deviceid='" + deviceid + '\'' +
                    ", time='" + time + '\'' +
                    '}';
        }
    }

    public ArrayList<PushStateItem> getItem() {
        return item;
    }

    public void setItem(ArrayList<PushStateItem> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "ProgramPushStateResponse{" +
                "item=" + item +
                '}';
    }
}
