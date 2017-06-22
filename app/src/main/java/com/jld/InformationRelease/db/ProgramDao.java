package com.jld.InformationRelease.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.LogUtil;

import org.json.JSONException;

import java.util.ArrayList;

import static com.jld.InformationRelease.db.DataBaseHelper.TABLE_NAME;
import static com.jld.InformationRelease.db.DataBaseHelper.creation_time;
import static com.jld.InformationRelease.db.DataBaseHelper.imgs;
import static com.jld.InformationRelease.db.DataBaseHelper.macs;
import static com.jld.InformationRelease.db.DataBaseHelper.model_id;
import static com.jld.InformationRelease.db.DataBaseHelper.program_id;
import static com.jld.InformationRelease.db.DataBaseHelper.tab;
import static com.jld.InformationRelease.db.DataBaseHelper.table_id;
import static com.jld.InformationRelease.db.DataBaseHelper.texts;
import static com.jld.InformationRelease.db.DataBaseHelper.upload_state;
import static com.jld.InformationRelease.db.DataBaseHelper.user_id;
import static com.jld.InformationRelease.db.DataBaseHelper.videos;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/23 15:39
 */
public class ProgramDao {

    private static final String TAG = "ProgramDao";
    private final DataBaseHelper mDb;
    private static ProgramDao sDao;
    private final Gson mGson;

    private ProgramDao(Context context) {
        mDb = new DataBaseHelper(context);
        mGson = new Gson();
    }

    public static ProgramDao getInstance(Context context) {
        if (sDao == null)
            sDao = new ProgramDao(context);
        return sDao;
    }

    /**
     * 增加节目
     */
    public void addProgram(ProgramBean bean, String userID) throws Exception {
        LogUtil.d(TAG, "addProgram:" + bean);
        if (bean == null)
            throw new Exception("空指针异常");
        SQLiteDatabase wDb = mDb.getWritableDatabase();
        wDb.beginTransaction();
        ContentValues content = getContentValues(bean, userID);
        wDb.insertOrThrow(TABLE_NAME, null, content);
        wDb.setTransactionSuccessful();
        wDb.endTransaction();
    }

