package com.jld.InformationRelease.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.jld.InformationRelease.db.DataBaseHelper.TABLE_NAME;
import static com.jld.InformationRelease.db.DataBaseHelper.tab;
import static com.jld.InformationRelease.db.DataBaseHelper.texts;
import static com.jld.InformationRelease.db.DataBaseHelper.creation_time;
import static com.jld.InformationRelease.db.DataBaseHelper.imgs;
import static com.jld.InformationRelease.db.DataBaseHelper.upload_state;
import static com.jld.InformationRelease.db.DataBaseHelper.macs;
import static com.jld.InformationRelease.db.DataBaseHelper.model_id;
import static com.jld.InformationRelease.db.DataBaseHelper.program_id;
import static com.jld.InformationRelease.db.DataBaseHelper.table_id;
import static com.jld.InformationRelease.db.DataBaseHelper.user_id;

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
    private String mUserID;

    private ProgramDao(Context context, String userID) {
        mDb = new DataBaseHelper(context);
        mGson = new Gson();
        mUserID = userID;
    }

    public static ProgramDao getInstance(Context context, String userID) {
        if (sDao == null)
            sDao = new ProgramDao(context, userID);
        return sDao;
    }

    /**
     * 增加节目
     */
    public void addProgram(ProgramBean bean) throws Exception {
        LogUtil.d(TAG, "增加节目:" + bean);
        if (bean == null)
            throw new Exception("空指针异常");
        SQLiteDatabase wDb = mDb.getWritableDatabase();
        wDb.beginTransaction();
        ContentValues content = getContentValues(bean);
        wDb.insertOrThrow(TABLE_NAME, null, content);
        wDb.setTransactionSuccessful();
        wDb.endTransaction();
    }

    /**
     * 根据用户ID获取节目
     *
     * @return
     */
    public ArrayList<ProgramBean> getAllProgram() throws JSONException {
        ArrayList<ProgramBean> data = new ArrayList<>();
        SQLiteDatabase rDb = mDb.getReadableDatabase();
        LogUtil.d(TAG, "mUserID:" + mUserID);
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
        Cursor cursor = rDb.rawQuery("select * from " + TABLE_NAME + " where " + user_id + "=?", new String[]{mUserID});
//        Cursor cursor = rDb.query(TABLE_NAME, null, user_id + "=?", new String[]{mUserID}, null, null, null);
        while (cursor.moveToNext()) {
            ProgramBean bean = new ProgramBean();
            String commoditys = cursor.getString(cursor.getColumnIndex(DataBaseHelper.texts));
            String model_id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.model_id));
            String program_id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.program_id));
            String is_load = cursor.getString(cursor.getColumnIndex(DataBaseHelper.upload_state));
            String imgs = cursor.getString(cursor.getColumnIndex(DataBaseHelper.imgs));
            String mac = cursor.getString(cursor.getColumnIndex(DataBaseHelper.macs));
            int table_id = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.table_id));
            String user_id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.user_id));
            String creation_time = cursor.getString(cursor.getColumnIndex(DataBaseHelper.creation_time));
            //commodity
            JSONArray array = new JSONArray(commoditys);
            ArrayList<ProgramBean.Commodity> commodities = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                ProgramBean.Commodity commodity = new ProgramBean.Commodity();
                JSONObject o = array.getJSONObject(i);
                commodity.setName(o.getString("name"));
                commodity.setPrice(o.getString("price"));
                commodities.add(commodity);
            }
            bean.setTexts(commodities);
            //imgs
            ArrayList<String> arImg = new ArrayList<>();
            JSONArray imgarry = new JSONArray(imgs);
            for (int i = 0; i < imgarry.length(); i++) {
                String o = (String) imgarry.get(i);
                LogUtil.d(TAG, "o:" + o);
                arImg.add(o);
            }
            bean.setImages(arImg);
            //model_id
            bean.setModelId(model_id);
            //program_id
            bean.setProgramId(program_id);
            //upload_state
            bean.setState(is_load);
            //table_id
            LogUtil.d(TAG, "table_id:" + table_id);
            bean.setTable_id(table_id);
            //creation_time
            bean.setCreation_time(creation_time);
            //user_id
            bean.setUserid(user_id);
            //mac
            ArrayList<String> macs = new ArrayList<>();
            JSONArray macarry = new JSONArray(mac);
            for (int i = 0; i < macarry.length(); i++) {
                String o = (String) macarry.get(i);
                LogUtil.d(TAG, "o:" + o);
                macs.add(o);
            }
            bean.setDeviceMacs(macs);
            data.add(bean);
        }
        return data;
    }

    /**
     * 根据节目ID更新节目
     */
    public void updateInProgramId(ProgramBean bean) throws Exception {
        LogUtil.d(TAG, "bean.getProgramid():" + bean.getProgramId());
        if (bean == null || TextUtils.isEmpty(bean.getProgramId()))
            throw new Exception("空指针异常");
        SQLiteDatabase wDb = mDb.getWritableDatabase();
        wDb.beginTransaction();
        ContentValues content = getContentValues(bean);
        content.put(program_id, bean.getProgramId());
        content.put(upload_state, bean.getState());
        wDb.update(TABLE_NAME, content, program_id + "=?", new String[]{bean.getProgramId()});
        wDb.setTransactionSuccessful();
        wDb.endTransaction();
    }


    /**
     * 更新发布状态
     *
     * @param createTime
     * @param state
     */
    public void updateState(String createTime, String state) {
        if (createTime == null)
            return;
        if (state == null)
            return;
        SQLiteDatabase wdb = mDb.getWritableDatabase();
        wdb.execSQL("update " + TABLE_NAME + " set " + upload_state + "=? " + "where " + creation_time + "=?",
                new Object[]{state, createTime});
    }

    /**
     * 更新节目ID
     *
     * @param createTime
     * @param programId
     */
    public void updateProgramId(String createTime, String programId) {
        if (createTime == null)
            return;
        if (programId == null)
            return;
        SQLiteDatabase wdb = mDb.getWritableDatabase();
        wdb.execSQL("update " + TABLE_NAME + " set " + program_id + "=? " + "where " + creation_time + "=?",
                new Object[]{programId, createTime});
    }

    /**
     * 根据数据库ID更新节目
     */
    public void updateInDataBaseId(ProgramBean bean) throws Exception {
        if (bean == null || bean.getTable_id() == -1)
            throw new Exception("空指针异常");
        SQLiteDatabase wDb = mDb.getWritableDatabase();
        wDb.beginTransaction();
        ContentValues content = getContentValues(bean);
        int update = wDb.update(TABLE_NAME, content, table_id + "=?", new String[]{String.valueOf(bean.getTable_id())});
        LogUtil.d("update:" + update);
        wDb.setTransactionSuccessful();
        wDb.endTransaction();
    }


    public void jsonToArry(ArrayList<Objects> data, String json) throws JSONException {
    }

    public ContentValues getContentValues(ProgramBean bean) {
        LogUtil.d(TAG, "getContentValues:" + bean);
        ContentValues content = new ContentValues();
        content.put(user_id, mUserID);
        content.put(creation_time, bean.getCreation_time());
        content.put(model_id, bean.getModelId());
        content.put(program_id, bean.getProgramId());
        content.put(imgs, mGson.toJson(bean.getImages()));
        content.put(texts, mGson.toJson(bean.getTexts()));
        content.put(macs, mGson.toJson(bean.getDeviceMacs()));
        content.put(upload_state, mGson.toJson(bean.getState()));
        content.put(tab, mGson.toJson(bean.getTab()));
        return content;
    }
}
