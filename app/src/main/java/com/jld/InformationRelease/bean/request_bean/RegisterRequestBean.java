package com.jld.InformationRelease.bean.request_bean;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/12 19:10
 */
public class RegisterRequestBean {

    /** 手机号码 */
    private String mobile;
    /** 密码  md5( ts_key +原始密码)*/
    private String passwd;
    /** 真实姓名 选填 */
    private String realname;
    /** 昵称 选填*/
    private String nick;
    /** 用户头像 选填 */
    private String img;
    /** 国家区号 */
    private String code;
    /** 加密字符串 md5($ts_skey + mobile + passwd)*/
    private String sign;

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "RegisterRequestBean{" +
                "mobile='" + mobile + '\'' +
                ", passwd='" + passwd + '\'' +
                ", realname='" + realname + '\'' +
                ", nick='" + nick + '\'' +
                ", img='" + img + '\'' +
                ", code='" + code + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
