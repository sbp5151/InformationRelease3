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

    public static final String BASE_HTTP_URL = "http://admsg.torsun.cn";

    /**
     * 1、注册接口
     */
    public static final String REGISTER_URL = "/user/reg";

    /**
     * 2、登录接口
     */
    public static final String LOGIN_URL = "/user/login";

    /**
     * 3、修改密码
     */
    public static final String CHANGE_PASSWORD = "user/uppwd";

    /**
     * 4、找回密码
     */
    public static final String RETRIEVE_PASSWORD = "user/findpwd";

    /**
     * 5、获取账号所有绑定的设备接口
     */
    public static final String GET_DEVICE_URL = "/device/all";

    /**
     * 6、获取指定设备信息接口
     */
    public static final String GET_DEVICE_INFO_URL = "";

    /**
     * 7、定时开关机接口
     */
    public static final String TIME_POWER_ON_OFF_URL = "";

    /**
     * 8、关机、重启接口
     */
    public static final String SHOWDOWN_RESTART_URL = "";

    /**
     * 10、音量调节接口
     */
    public static final String VOLUME_ADJUST_URL = "";

    /**
     * 11、推送节目接口
     */
    public static final String PUSH_PROGRAM_URL = "/program/getprogram";
    /**
     * 设备绑定接口
     */
    public static final String BIND_DEVICE = "/device/binding";
    /**
     * 设备解绑接口
     */
    public static final String UNBIND_DEVICE = "/device/unbind";

    /**
     * 12、验证码请求接口
     */
    public static final String GET_VERIFY_CODE = "/tsmsg/sop";
    /**
     * 12、验证码请求接口
     */
    public static final String SMS_GET = "/tsmsg/soppwd";

    /**
     * 13、多个文件上传接口
     */
    public static final String PUSH_FILES = "/user/userimg";
    /**
     * 14、单个文件上传接口
     */
    public static final String PUSH_FILE = "/user/userimg";

    /**
     * 15、获取所有节目模板
     */
    public static final String GET_MODEL = "/user/userimg";

    /**
     * 16、节目发布后设备加载状况
     */
    public static final String PROGRAM_LOAD_STATE = "/program/pushgetproids";

    /**
     * 用户名修改
     */
    public static final String CHAGNE_NICK = "/user/upnick";
    /**
     * 用户反馈接口
     */
    public static final String FEED_BACK = "/tsmsg/feedb";
}
