package com.jld.InformationRelease.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.response_bean.DeviceBeanSimple;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;

import java.util.ArrayList;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/26 14:47
 */
public class TerminalSelectDialog extends DialogFragment {

    public static final String TAG = "TerminalSelectDialog";
    private Context mContext;
    private ArrayList<String> mCheckMac;
    protected ArrayList<DeviceBeanSimple> mTerminals;
    //供选择的终端列表
    private String[] mSelect_item_arry;
    //默认选项
    private boolean[] mTerminalBoolean;
    private TerminalSelectListen mSelectListen;

    /**
     * @param context
     * @param checkMac 曾选设备 默认勾选
     */
    public TerminalSelectDialog(Context context, ArrayList<String> checkMac, TerminalSelectListen listen) {
        mContext = context;
        this.mCheckMac = checkMac;
        mSelectListen = listen;
        initData();
    }

    public void initData() {
        //获取所有设备列表
        SharedPreferences sp = mContext.getSharedPreferences(Constant.SHARE_KEY, Context.MODE_PRIVATE);
        String terminalJson = sp.getString(Constant.MY_TERMINAL, "");
        if (!TextUtils.isEmpty(terminalJson)) {
            mTerminals = new Gson().fromJson(terminalJson, new TypeToken<ArrayList<DeviceBeanSimple>>() {
            }.getType());
        }
        LogUtil.d(TAG, "mTerminals:" + mTerminals);
        /**
         * 设置内容区域为多选列表项
         * 默认选择都为false
         */
        mTerminalBoolean = new boolean[mTerminals.size()];
        for (int i = 0; i < mTerminalBoolean.length; i++) {
            mTerminalBoolean[i] = false;
        }
        ArrayList<String> select_item = new ArrayList<>();
        for (int i = 0; i < mTerminals.size(); i++) {

            if (mTerminals.get(i).getState().equals("1")) {//在线终端供选择
                select_item.add("ID: " + mTerminals.get(i).getId() + "  " + mTerminals.get(i).getName());
                if (mCheckMac != null && mCheckMac.size() > 0) {//再编辑  默认选中以前选中过的设备
                    for (String str : mCheckMac) {//默认选项
                        if (mTerminals.get(i).getMac().equals(str)) {
                            mTerminalBoolean[i] = true;
                        }
                    }
                }
            }
        }
        mSelect_item_arry = select_item.toArray(new String[0]);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getResources().getString(R.string.select_device_dialog_title));

        builder.setMultiChoiceItems(mSelect_item_arry, mTerminalBoolean, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                LogUtil.d(TAG, "mPushId1:" + mCheckMac);
                if (b)
                    mCheckMac.add(mTerminals.get(i).getMac());
                else {
                    boolean remove = mCheckMac.remove(mTerminals.get(i).getMac());
                    LogUtil.d(TAG, "remove:" + remove);
                }
                LogUtil.d(TAG, "mPushId2:" + mCheckMac);
            }
        });
        //监听下方button点击事件
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mCheckMac.size() > 0)
                    mSelectListen.onSure(mCheckMac);
                else
                    ToastUtil.showToast(mContext, getString(R.string.terminal_id_no_null), 3000);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        return dialog;
    }
    public interface TerminalSelectListen {
        void onSure(ArrayList<String> selectMac);
    }
}
