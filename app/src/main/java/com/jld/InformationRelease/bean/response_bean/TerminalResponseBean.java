package com.jld.InformationRelease.bean.response_bean;

import com.jld.InformationRelease.base.BaseResponse;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/18 14:31
 */
public class TerminalResponseBean extends BaseResponse {

    /**终端ID*/
    private String id;
    /**终端名称*/
    private String name;
    /**终端状态*/
    private String start;
    /**所在组ID*/
    private String group_id;
    /**所在组名*/
    private String group_name;
    /**终端IP*/
    private String ip;
}
