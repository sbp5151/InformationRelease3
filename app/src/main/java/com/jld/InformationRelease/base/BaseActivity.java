package com.jld.InformationRelease.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.LogUtil;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/4/11 17:23
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);//透明导航栏
            getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar));//透明状态栏
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        LogUtil.setLogTag(getClass().getName());
    }

    public void toActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void toActivity(Class activity, String putName, String putData) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(putName, putData);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void toActivity(Class activity, Parcelable parcelable, String serializableName) {
        Intent intent = new Intent(this, activity);
        intent.putExtra(serializableName, parcelable);
        startActivity(intent);

    }
}
