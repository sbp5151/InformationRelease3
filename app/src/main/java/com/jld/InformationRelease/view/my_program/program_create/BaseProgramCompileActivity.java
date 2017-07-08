package com.jld.InformationRelease.view.my_program.program_create;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.dialog.SetProgramTabDialog;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_program_compile);
        model_img = getIntent().getStringExtra("model_img");
        mProgramBean = (ProgramBean) getIntent().getSerializableExtra("program_data");
        if (mProgramBean == null) {
            mProgramBean = new ProgramBean();
            mProgramBean.setModelId(Constant.VIDEO_MODEL);
            mProgramBean.setModel_img(model_img);
            setTabDialog();
        } else {
            mIsAgainCompile = true;
            mCheckMac = mProgramBean.getDeviceMacs();
        }
        mSp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        mPush = (Button) findViewById(R.id.btn_hide_push);

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
        tabDialog.show(getFragmentManager(),"");
//        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
//        //view
//        View view = LayoutInflater.from(this).inflate(R.layout.set_name_dialog, null);
//        TextView title = (TextView) view.findViewById(R.id.dialog1_title);
//        ImageView close = (ImageView) view.findViewById(R.id.dialog1_close);
//        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);
//        final EditText content = (EditText) view.findViewById(R.id.dialog1_content);
//        view.findViewById(R.id.dialog1_confirm);
//        title.setText(getString(R.string.setTab_dialog_title));
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                mProgramBean.setTab(getString(R.string.default_program_tab));
//            }
//        });
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {//设置节目名称
//                if (TextUtils.isEmpty(content.getText().toString())) {
//                    ToastUtil.showToast(BaseProgramCompileActivity.this, getString(R.string.please_input_tab), 3000);
//                } else {
//                    mProgramBean.setTab(content.getText().toString());
//                    dialog.dismiss();
//                }
//            }
//        });
//        //dialog
//        dialog.setCancelable(false);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT));
//        Window window = dialog.getWindow();
//        window.setWindowAnimations(R.style.main_menu_animstyle);
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        dialog.show();
    }

    /**
     * 终端选择
     */
    public void terminalSelect() {
        String terminalJson = mSp.getString(Constant.MY_TERMINAL, "");
        LogUtil.d(TAG, "terminalJson:" + terminalJson);
        if (!TextUtils.isEmpty(terminalJson)) {
            mTerminals = new Gson().fromJson(terminalJson, new TypeToken<ArrayList<TerminalBeanSimple>>() {
            }.getType());
        }
        LogUtil.d(TAG, "mTerminals:" + mTerminals);

        if (mTerminals == null || mTerminals.size() <= 0) {
            ToastUtil.showToast(this, getString(R.string.terminal_null), 3000);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.select_device_dialog_title));
        /**
         * 设置内容区域为多选列表项
         */
        //默认选择都为false
        boolean[] terminalBoolean = new boolean[mTerminals.size()];
        for (int i = 0; i < terminalBoolean.length; i++) {
            terminalBoolean[i] = false;
        }
        ArrayList<String> select_item = new ArrayList<>();
        for (int i = 0; i < mTerminals.size(); i++) {
            if (mTerminals.get(i).getState().equals("1")) {//在线终端供选择
                select_item.add("ID: " + mTerminals.get(i).getId() + "  " + mTerminals.get(i).getName());
                if (mIsAgainCompile) {//再编辑  默认选中以前选中过的设备
                    for (String str : mCheckMac) {
                        if (mTerminals.get(i).getMac().equals(str)) {
                            terminalBoolean[i] = true;
                        }
                    }
                }
            }
        }
        LogUtil.d(TAG, "select_item:" + select_item);
        final String[] select_item_arry = select_item.toArray(new String[0]);
        LogUtil.d(TAG, "select_item_arry:" + select_item_arry);

        builder.setMultiChoiceItems(select_item_arry, terminalBoolean, new DialogInterface.OnMultiChoiceClickListener() {
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
                programPush();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCheckMac.clear();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
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
                if (mPopupWindowListener != null)
                    mPopupWindowListener.onProgramPush();
            }
        });
        contentView.findViewById(R.id.pp_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                if (mPopupWindowListener != null)
                    mPopupWindowListener.onPreview();
            }
        });
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
