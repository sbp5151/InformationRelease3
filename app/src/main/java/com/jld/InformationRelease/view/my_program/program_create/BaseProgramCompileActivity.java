package com.jld.InformationRelease.view.my_program.program_create;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.dialog.SetProgramTabDialog;
import com.jld.InformationRelease.dialog.TerminalSelectDialog;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;

import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_program.MyProgramFragment.sIsProgramUpload;

public abstract class BaseProgramCompileActivity extends BaseActivity {
    protected ProgramBean mProgramBean;
    protected SharedPreferences mSp;
    private static final String TAG = "BaseProgramCompileActivity";
    protected ArrayList<TerminalBeanSimple> mTerminals;
    protected boolean mIsAgainCompile = false;
    protected ArrayList<String> mCheckMac = new ArrayList<>();
    protected ImageButton mIb_tool;
    protected String model_img = "";
    protected Button mPush;
    protected ArrayList<ProgramBean> mProgramDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_program_compile);
        mProgramBean = getIntent().getParcelableExtra("program_data");
        model_img = getIntent().getStringExtra("model_img");
        //所有节目数据
        mProgramDatas = getIntent().getParcelableArrayListExtra("program_datas");
        LogUtil.d(TAG, "model_img:" + model_img);
        LogUtil.d(TAG, "mProgramBean:" + mProgramBean);
        LogUtil.d(TAG, "mProgramDatas:" + mProgramDatas);

        if (mProgramBean == null) {//新建节目
            mProgramBean = new ProgramBean();
            if (mProgramDatas == null) {//普通节目
                mProgramBean.setModelId(Constant.VIDEO_MODEL);
                mProgramBean.setModel_img(model_img);
                mProgramBean.setType(Constant.PROGRAM_TYPE_COMMON);//普通节目类型
            } else {//每日任务
                mProgramBean.setType(Constant.PROGRAM_TYPE_DAY);//每日任务类型
            }
            setTabDialog();
        } else {//再次编辑
            mIsAgainCompile = true;
        }
        mSp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        mPush = (Button) findViewById(R.id.btn_hide_push);
        LogUtil.d(TAG, "mProgramBean:" + mProgramBean);
    }

    /**
     * 保存节目到数据库
     */
    public abstract void saveProgram();

    /**
     * 发布
     */
    public abstract void programPush();

    /**
     * 数据是否改变
     *
     * @return
     */
    public abstract boolean isDataChange();

    /**
     * 设置节目标签dialog
     */
    public void setTabDialog() {
        SetProgramTabDialog tabDialog = new SetProgramTabDialog(this, new SetProgramTabDialog.OnProgramTabListen() {
            @Override
            public void onSetTab(String tab) {
                mProgramBean.setTab(tab);
            }
        });
        tabDialog.show(getFragmentManager(), "");
    }

    /**
     * 终端选择
     */
    public void terminalSelect() {
        mCheckMac = mProgramBean.getDeviceMacs();
        TerminalSelectDialog selectDialog = new TerminalSelectDialog(this, mCheckMac, new TerminalSelectDialog.TerminalSelectListen() {
            @Override
            public void onSure() {
                programPush();
            }
        });
        selectDialog.show(getFragmentManager(), "select");
    }

    /**
     * 退出是否保存节目提示
     */
    public void backSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.hint))
                .setMessage(getResources().getString(R.string.whether_save_program))
                .setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveProgram();
                    }
                }).setNegativeButton(getResources().getString(R.string.no_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }

    /**
     * 推送、预览、保存
     */
    public void showPopupwindow() {
        final PopupWindow mPopupWindow = new PopupWindow(this);
        View contentView = getLayoutInflater().inflate(R.layout.program_popupwindow_layout, null);
        contentView.findViewById(R.id.pp_program_push).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                if (mPopupWindowListener != null) {
                    if (!sIsProgramUpload)
                        mPopupWindowListener.onProgramPush();
                    else
                        ToastUtil.showToast(BaseProgramCompileActivity.this, getString(R.string.program_upload_ing), 3000);
                }
            }
        });
        if (mProgramBean.getType().equals(Constant.PROGRAM_TYPE_DAY)) {//每日任务不让预览
            contentView.findViewById(R.id.pp_preview).setVisibility(View.GONE);
        } else {
            contentView.findViewById(R.id.pp_preview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPopupWindow.dismiss();
                    if (mPopupWindowListener != null)
                        mPopupWindowListener.onPreview();
                }
            });
        }
        contentView.findViewById(R.id.pp_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                saveProgram();
            }
        });
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(GeneralUtil.dip2px(this, 100));
        mPopupWindow.setOutsideTouchable(true);//触摸外部消失
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x000000000));//透明背景
        mPopupWindow.setAnimationStyle(R.style.push_popupwindow_style);//动画
        mPopupWindow.showAsDropDown(mIb_tool, GeneralUtil.dip2px(this, 21), GeneralUtil.dip2px(this, -21));
    }

    PopupWindowListener mPopupWindowListener;


    protected void setPopupWindowListener(PopupWindowListener listener) {
        mPopupWindowListener = listener;
    }

    protected interface PopupWindowListener {
        void onPreview();

        void onProgramPush();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         * 监听返回键保存数据
         */
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isDataChange()) {
                backSaveDialog();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
