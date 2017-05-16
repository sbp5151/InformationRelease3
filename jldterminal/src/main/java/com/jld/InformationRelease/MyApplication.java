package com.jld.InformationRelease;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.MacUtil;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 项目名称：InformationRelease2
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/21 11:07
 */
public class MyApplication extends Application {

    public static Boolean JPush_Alias_Succeed = false;
    private static final String TAG = "MyApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        String mac = MacUtil.getMac();
        Log.d(TAG, "mac:"+mac);
        JPushInterface.init(this);
        final SharedPreferences.Editor edit = getSharedPreferences(Constant.share_key, MODE_PRIVATE).edit();
        JPushInterface.setAlias(this, mac, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Toast.makeText(MyApplication.this,s,Toast.LENGTH_SHORT).show();
                Log.d("JPush", "i:" + i);
                Log.d("JPush", "s:" + s);
                if (0 == i) {
                    Log.d("JPush", "设置别名成功！");
                    edit.putInt(Constant.JPush_alias_set,i).apply();
                    JPush_Alias_Succeed = true;
                }else
                    edit.putInt(Constant.JPush_alias_set,i).apply();
            }
        });
    }
}
