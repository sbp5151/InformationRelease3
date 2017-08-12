package com.jld.InformationRelease.util;

import android.content.Context;

import java.util.Locale;

/**
 * 项目名称：Torsun
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator 单柏平 <br/>
 * @create-time ${date} ${time}
 */
public class LanguageUtil {
    public static boolean isZh(Context context) {
        if(context==null)
            return true;
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        LogUtil.d("LanguageUtil","language:"+language);
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
