package com.jld.InformationRelease.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import static com.jld.InformationRelease.db.DataBaseHelper.DAY_TASK_TABLE_NAME;
import static com.jld.InformationRelease.db.DataBaseHelper.program_id;
import static com.jld.InformationRelease.db.DataBaseHelper.table_id;
import static com.jld.InformationRelease.db.DataBaseHelper.upload_state;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/5/23 15:39
 */
public class ProgramDao2 {

    private static final String TAG = "ProgramDao2";
    private DataBaseHelper mDataBaseHelper;
    private static ProgramDao2 sDao;
    private final Gson mGson;
    private Context mContext;

    private ProgramDao2(Context context) {
        mDataBaseHelper = new DataBaseHelper(context);
        mGson = new Gson();
        mContext = context;
    }

    public static ProgramDao2 getInstance(Context context) {
        if (sDao == null)
            sDao = new ProgramDao2(context);
        return sDao;
    }

//    public void addData(DayTaskBean bean) {
//
//        LogUtil.d(TAG, "addData:" + bean);
//        if (TextUtils.isEmpty(bean.getUserid())) {
//            ToastUtil.showToast(mContext, mContext.getString(R.string.please_login), 3000);
//            return;
//        }
//        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
//        db.beginTransaction();
//        ContentValues values = new ContentValues();
//        values.put(user_id, bean.getUserid());
//        values.put(time, bean.getTime());
//        values.put(program_id, bean.getProgramId());
//        values.put(macs, mGson.toJson(bean.getDeviceMacs()));
//        values.put(upload_state, bean.getUpload_state());
//        values.put(tab, bean.getTab());
//        values.put(is_load_succeed, bean.getIsLoadSucceed());
//        values.put(model_image, bean.getModel_img());
//        values.put(program_item, mGson.toJson(bean.getProgram_item()));
//        values.put(type, bean.getType());
//        db.insertOrThrow(DAY_TASK_TABLE_NAME, null, values);
//        db.setTransactionSuccessful();
//        db.endTransaction();
//    }
//
//    public ArrayList<DayTaskBean> queryDataAll(String userId) {
//        LogUtil.d(TAG, "mDataBaseHelper:" + mDataBaseHelper);
//        SQLiteDatabase db = mDataBaseHelper.getReadableDatabase();
//        Cursor query = db.query(DAY_TASK_TABLE_NAME, null, user_id + " = ?", new String[]{userId}, null, null, null);
//        ArrayList<DayTaskBean> datas = new ArrayList<>();
//        while (query.moveToNext()) {
//            DayTaskBean data = new DayTaskBean();
//            data.setUserid(query.getString(query.getColumnIndex(user_id)));
//            data.setTime(query.getString(query.getColumnIndex(time)));
//            data.setProgramId(query.getString(query.getColumnIndex(program_id)));
//            String rMacs = query.getString(query.getColumnIndex(macs));
//            if (!TextUtils.isEmpty(rMacs)) {
//                ArrayList<String> macs = new Gson().fromJson(rMacs, new TypeToken<ArrayList<String>>() {
//                }.getType());
//                data.setDeviceMacs(macs);
//            }
//            data.setUpload_state(query.getString(query.getColumnIndex(upload_state)));
//            data.setTab(query.getString(query.getColumnIndex(tab)));
//            data.setIsLoadSucceed(query.getString(query.getColumnIndex(is_load_succeed)));
//            data.setModel_img(query.getString(query.getColumnIndex(model_image)));
//            data.setType(query.getString(query.getColumnIndex(type)));
//            data.setTable_id(Integer.parseInt(query.getString(query.getColumnIndex(table_id))));
//            String rProgramItem = query.getString(query.getColumnIndex(program_item));
//            LogUtil.d(TAG, "rProgramItem:" + rProgramItem);
//            if (!TextUtils.isEmpty(rProgramItem)) {
//                ArrayList<DayTaskBean.DayTaskItem> item = new Gson().fromJson(rProgramItem, new TypeToken<ArrayList<DayTaskBean.DayTaskItem>>() {
//                }.getType());
//                LogUtil.d(TAG, "item:" + item);
//                data.setProgram_item(item);
//            }
//            datas.add(data);
//        }
//        query.close();
//        return datas;
//    }
//
//    public void deleteData(String tabId) {
//        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
//        db.beginTransaction();
//        db.delete(DAY_TASK_TABLE_NAME, table_id + " = ?", new String[]{tabId});
//        db.setTransactionSuccessful();
//        db.endTransaction();
//    }

    public void updateProgramId(String tabId, String programId, String uploadState) {
        SQLiteDatabase db = mDataBaseHelper.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(program_id, programId);
        values.put(upload_state, uploadState);
        db.update(DAY_TASK_TABLE_NAME, values, table_id + " = ?", new String[]{tabId});
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
