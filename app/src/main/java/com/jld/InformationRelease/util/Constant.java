package com.jld.InformationRelease.util;

import android.os.Environment;

import java.io.File;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/12 20:03
 */
public class Constant {
    /**
     * 1 加密字符串
     */
//    public static final String S_KEY = "jldInformationRelease66666@%^&66666";
    public static final String S_KEY = "tucson88888888%#!88888888tucson";

    /**
     * 2 share_key
     */
    public static final String SHARE_KEY = "jld_share_key";


    //奶茶模板
    public static final String NAICHA_MODEL_1 = "1";
    //幻灯片模板
    public static final String IMAGE_MODEL = "2";
    //视频广告模板
    public static final String VIDEO_MODEL = "3";
    /**
     * 保存终端设备
     */
    public static final String MY_TERMINAL = "my_terminal";

    /**
     * 普通节目类型
     */
    public static final String PROGRAM_TYPE_COMMON = "1";
    /**
     * 每日任务节目类型
     */
    public static final String PROGRAM_TYPE_DAY = "2";
    /**
     * 紧急插播
     */
    public static final String PROGRAM_TYPE_URGENCY = "3";
    /**
     * 关于晶凌达信息发布系统 网页链接
     */
    public static final String ABOUT_INFORMATION = "http://www.baidu.com/";
    /**
     * 用户协议 网页链接 中文
     */
    public static final String USER_AGREEMENT_CN = "file:///android_asset/userProtocol_cn.html";
    /**
     * 用户协议 网页链接 英文
     */
    public static final String USER_AGREEMENT_EN = "file:///android_asset/userProtocol_en.html";

    /**
     * 图片缓存区
     */
    public static final String IMAGE_CHACE = Environment.getExternalStorageDirectory() + File.separator + "miniPhoto";
}