    /**
     * 根据用户ID获取节目
     *
     * @return
     */
    public ArrayList<ProgramBean> getAllProgram(String userID) throws JSONException {
        ArrayList<ProgramBean> data = new ArrayList<>();
        SQLiteDatabase rDb = mDb.getReadableDatabase();
        LogUtil.d(TAG, "mUserID:" + userID);
//        Cursor cursor = rDb.rawQuery("select * from " + TABLE_NAME + " where " + table_id + "=?", new String[]{"1"});
//        参数table:表名称
//        参数columns:列名称数组
//        参数selection:条件字句，相当于where
//        参数selectionArgs:条件字句，参数数组
//        参数groupBy:分组列
//        参数having:分组条件
//        参数orderBy:排序列
//        参数limit:分页查询限制
//        参数Cursor:返回值，相当于结果集ResultSet
//        Cursor是一个游标接口，提供了遍历查询结果的方法，如移动指针方法move()，获得列值方法getString()等.
//                Cursor游标常用方法
        Cursor cursor = rDb.rawQuery("select * from " + TABLE_NAME + " where " + user_id + "=?", new String[]{userID});
//        Cursor cursor = rDb.query(TABLE_NAME, null, user_id + "=?", new String[]{mUserID}, null, null, null);
        while (cursor.moveToNext()) {
            ProgramBean bean = new ProgramBean();
            String commoditys = cursor.getString(cursor.getColumnIndex(DataBaseHelper.texts));
            String model_id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.model_id));
            String tab = cursor.getString(cursor.getColumnIndex(DataBaseHelper.tab));
            String program_id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.program_id));
            String is_load = cursor.getString(cursor.getColumnIndex(DataBaseHelper.upload_state));
            String imgs = cursor.getString(cursor.getColumnIndex(DataBaseHelper.imgs));
            String video = cursor.getString(cursor.getColumnIndex(DataBaseHelper.videos));
            String mac = cursor.getString(cursor.getColumnIndex(DataBaseHelper.macs));
            int table_id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.table_id));
            String user_id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.user_id));
            String creation_time = cursor.getString(cursor.getColumnIndex(DataBaseHelper.creation_time));
            String cover = cursor.getString(cursor.getColumnIndex(DataBaseHelper.cover));
            //text
            if (!TextUtils.isEmpty(commoditys)) {
                ArrayList<ProgramBean.Commodity> commodities = new Gson().fromJson(commoditys, new TypeToken<ArrayList<ProgramBean.Commodity>>() {
                }.getType());
                bean.setTexts(commodities);
            }
            //videos
            if (!TextUtils.isEmpty(video)) {
                ArrayList<String> videos = new Gson().fromJson(video, new TypeToken<ArrayList<String>>() {
                }.getType());
                bean.setVideos(videos);
            }
            //imgs
            if (!TextUtils.isEmpty(imgs)) {
                ArrayList<String> arImg = new Gson().fromJson(imgs, new TypeToken<ArrayList<String>>() {
                }.getType());
                bean.setImages(arImg);
            }
            //cover
            bean.setCover(cover);
            //model_id
            bean.setModelId(model_id);
            //program_id
            bean.setProgramId(program_id);
            //upload_state
            bean.setState(is_load);
            //table_id
            bean.setTable_id(table_id);
            //creation_time
            bean.setCreation_time(creation_time);
            //user_id
            bean.setUserid(user_id);
            //tab
            bean.setTab(tab);
            //mac
            if (!TextUtils.isEmpty(mac)) {
                ArrayList<String> macs = new Gson().fromJson(mac, new TypeToken<ArrayList<String>>() {
                }.getType());
                bean.setDeviceMacs(macs);
            }
            data.add(bean);
        }
        return data;
    }

    /**
     * 根据table_id删除节目数据
     *
     * @param tableId
     * @param userId
     */
    public void deleteProgram(String tableId, String userId) {
        LogUtil.d(TAG, "deleteProgram:" + tableId + "\n\r" + userId);
        SQLiteDatabase wDb = mDb.getWritableDatabase();
        wDb.beginTransaction();
        try {
            wDb.execSQL("delete from " + TABLE_NAME + " where " + table_id + " = ?", new Object[]{tableId});
            wDb.setTransactionSuccessful();
            wDb.endTransaction();
        } catch (Exception e) {
            LogUtil.d(TAG, "Exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据本地ID更新节目
     */
    public void updateInProgram(ProgramBean bean, String userID) throws Exception {
        LogUtil.d(TAG, "updateInProgram:" + bean);
        if (bean == null || TextUtils.isEmpty(bean.getTable_id() + ""))
            throw new Exception("空指针异常");
        SQLiteDatabase wDb = mDb.getWritableDatabase();
        wDb.beginTransaction();
        ContentValues content = getContentValues(bean, userID);
        content.put(program_id, bean.getProgramId());
        content.put(upload_state, bean.getState());
        wDb.update(TABLE_NAME, content, table_id + "=?", new String[]{bean.getTable_id() + ""});
        wDb.setTransactionSuccessful();
        wDb.endTransaction();
    }


    /**
     * 更新发布状态
     *
     * @param tableId
     * @param state
     */
    public void updateState(String tableId, String state, String userID) {
        LogUtil.d(TAG, "tableId:" + tableId);
        LogUtil.d(TAG, "state:" + state);

        if (table_id == null)
            return;
        if (state == null)
            return;
        SQLiteDatabase wdb = mDb.getWritableDatabase();
        wdb.execSQL("update " + TABLE_NAME + " set " + upload_state + "=? " + "where " + table_id + "=?",
                new Object[]{state, tableId});
    }

    /**
     * 更新节目ID
     *
     * @param tableId
     * @param programId
     */
    public void updateProgramId(String tableId, String programId, String userID) {
        LogUtil.d(TAG, "updateProgramId:" + programId);
        LogUtil.d(TAG, "tableId:" + tableId);
        if (tableId == null)
            return;
        if (programId == null)
            return;
        SQLiteDatabase wdb = mDb.getWritableDatabase();
        wdb.execSQL("update " + TABLE_NAME + " set " + program_id + "=? " + "where " + table_id + "=?",
                new Object[]{programId, tableId});
    }

    public void updateProgramTab() {

    }

    /**
     * 根据数据库ID更新节目
     */
    public void updateInDataBaseId(ProgramBean bean, String userID) throws Exception {
        LogUtil.d(TAG, "updateInDataBaseId:" + bean);
        if (bean == null || bean.getTable_id() == -1)
            throw new Exception("空指针异常");
        SQLiteDatabase wDb = mDb.getWritableDatabase();
        wDb.beginTransaction();
        ContentValues content = getContentValues(bean, userID);
        int update = wDb.update(TABLE_NAME, content, table_id + "=?", new String[]{String.valueOf(bean.getTable_id())});
        wDb.setTransactionSuccessful();
        wDb.endTransaction();
    }

    public ContentValues getContentValues(ProgramBean bean, String userID) {
        ContentValues content = new ContentValues();
        content.put(user_id, userID);
        content.put(creation_time, bean.getCreation_time());
        content.put(model_id, bean.getModelId());
        content.put(program_id, bean.getProgramId());
        content.put(imgs, mGson.toJson(bean.getImages()));
        content.put(videos, mGson.toJson(bean.getVideos()));
        content.put(texts, mGson.toJson(bean.getTexts()));
        content.put(macs, mGson.toJson(bean.getDeviceMacs()));
        content.put(upload_state, bean.getState());
        content.put(tab, bean.getTab());
        content.put(DataBaseHelper.cover, bean.getCover());
        return content;
    }
}
