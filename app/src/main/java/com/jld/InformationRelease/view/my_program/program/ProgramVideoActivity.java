package com.jld.InformationRelease.view.my_program.program;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseRecyclerViewAdapterClick;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.bean.response_bean.TerminalBeanSimple;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.my_program.program.adapter.ProgramVideoAdapter;
import com.jld.InformationRelease.view.my_program.program.preview.ProgramVideoPreview;

import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

/**
 * 视频广告  节目编辑
 */
public class ProgramVideoActivity extends BaseProgramCompileActivity {

    private static final String TAG = "ProgramVideoActivity";
    private static final int GET_VIDEO_RESULT = 0x01;
    private RecyclerView mRecyclerView;
    private ArrayList<String> mVideoPath = new ArrayList<>();
    private ArrayList<String> mSaveVideoPath = new ArrayList<>();
    private ProgramVideoAdapter mAdapter;
    private View mAdd;
    ArrayList<TerminalBeanSimple> mTerminals;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

            }
        }
    };
    private int mAddPathPosition;
    private PopupWindow mPopupWindow;
    private ImageButton mIb_tool;
    private boolean mIsAgainCompile = false;
    private ArrayList<String> mCheckMac = new ArrayList<>();
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_video);
        mProgramBean = (ProgramBean) getIntent().getSerializableExtra("program_data");
        LogUtil.d(TAG, "mProgramBean:" + mProgramBean);
        if (mProgramBean == null) {
            mProgramBean = new ProgramBean();
            mProgramBean.setModelId(Constant.VIDEO_MODEL);
        } else {
            mIsAgainCompile = true;
            mCheckMac = mProgramBean.getDeviceMacs();
            mVideoPath = mProgramBean.getVideos();//提取视频路径
        }
        initView();
        mSaveVideoPath.addAll(mVideoPath);
        sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
    }

    public void initView() {
        //title
        View title = findViewById(R.id.program_video_title);
        mIb_tool = (ImageButton) title.findViewById(R.id.titlebar_tool);
        mIb_tool.setVisibility(View.VISIBLE);
        mIb_tool.setOnClickListener(mOnClickListener);
        TextView title_right = (TextView) title.findViewById(R.id.title_right);
        title_right.setVisibility(View.GONE);
        LinearLayout back = (LinearLayout) title.findViewById(R.id.title_back);
        back.setOnClickListener(mOnClickListener);
        TextView conter = (TextView) findViewById(R.id.title_center);
        conter.setText(getString(R.string.program_compile));

        //add
        mAdd = findViewById(R.id.iv_add_video);
        mAdd.setOnClickListener(mOnClickListener);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_program_video_content);
        //Animator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ProgramVideoAdapter(mVideoPath, this);
        mRecyclerView.setAdapter(mAdapter);
        if (!mIsAgainCompile)
            mAdapter.addItem();
        mAdapter.setMyOnClickListener(new BaseRecyclerViewAdapterClick.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                chooseVideo();
                mAddPathPosition = position;
            }
        });
    }

    /**
     * 获得非空视频路径
     *
     * @return
     */
    public ArrayList<String> getNoNullPath() {

        ArrayList<String> noNullPath = new ArrayList<>();
        for (String path : mVideoPath) {
            if (!TextUtils.isEmpty(path))
                noNullPath.add(path);
        }
        return noNullPath;
    }

    /**
     * 获取SD卡视频
     */
    private void chooseVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        startActivityForResult(intent, GET_VIDEO_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 选取图片的返回值
        if (requestCode == GET_VIDEO_RESULT) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null,
                        null, null);
                cursor.moveToFirst();
                String v_path = cursor.getString(1); // 图片文件路径
                String v_name = cursor.getString(2); // 图片文件名
                String v_size = cursor.getString(3); // 图片大小
                String v_type = cursor.getString(4); // 图片类型
                LogUtil.e("v_path=" + v_path);
                LogUtil.e("v_name=" + v_name);
                LogUtil.e("v_size=" + v_size);
                LogUtil.e("v_size=M" + Integer.parseInt(v_size) / 1024 / 1024);
                LogUtil.e("v_type=" + v_type);
                if (Integer.parseInt(v_size) / 1024 / 1024 > 100) {
                    ToastUtil.showToast(ProgramVideoActivity.this, getString(R.string.video_size_too_big), 3000);
                } else
                    mAdapter.setPath(mAddPathPosition, v_path);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.title_back://返回
                    if (isDataChange())
                        saveProgram();
                    else
                        finish();
                    break;
                case R.id.iv_add_video://添加
                    AnimationUtil.togetherRun(mAdd, 270);
                    if (mAdapter.getItemCount() < 10) {
                        mAdapter.addItem();
                    } else
                        ToastUtil.showToast(ProgramVideoActivity.this, getString(R.string.add_video_max), 3000);
                    break;
                case R.id.titlebar_tool:
                    showPopupwindow();
                    break;
                case R.id.pp_program_push://发布
                    mPopupWindow.dismiss();
                    if (getNoNullPath().size() > 0)
                        terminalSelect();
                    else
                        ToastUtil.showToast(ProgramVideoActivity.this, getResources().getString(R.string.please_compile), 3000);
                    break;
                case R.id.pp_preview://预览
                    mPopupWindow.dismiss();
                    Intent intent = new Intent(ProgramVideoActivity.this, ProgramVideoPreview.class);
                    intent.putStringArrayListExtra("videoPath", getNoNullPath());
                    startActivity(intent);
                    break;
                case R.id.pp_save://保存
                    mPopupWindow.dismiss();
                    saveProgram();
                    break;
            }
        }
    };

    /**
     * 发布
     */
    public void pushData() {
        SharedPreferences sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        String userID = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(this, getResources().getString(R.string.please_login), 3000);
            toActivity(LoginActivity.class);
            return;
        }
        if (mCheckMac.size() <= 0) {
            ToastUtil.showToast(this, getResources().getString(R.string.terminal_id_no_null), Toast.LENGTH_SHORT);
            return;
        }
        mProgramBean.setDeviceMacs(mCheckMac);//需要推送终端的Mac地址
        mProgramBean.setCreation_time(GeneralUtil.getTimeStr());
        mProgramBean.setModelId(Constant.VIDEO_MODEL);//模板ID
        mProgramBean.setVideos(getNoNullPath());
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setState("0");

        Intent intent = new Intent();
        intent.putExtra("body", mProgramBean);
        LogUtil.d(TAG, "programPush:" + mProgramBean);
        setResult(mProgramResultCode, intent);//编辑结果返回
        finish();

    }

    /**
     * 终端选择
     */
    private void terminalSelect() {
        String terminalJson = sp.getString(Constant.MY_TERMINAL, "");
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
                pushData();
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
     * 数据是否改变
     *
     * @return
     */
    public boolean isDataChange() {

        if (mSaveVideoPath.size() == mVideoPath.size()) {

            for (int i = 0; i < mSaveVideoPath.size(); i++) {
                if (!mSaveVideoPath.get(i).equals(mVideoPath.get(i)))
                    return true;
            }
            return false;
        } else
            return true;
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
     * 保存节目到数据库
     */
    public void saveProgram() {
        SharedPreferences sp = getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        String userID = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(this, getResources().getString(R.string.please_login), 3000);
            toActivity(LoginActivity.class);
            return;
        }
        mProgramBean.setCreation_time(GeneralUtil.getTimeStr());
        mProgramBean.setModelId(Constant.VIDEO_MODEL);//模板ID
        mProgramBean.setVideos(getNoNullPath());//需要推送终端的Mac地址
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setState("0");
        try {
            ProgramDao mProgramDao = ProgramDao.getInstance(this);
            if (mIsAgainCompile) {
                mProgramBean.setDeviceMacs(mCheckMac);//需要推送终端的Mac地址
                mProgramDao.updateInDataBaseId(mProgramBean, userID);
            } else
                mProgramDao.addProgram(mProgramBean, userID);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 推送、预览、保存
     */
    public void showPopupwindow() {
        mPopupWindow = new PopupWindow(this);
        View contentView = getLayoutInflater().inflate(R.layout.program_popupwindow_layout, null);
        contentView.findViewById(R.id.pp_program_push).setOnClickListener(mOnClickListener);
        contentView.findViewById(R.id.pp_preview).setOnClickListener(mOnClickListener);
        contentView.findViewById(R.id.pp_save).setOnClickListener(mOnClickListener);
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setWidth(GeneralUtil.dip2px(this, 100));
        mPopupWindow.setOutsideTouchable(true);//触摸外部消失
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x000000000));//透明背景
        mPopupWindow.setAnimationStyle(R.style.push_popupwindow_style);//动画
        mPopupWindow.showAsDropDown(mIb_tool, GeneralUtil.dip2px(this, 21), GeneralUtil.dip2px(this, -21));
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mIb_tool.setClickable(true);
                mIb_tool.setEnabled(true);
            }
        });
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
