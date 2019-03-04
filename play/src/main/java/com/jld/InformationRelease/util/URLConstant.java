package com.jld.InformationRelease.util;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/11 16:36
 */
public class URLConstant {
//    public static final String BASE_HTTP_URL = "http://admsg.torsun.cn";
    public static final String BASE_HTTP_URL = "http://120.78.146.50:8081";

    public static final String LOAD_PROGRAM_URL = "/program/one";
    /**
     * 1、上传文件接口
     */
    public static final String UPLOAD_FILES = "/user/userimg";
    /**
     * 2、上传截屏接口
     */
    public static final String UPLOAD_SCREEN = "/user/userimg";
    /**
     * 在线心跳包接口
     */
    public static final String HEART_BEAT = "/user/userimg";

    /**
     * 节目加载成功反馈
     */
    public static final String PROGRAM_BACK = "/program/getpushdevicepro";

    /**
     * 检查设备是否被绑定
     */
    public static final String CHECK_BIND = "/device/checkbind";

    /**
     * 在线时间更新
     */
    public static final String UPDATE_TIME = "device/updatedevicetime";
    /**
     * 获取设备ID
     */
    public static final String GET_DEV_ID = "device/getidbymac";
}
