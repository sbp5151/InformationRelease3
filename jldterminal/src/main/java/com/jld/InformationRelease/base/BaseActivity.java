package com.jld.InformationRelease.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


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
    }

    public void toActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
    public void toActivity(Class activity,String putName,String putData){
        Intent intent = new Intent(this, activity);
        intent.putExtra(putName,putData);
        startActivity(intent);
    }
}
