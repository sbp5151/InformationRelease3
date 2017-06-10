package com.jld.InformationRelease.bean.request_bean;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/11 14:37
 */
public class UpdateTerminalRequest {

    private String userid;
    private String sign;//md5($ts_skey + userid)

    public String getUserId() {
        return userid;
    }

    public void setUserId(String userId) {
        this.userid = userId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "UpdateTerminalRequest{" +
                "userId='" + userid + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
