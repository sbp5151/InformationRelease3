package com.jld.InformationRelease.view.my_program.program_create;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.util.AnimationUtil;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.TimeUtil;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.login_register.LoginActivity;
import com.jld.InformationRelease.view.my_program.program_create.adapter.ProgramVideoAdapter;
import com.jld.InformationRelease.view.my_program.program_create.preview.ProgramVideoPreview;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;

import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

/**
 * 视频广告  节目编辑
 */
public class ProgramVideoActivity extends BaseProgramCompileActivity {

    private static final String TAG = "ProgramVideoActivity";
    private static final int GET_VIDEO_RESULT = 0x01;
    private ArrayList<String> mVideoPath = new ArrayList<>();
    private ArrayList<String> mLastPath = new ArrayList<>();
    private ProgramVideoAdapter mAdapter;
    private View mAdd;
    private int mAddPathPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_video);
        LogUtil.d(TAG, "mProgramBean:" + mProgramBean);
        if (mIsAgainCompile) {
            mVideoPath = mProgramBean.getVideos();//提取视频路径
            mLastPath.addAll(mVideoPath);
        }
        setPopupWindowListener(mPopupWindowListener);
        initView();
    }

    public void initView() {
        //title
        View title = findViewById(R.id.program_video_title);
        mIb_tool = (ImageButton) title.findViewById(R.id.title_tool);
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
        SwipeMenuRecyclerView recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.rv_program_video_content);
        //Animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setSwipeMenuItemClickListener(mSwipeMenuItemClickListener);
//        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        mAdapter = new ProgramVideoAdapter(mVideoPath, this);
        recyclerView.setAdapter(mAdapter);
        if (!mIsAgainCompile) {
//            chooseVideo();//自动进入添加界面
            mAdapter.addItem();
//            mAddPathPosition = 0;
        }
        mAdapter.setMyOnClickListener(new ProgramVideoAdapter.OnProgramVideoItemClickListen() {
            @Override
            public void onSelectItemClick(View view, int position) {
                chooseVideo();
                mAddPathPosition = position;
            }

            @Override
            public void onDeleteItemClick(View view, int position) {

                mAdapter.removeItem(position);
            }
        });
    }

    SwipeMenuItemClickListener mSwipeMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int position = menuBridge.getAdapterPosition();
            mAdapter.removeItem(position);
        }
    };


    /**
     * 侧滑菜单 item创建
     */
    SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.swipe_menu_item_width2);
            int height = getResources().getDimensionPixelSize(R.dimen.swipe_menu_item_height);
            SwipeMenuItem menuItem = new SwipeMenuItem(ProgramVideoActivity.this)
                    .setBackground(R.drawable.swipe_menu_delete)
                    .setHeight(height)
                    .setWidth(width)
                    .setText(getString(R.string.delete))
                    .setTextColor(getResources().getColor(R.color.white));
            swipeRightMenu.addMenuItem(menuItem);
        }
    };

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
        // 选取视频的返回值
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
                String[] split = v_type.split("/");
                LogUtil.e("split[0]=" + split[0]);
                LogUtil.e("split[1]=" + split[1]);
                if (split[0].equals("video") && (split[1].equals("mp4") || split[1].equals("3gp") || split[1].equals("mov"))) {
                    if (split[0].equals("video") && Integer.parseInt(v_size) / 1024 / 1024 > 100) {
                        ToastUtil.showToast(ProgramVideoActivity.this, getString(R.string.video_size_too_big), 3000);
                    } else {
                        mAdapter.setPath(mAddPathPosition, v_path);
                    }
                } else
                    ToastUtil.showToast(ProgramVideoActivity.this, getString(R.string.video_type_nonsupport), 3000);

            } else {
                LogUtil.d(TAG, "返回数据为空" + mAddPathPosition);
                LogUtil.d(TAG, "返回数据为空" + mAdapter.getItemCount());
                if (TextUtils.isEmpty(mAdapter.getItem(mAddPathPosition)))
                    mAdapter.removeItem(mAddPathPosition);
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
                        chooseVideo();//自动进入添加界面
                        mAdapter.addItem();
                        mAddPathPosition = mAdapter.getItemCount() - 1;
                    } else
                        ToastUtil.showToast(ProgramVideoActivity.this, getString(R.string.add_video_max), 3000);
                    break;
                case R.id.title_tool:
                    showPopupwindow();
                    break;
//                case R.id.pp_program_push://发布
//                    mPopupWindow.dismiss();
//                    if (getNoNullPath().size() > 0)
//                        terminalSelect();
//                    else
//                        ToastUtil.showToast(ProgramVideoActivity.this, getResources().getString(R.string.please_compile), 3000);
//                    break;
//                case R.id.pp_preview://预览
//                    Intent intent = new Intent(ProgramVideoActivity.this, ProgramVideoPreview.class);
//                    intent.putStringArrayListExtra("videoPath", getNoNullPath());
//                    startActivity(intent);
//                    break;
//                case R.id.pp_save://保存
//                    mPopupWindow.dismiss();
//                    saveProgram();
//                    break;
            }
        }
    };

    /**
     * 发布
     */
    public void programPush() {
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
        mProgramBean.setModelId(Constant.VIDEO_MODEL);//模板ID
        mProgramBean.setVideos(getNoNullPath());
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setUpload_state(Constant.UPLOAD_STATE_NOT);
        Intent intent = new Intent();
        intent.putExtra("body", mProgramBean);
        LogUtil.d(TAG, "programPush:" + mProgramBean);
        setResult(mProgramResultCode, intent);//编辑结果返回
        finish();
    }

    /**
     * 数据是否改变
     *
     * @return
     */
    public boolean isDataChange() {
        if (mIsAgainCompile) {
            if (mLastPath.size() == mVideoPath.size()) {
                for (int i = 0; i < mLastPath.size(); i++) {
                    if (!mLastPath.get(i).equals(mVideoPath.get(i)))
                        return true;
                }
                return false;
            } else
                return true;
        } else if (mVideoPath.size() > 0 && !TextUtils.isEmpty(mVideoPath.get(0))) {
            return true;
        }
        return false;
    }

    /**
     * 保存节目到数据库
     */
    public void saveProgram() {
        String userID = mSp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(userID)) {//账号不能为空
            ToastUtil.showToast(this, getResources().getString(R.string.please_login), 3000);
            toActivity(LoginActivity.class);
            return;
        }
        mProgramBean.setTime(TimeUtil.getTodayDateTime());
        mProgramBean.setModelId(Constant.VIDEO_MODEL);//模板ID
        mProgramBean.setVideos(getNoNullPath());//需要推送终端的Mac地址
        mProgramBean.setUserid(userID);//账号
        mProgramBean.setUpload_state(Constant.UPLOAD_STATE_NOT);
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

    PopupWindowListener mPopupWindowListener = new PopupWindowListener() {
        @Override
        public void onPreview() {
            Intent intent = new Intent(ProgramVideoActivity.this, ProgramVideoPreview.class);
            intent.putStringArrayListExtra("videoPath", getNoNullPath());
            startActivity(intent);
        }

        @Override
        public void onProgramPush() {
            if (getNoNullPath().size() > 0)
                terminalSelect();
            else
                ToastUtil.showToast(ProgramVideoActivity.this, getResources().getString(R.string.please_compile), 3000);
        }
    };
}
