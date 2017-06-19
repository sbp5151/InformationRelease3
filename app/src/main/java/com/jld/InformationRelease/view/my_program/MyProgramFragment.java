package com.jld.InformationRelease.view.my_program;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.bean.ProgramBean;
import com.jld.InformationRelease.db.ProgramDao;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.ToastUtil;
import com.jld.InformationRelease.util.UserConstant;
import com.jld.InformationRelease.view.MainActivity;
import com.jld.InformationRelease.view.service.ProgramPushService;

import org.json.JSONException;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramRequestCode;
import static com.jld.InformationRelease.view.my_terminal.MyTerminalFragment.mProgramResultCode;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * 节目列表
 */
public class MyProgramFragment extends Fragment {

    private static final java.lang.String TAG = "MyProgramFragment";
    private MainActivity mActivity;
    private ArrayList<ProgramBean> mAllProgram;
    public MyProgramRecyclerAdapter mAdapter;
    private ImageView mIv_add;
    public static final int NEW_PROGRAM = 0x11;//新建节目
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NEW_PROGRAM:
                    Intent intent = new Intent(mActivity, ProgramCompileActivity.class);
                    intent.putParcelableArrayListExtra("terminal_data", mActivity.mTerminal_fragment.mAdapter.getData());//终端数据
                    startActivityForResult(intent, mProgramRequestCode);
                    break;
            }
        }
    };
    private TextView mTvComplete;
    private String mUserid;
    private ProgressBar mProgramBean;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.d(TAG, "hidden:" + hidden);
        initData();
        if (mAllProgram != null)
            mAdapter.update(mAllProgram);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_model, container, false);
        SharedPreferences sp = mActivity.getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
        mUserid = sp.getString(UserConstant.USER_ID, "");
        if (TextUtils.isEmpty(mUserid)) {//需要登录
            ToastUtil.showToast(mActivity, getResources().getString(R.string.please_login), 3000);
            return view;
        }
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    private void initView(View view) {
        //title
        View title_view = view.findViewById(R.id.my_progarm_title);
        ImageButton menu = (ImageButton) title_view.findViewById(R.id.toolbar_left);
        mTvComplete = (TextView) title_view.findViewById(R.id.toolbar_complete);
        mProgramBean = (ProgressBar) title_view.findViewById(R.id.pb_program_upload);
        mTvComplete.setOnClickListener(mOnClickListener);
        title_view.findViewById(R.id.toolbar_push).setVisibility(View.GONE);
        menu.setOnClickListener(mOnClickListener);
        TextView title_content = (TextView) title_view.findViewById(R.id.toolbar_title);
        title_content.setText(getString(R.string.program_list));

        //RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.terminal_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyProgramRecyclerAdapter(mActivity, mAllProgram);
        mAdapter.setMyItemSelectClick(mMyItemClick);
        mRecyclerView.setAdapter(mAdapter);
        //add
        mIv_add = (ImageView) view.findViewById(R.id.imagev_add_trouteam);
        mIv_add.setOnClickListener(mOnClickListener);
    }


    public void initData() {
        ProgramDao programDao = ProgramDao.getInstance(mActivity);
        try {
            mAllProgram = programDao.getAllProgram(mUserid);
            LogUtil.d(TAG, "mAllProgram:" + mAllProgram);
        } catch (JSONException e) {
            ToastUtil.showToast(mActivity, getResources().getString(R.string.read_data_error), 3000);
            e.printStackTrace();
        }
        if (mAllProgram != null)
            mAdapter.update(mAllProgram);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toolbar_left:
                    if (mActivity.mDrawer.isDrawerOpen(mActivity.mNavigationView)) {
                        mActivity.mDrawer.closeDrawer(mActivity.mNavigationView);
                    } else
                        mActivity.mDrawer.openDrawer(mActivity.mNavigationView);
                    break;
                case R.id.toolbar_complete:
                    if (mAdapter.getCompileState()) {//取消编辑
                        mTvComplete.setText(getResources().getString(R.string.completer));
                        mIv_add.setVisibility(View.VISIBLE);
                    } else {//编辑
                        mTvComplete.setText(getResources().getString(R.string.cancle));
                        mIv_add.setVisibility(View.GONE);
                    }
                    mAdapter.changeCompileState();
                    break;
                case R.id.imagev_add_trouteam://新建节目单
                    togetherRun(mIv_add, 270);
                    mHandler.sendEmptyMessageDelayed(NEW_PROGRAM, 270);
                    break;
            }
        }
    };
    MyProgramRecyclerAdapter.MyItemClick mMyItemClick = new MyProgramRecyclerAdapter.MyItemClick() {
        @Override
        public void onItemClickListen(View view, int position) {
            if (!mAdapter.getCompileState()) {//在编辑状态不让点击
                Intent intent = new Intent(mActivity, ProgramCompileActivity.class);
                intent.putExtra("program_data", mAllProgram.get(position));//节目数据
                intent.putParcelableArrayListExtra("terminal_data", mActivity.mTerminal_fragment.mAdapter.getData());//终端数据
                startActivityForResult(intent, mProgramRequestCode);
            }
        }

        @Override
        public void onItemDeleteClickListen(View view, int position) {
            deleteProgramDialog(position);
        }
    };

    public void deleteProgramDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getResources().getString(R.string.hint))
                .setMessage(getResources().getString(R.string.sure_delete_program))
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除节目
                        ProgramDao programDao = ProgramDao.getInstance(mActivity);
                        programDao.deleteProgram(mAdapter.getData(position).getTable_id() + "", mUserid);
                        initData();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public ProgramBean mProgram;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mProgramRequestCode && resultCode == mProgramResultCode) {
            //再发布
            ProgramBean body = (ProgramBean) data.getSerializableExtra("body");
            if (body != null) {
                mProgram = body;
                LogUtil.d(TAG, "获取返回的节目数据：" + mProgram);
                createProgramService();
                mProgramBean.setVisibility(View.VISIBLE);
                mTvComplete.setVisibility(View.GONE);
                ToastUtil.showToast(mActivity, getString(R.string.program_back_push), 3000);
            }
        }
    }

    /**
     * 创建后台服务上传节目
     */
    public void createProgramService() {
        Intent intent = new Intent(mActivity, ProgramPushService.class);
        mActivity.startService(intent);
        mActivity.bindService(intent, mCon, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ProgramPushService.MyBinder myBinder = (ProgramPushService.MyBinder) iBinder;

            //更新数据库为已上传状态
            final SharedPreferences sp = mActivity.getSharedPreferences(Constant.SHARE_KEY, MODE_PRIVATE);
            final ProgramDao baseHelper = ProgramDao.getInstance(mActivity);
            mProgram.setSign(MD5Util.getMD5(Constant.S_KEY + mProgram.getUserid()));
            myBinder.sendPushData(mProgram);
            myBinder.sendCompleteListener(new ProgramPushService.PushCompleteListener() {
                @Override
                public void updateSucceed(String programId) {
                    mProgramBean.setVisibility(View.GONE);
                    mTvComplete.setVisibility(View.VISIBLE);
                    //上传成功 解绑service
                    mActivity.unbindService(mCon);
                    //更新数据库为已上传状态
                    LogUtil.d(TAG, "发布成功:");
                    try {
                        //保存到数据库
                        mProgram.setState("1");//上传成功状态
                        mProgram.setProgramId(programId);//更新节目ID
                        if (mProgram.getTable_id() == -1)//新建
                            baseHelper.addProgram(mProgram, sp.getString(UserConstant.USER_ID, ""));
                        else//再编辑
                            baseHelper.updateInProgram(mProgram, sp.getString(UserConstant.USER_ID, ""));
                        initData();//刷新界面
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ToastUtil.showToast(mActivity, getString(R.string.push_succeed), 3000);
                }

                @Override
                public void updateDefeated() {
                    mProgramBean.setVisibility(View.GONE);
                    mTvComplete.setVisibility(View.VISIBLE);
                    //解绑service
                    mActivity.unbindService(mCon);
                    //更新数据库为已上传状态
                    LogUtil.d(TAG, "发布失败:");
                    try {
                        //保存到数据库
                        mProgram.setState("-1");//上传失败状态
                        if (mProgram.getTable_id() == -1)//新建
                            baseHelper.addProgram(mProgram, sp.getString(UserConstant.USER_ID, ""));
                        else//再编辑
                            baseHelper.updateInProgram(mProgram, sp.getString(UserConstant.USER_ID, ""));
                        initData();//刷新界面
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ToastUtil.showToast(mActivity, getString(R.string.push_defeated), 3000);
                }
            });
            //开始上传
            myBinder.startPush();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    /**
     * 按钮动画
     *
     * @param imageView
     * @param duration
     */
    public static void togetherRun(ImageView imageView, int duration) {
        if (null == imageView) {
            return;
        }
        if (duration <= 0) {
            duration = 400;
        }
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(imageView, "scaleX",
                1.0f, 0.8f, 1.1f, 1.0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(imageView, "scaleY",
                1.0f, 0.8f, 1.1f, 1.0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(duration);
        animSet.setInterpolator(new LinearInterpolator());
        //两个动画同时执行
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }
}
