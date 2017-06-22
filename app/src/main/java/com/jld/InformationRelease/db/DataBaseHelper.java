package com.jld.InformationRelease.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/23 11:05
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jldInformation.db";// 数据库名称
    public static final String TABLE_NAME = "program";
    private static final int DATABASE_VERSION = 1;// 数据库当前版本，老版本为1
    //字段
    public static final String table_id = "id";
    public static final String creation_time = "creation_time";
    public static final String model_id = "model_id";
    public static final String program_id = "program_id";
    public static final String imgs = "imgs";
    public static final String texts = "texts";
    public static final String videos = "videos";
    public static final String upload_state = "upload_state";
    public static final String tab = "tab";
    public static final String user_id = "user_id";
    public static final String macs = "mac";
    public static final String cover = "cover";

    /**
     * 创建团成员数据库表
     */
    public static final String CREATE_MYMODEL_TABLE =
            "create table if not exists " + TABLE_NAME + "( "
                    + table_id + " Integer primary key autoincrement,"//id自增长
                    + user_id + " varchar(30),"//用户ID
                    + creation_time + " varchar(30),"//创建时间
                    + model_id + " varchar(30),"//模板ID
                    + program_id + " varchar(30) UNIQUE,"//节目ID 唯一
                    + imgs + " varchar(1000),"//图片集合
                    + videos + " varchar(1000),"//视频集合
                    + texts + " varchar(2000),"//“商品”集合
                    + macs + " varchar(1000),"//mac集合
                    + upload_state + " varchar(10) DEFAULT 0,"//是否已经上传服务器 默认为没有上传
                    + tab + " varchar(30),"//用户设置的标签
                    + cover + " varchar(50)"//封面路径
                    + ");";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //当你Android安装一个全新的应用，会从onCreate这个方法里创建。
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MYMODEL_TABLE);
    }

    //当你Android在旧版本上更新的时候会从onUpgrade方法里更新。
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
