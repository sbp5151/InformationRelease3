package com.jld.InformationRelease.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.view.MainActivity;
import com.rey.material.app.ThemeManager;
import com.rey.material.app.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by boping on 2017/8/11.
 */

public class SpotsProgramDialog extends DialogFragment {

    public static final String TAG = "SpotsProgramDialog";
    private final String[] mPlayType;
    private final ArrayList<String> mNames;
    private Context mContext;
    private String mSelectPlayType;
    private int mSelectProgramPosition;
    private String mStateTime;
    private String mStopTime;
    private final ArrayAdapter<String> mProgramAdapter;
    OnUrgencyProgramListen mOnUrgencyProgramListen;

    public SpotsProgramDialog(Context context, ArrayList<String> names, OnUrgencyProgramListen onUrgencyProgramListen) {
        mContext = context;
        mNames = names;
        mPlayType = context.getResources().getStringArray(R.array.program_play_type);
        mProgramAdapter = new ArrayAdapter<>(context, R.layout.spots_program_select, names);
        mOnUrgencyProgramListen = onUrgencyProgramListen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.urgency_program_dialog, null);
        //节目选择
        Spinner sp_select = (Spinner) view.findViewById(R.id.sp_program_select);
        sp_select.setAdapter(mProgramAdapter);
        sp_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectProgramPosition = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //播放次数、时长
        final EditText play_num = (EditText) view.findViewById(R.id.et_play_num);
        final LinearLayout ll_play_num = (LinearLayout) view.findViewById(R.id.ll_play_num);
        final TextView tv_play_num = (TextView) view.findViewById(R.id.tv_play_num);
        //开始、结束时间
        final View llStart = view.findViewById(R.id.ll_play_start_time);
        final View llStop = view.findViewById(R.id.ll_play_stop_time);
        final Button btn_start_time = (Button) view.findViewById(R.id.btn_program_start_time);
        final Button btn_stop_time = (Button) view.findViewById(R.id.btn_program_stop_time);
        btn_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
                TimePickerDialog.Builder start_build = new TimePickerDialog.Builder(isLightTheme ?
                        R.style.Material_App_Dialog_TimePicker_Light
                        : R.style.Material_App_Dialog_TimePicker, 24, 00) {
                    @Override
                    public void onPositiveActionClicked(com.rey.material.app.DialogFragment fragment) {
                        TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                        LogUtil.d(TAG, "dialog:" + dialog);
                        DateFormat timeInstance = SimpleDateFormat.getTimeInstance();
                        mStateTime = dialog.getFormattedTime(timeInstance);
                        LogUtil.d(TAG, "mStateTime:" + mStateTime);
                        super.onPositiveActionClicked(fragment);
                        //结束时间大于播放时间检测
                        if (TextUtils.isEmpty(mStopTime) || TimeUtil.timeCompare(mStopTime, mStateTime)) {
                            btn_start_time.setText(mStateTime);
                        } else {
                            ToastUtil.showToast(mContext, getString(R.string.play_time_error), 3000);
                        }
                    }
                };
                start_build.positiveAction(getResources().getString(R.string.sure))
                        .negativeAction(getResources().getString(R.string.cancel));
                com.rey.material.app.DialogFragment fragment = com.rey.material.app.DialogFragment.newInstance(start_build);
                MainActivity activity = (MainActivity) getActivity();
                fragment.show(activity.getSupportFragmentManager(), null);
            }
        });
        btn_stop_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLightTheme = ThemeManager.getInstance().getCurrentTheme() == 0;
                TimePickerDialog.Builder start_build = new TimePickerDialog.Builder(isLightTheme ?
                        R.style.Material_App_Dialog_TimePicker_Light
                        : R.style.Material_App_Dialog_TimePicker, 24, 00) {
                    @Override
                    public void onPositiveActionClicked(com.rey.material.app.DialogFragment fragment) {
                        TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                        DateFormat timeInstance = SimpleDateFormat.getTimeInstance();
                        mStopTime = dialog.getFormattedTime(timeInstance);
                        LogUtil.d(TAG, "stopTime:" + mStopTime);
                        super.onPositiveActionClicked(fragment);
                        //结束时间大于播放时间检测
                        if (mStopTime.equals("00:00:00") || TextUtils.isEmpty(mStateTime) || TimeUtil.timeCompare(mStopTime, mStateTime)) {
                            if ("00:00:00".equals(mStopTime))
                                mStopTime = "24:00:00";
                            btn_stop_time.setText(mStopTime);
                        } else {
                            ToastUtil.showToast(mContext, getString(R.string.play_time_error), 3000);
                        }
                    }
                };
                start_build.positiveAction(getResources().getString(R.string.sure))
                        .negativeAction(getResources().getString(R.string.cancel));
                com.rey.material.app.DialogFragment fragment = com.rey.material.app.DialogFragment.newInstance(start_build);
                MainActivity activity = (MainActivity) getActivity();
                fragment.show(activity.getSupportFragmentManager(), null);
            }
        });
        //播放类型
        Spinner sp_type = (Spinner) view.findViewById(R.id.sp_play_type);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0://播放时长
                        mSelectPlayType = "0";
                        tv_play_num.setText(getString(R.string.play_time_minute));
                        llStart.setVisibility(View.GONE);
                        llStop.setVisibility(View.GONE);
                        ll_play_num.setVisibility(View.VISIBLE);
                        break;
                    case 1://播放次数
                        mSelectPlayType = "1";
                        tv_play_num.setText(getString(R.string.play_num));
                        llStart.setVisibility(View.GONE);
                        llStop.setVisibility(View.GONE);
                        ll_play_num.setVisibility(View.VISIBLE);
                        break;
                    case 2://播放时间段
                        mSelectPlayType = "2";
                        llStart.setVisibility(View.VISIBLE);
                        llStop.setVisibility(View.VISIBLE);
                        ll_play_num.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //取消
        view.findViewById(R.id.btn_urgency_program_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //推送
        view.findViewById(R.id.btn_urgency_program_push).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mSelectPlayType) {
                    case "0":
                        if (!TextUtils.isEmpty(play_num.getText().toString())){
                            mOnUrgencyProgramListen.onPush(mSelectProgramPosition, mSelectPlayType, play_num.getText().toString(), "");
                            dialog.dismiss();
                        }
                        else
                            ToastUtil.showToast(mContext, getString(R.string.please_input_program_time), 3000);
                        break;
                    case "1":
                        if (!TextUtils.isEmpty(play_num.getText().toString())){
                            mOnUrgencyProgramListen.onPush(mSelectProgramPosition, mSelectPlayType, play_num.getText().toString(), "");
                            dialog.dismiss();
                        }
                        else
                            ToastUtil.showToast(mContext, getString(R.string.please_input_program_num), 3000);
                        break;
                    case "2":
                        if (TextUtils.isEmpty(mStateTime))
                            ToastUtil.showToast(mContext, getString(R.string.please_input_start_time), 3000);
                        else if (TextUtils.isEmpty(mStopTime))
                            ToastUtil.showToast(mContext, getString(R.string.please_input_stop_time), 3000);
                        else{
                            mOnUrgencyProgramListen.onPush(mSelectProgramPosition, mSelectPlayType, mStateTime, mStopTime);
                            dialog.dismiss();
                        }
                        break;
                }
            }
        });
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        return dialog;
    }

    public interface OnUrgencyProgramListen {
        void onPush(int selectProgram, String play_type, String num1, String num2);
    }

}
