package com.jld.InformationRelease.bean.response_bean;

import com.jld.InformationRelease.base.BaseResponse;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/13 16:19
 */
public class VerifyCodeResponseBean extends BaseResponse{

    /**
     * 验证码
     */
    private String code;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "VerifyCodeResponseBean{" +
                "result='" + result + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
