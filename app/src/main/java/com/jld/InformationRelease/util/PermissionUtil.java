package com.jld.InformationRelease.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_program.program.ProgramCompileActivity.REQUEST_READ_EXTERNAL_STORAGE;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/6/16 14:16
 */
public class PermissionUtil {


    public static void getPermission(Activity context, String[] permissions, int requesTag, GetPermissionListen listen) {

        //获取没有请求过的权限
        ArrayList<String> notPermission = new ArrayList<>();
        for (String str : permissions) {
            if (ContextCompat.checkSelfPermission(context,
                    str) != PackageManager.PERMISSION_GRANTED) {
                notPermission.add(str);
            }
        }
        //申请权限
        if (notPermission.size() > 0) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            listen.getPermissionSuccess();
        }
    }

    /**
     * 获取权限监听
     */
    public interface GetPermissionListen {
        //成功
        void getPermissionSuccess();

        //失败
        void getPermissionFail();
    }

}
