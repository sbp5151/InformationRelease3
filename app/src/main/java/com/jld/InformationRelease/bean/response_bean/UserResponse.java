package com.jld.InformationRelease.bean.response_bean;

import com.jld.InformationRelease.base.BaseResponse;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 16:51
 */
public class UserResponse extends BaseResponse {

    private String userid;
    private String mobile;
    private String nick;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "userid='" + userid + '\'' +
                ", mobile='" + mobile + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }
}
