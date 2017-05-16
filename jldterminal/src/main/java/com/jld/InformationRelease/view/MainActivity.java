package com.jld.InformationRelease.view;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jld.InformationRelease.JPushReceiver;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.bean.response.PushResponse;
import com.jld.InformationRelease.model.GetScreenModel;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.presenter.GetScreenPresenter;
import com.jld.InformationRelease.presenter.LoadProgramPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.DeviceUtil;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.MD5Util;
import com.jld.InformationRelease.util.VolumeUtil;
import com.jld.InformationRelease.view.fragment.ProgramFragment_1;

import java.io.File;

public class MainActivity extends BaseActivity implements JPushReceiver.JPushListener, IViewToPresenter<ProgramResponseBean> {

    private static final int LOAD_PROGRAM_TAG = 0x12;
    private static final String TAG = "MainActivity";
    private String[] names1 = {"原味奶茶", "香芋奶茶", "芒果奶茶", "草莓奶茶", "哈密瓜奶茶", "珍珠奶茶"};
    private String[] names2 = {"芒果奶昔", "草莓奶昔", "芦荟奶昔", "蓝莓奶昔", "水蜜桃奶昔", "哈密瓜奶昔"};
    private ProgressDialog mPdialog;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = (FrameLayout) findViewById(R.id.framelayout_main);
        //注册极光推送监听
        JPushReceiver.sendListener(this);
        mPdialog = new ProgressDialog(this);
    }

    /**
     * 极光推送返回监听
     *
     * @param pushMsg
     */
    @Override
    public void pushMessage(String pushMsg) {

        Gson gson = new Gson();
        PushResponse response;
        try {
            response = gson.fromJson(pushMsg, PushResponse.class);
        } catch (JsonSyntaxException e) {
            Log.d(TAG, e.toString());
            return;
        }
        if (response == null)
            throw new IllegalArgumentException("推送json异常 ");
        switch (response.getResult()) {
            case Constant.PUSH_UNBIND://解绑
                SharedPreferences.Editor edit = getSharedPreferences(Constant.share_key, MODE_PRIVATE).edit();
                edit.putBoolean(Constant.DEVICE_ISBINDING, false).apply();
                toActivity(QRCodeActivity.class);
                finish();
                break;
            case Constant.PUSH_PROGRAM://节目推送
                //加载节目更新
                String programID = response.getMsg();//节目ID
                LoadProgramPresenter presenter = new LoadProgramPresenter(this, MainActivity.this);
                String md5 = MD5Util.getMD5(programID + Constant.S_KEY);
                presenter.LoadProgram(md5, LOAD_PROGRAM_TAG);
                break;
            case Constant.SHUTDOWN://关机
                DeviceUtil.showDown();
                break;
            case Constant.RESTART://重启
                DeviceUtil.restart();
                break;
            case Constant.VOLUME_SETTING://音量调节
                Log.d(TAG, "current:" + VolumeUtil.getCurrentVolume(this));
                Log.d(TAG, "max:" + VolumeUtil.getMaxVolume(this));
                String volume = response.getVolume();
                Log.d(TAG, "volume:" + volume);

                try {
                    int volumei = Integer.parseInt(volume);
                    DeviceUtil.volumeSetting(this, volumei);
                } catch (NumberFormatException e) {
                    Log.d(TAG, "NumberFormatException:" + e);
                }
                break;
            case Constant.GET_SCREEN://获取截屏
                String savePath = MainActivity.this.getExternalCacheDir().getAbsolutePath() + File.separator + GeneralUtil.getTimeStr() + "111.png";
                Log.e(TAG, "absolutePath:" + savePath);
                new GetScreenPresenter().getScreen(this, savePath, new GetScreenModel.GetScreenListen() {
                    @Override
                    public void onScreenError() {//获取失败
                        // TODO: 2017/5/16 获取截屏失败
                    }

                    @Override
                    public void onScreenComplete() {//获取成功
                        //上传图片，获取图片链接
                        new FilePresenter(MainActivity.this, new IViewToPresenter<FileResponseBean>() {
                            @Override
                            public void showProgress(int requestTag) {

                            }

                            @Override
                            public void hideProgress(int requestTag) {

                            }

                            @Override
                            public void loadDataSuccess(FileResponseBean data, int requestTag) {


                            }

                            @Override
                            public void loadDataError(Throwable e, int requestTag) {

                                // TODO: 2017/5/16 上传截屏文件失败
                            }
                        });
                    }
                });
                break;
        }
    }

    @Override
    public void showProgress(int requestTag) {
        if (requestTag == LOAD_PROGRAM_TAG) {
            Toast.makeText(this, getString(R.string.load_program), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void hideProgress(int requestTag) {

    }

    //节目加载成功
    @Override
    public void loadDataSuccess(ProgramResponseBean data, int requestTag) {
        if (requestTag == LOAD_PROGRAM_TAG) {
            Toast.makeText(this, getString(R.string.load_program_succeed), Toast.LENGTH_SHORT).show();
            ProgramFragment_1 fragment_1 = ProgramFragment_1.getInstance(data);
            replaceFragment(fragment_1);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        if (requestTag == LOAD_PROGRAM_TAG) {
            Toast.makeText(this, getString(R.string.load_program_error), Toast.LENGTH_SHORT).show();
        }
    }

    //节目替换
    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.framelayout_main, fragment);
        ft.commit();
    }
}
