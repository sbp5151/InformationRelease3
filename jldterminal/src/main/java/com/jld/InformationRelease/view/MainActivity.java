package com.jld.InformationRelease.view;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jld.InformationRelease.JPushReceiver;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.base.BaseResponse;
import com.jld.InformationRelease.base.IViewToPresenter;
import com.jld.InformationRelease.base.SimpleIViewToPresenter;
import com.jld.InformationRelease.bean.response.FileResponseBean;
import com.jld.InformationRelease.bean.response.ProgramResponseBean;
import com.jld.InformationRelease.bean.response.PushResponse;
import com.jld.InformationRelease.model.GetScreenModel;
import com.jld.InformationRelease.presenter.FilePresenter;
import com.jld.InformationRelease.presenter.GetScreenPresenter;
import com.jld.InformationRelease.presenter.LoadProgramPresenter;
import com.jld.InformationRelease.presenter.UploadScreenPresenter;
import com.jld.InformationRelease.util.Constant;
import com.jld.InformationRelease.util.DeviceUtil;
import com.jld.InformationRelease.util.GeneralUtil;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.ModelIds;
import com.jld.InformationRelease.util.VolumeUtil;
import com.jld.InformationRelease.view.fragment.DefaultFragment;
import com.jld.InformationRelease.view.fragment.ProgramFragment_1;
import com.jld.InformationRelease.view.fragment.ProgramFragment_2;
import com.jld.InformationRelease.view.fragment.ProgramVideoFragment;

import java.io.File;

public class MainActivity extends BaseActivity implements JPushReceiver.JPushListener, IViewToPresenter<ProgramResponseBean> {

    private static final int LOAD_PROGRAM_TAG = 0x12;
    private static final String TAG = "MainActivity";
    private static final int UPLOAD_SCREEN_REQUESTID = 0x21;
    private static final int GET_SCREEN_URL_REQUESTTAG = 0x22;
    private String[] names1 = {"原味奶茶", "香芋奶茶", "芒果奶茶", "草莓奶茶", "哈密瓜奶茶", "珍珠奶茶"};
    private String[] names2 = {"芒果奶昔", "草莓奶昔", "芦荟奶昔", "蓝莓奶昔", "水蜜桃奶昔", "哈密瓜奶昔"};
    private ProgressDialog mPdialog;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = (FrameLayout) findViewById(R.id.framelayout_main);
        //注册极光推送监听
        JPushReceiver.sendListener(this);
        mPdialog = new ProgressDialog(this);
        replaceFragment(null);
    }

    /**
     * 极光推送返回监听
     *
     * @param pushMsg
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
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
                String programID = response.getProgramID();//节目ID
                LoadProgramPresenter presenter = new LoadProgramPresenter(this, MainActivity.this);
                presenter.LoadProgram(programID, LOAD_PROGRAM_TAG);
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
                getScreen(savePath);
                break;
        }
    }

    /**
     * 获取截屏
     *
     * @param savePath 截屏保存地址
     */
    public void getScreen(final String savePath) {
        new GetScreenPresenter().getScreen(this, savePath, new GetScreenModel.GetScreenListen() {
            @Override
            public void onScreenError() {//获取失败
            }

            @Override
            public void onScreenComplete() {//获取成功
                //上传截屏文件获取截屏url
                getScreenUrl(savePath);
            }
        });
    }

    /**
     * 上传截屏文件获取截屏url
     *
     * @param screenPath 截屏文件目录
     */
    public void getScreenUrl(String screenPath) {
        //上传图片，获取图片链接
        FilePresenter filePresenter = new FilePresenter(MainActivity.this, new SimpleIViewToPresenter<FileResponseBean>() {

            @Override
            public void loadDataSuccess(FileResponseBean data, int requestTag) {

                String fileUrl = data.getFileUrl();
                if (!TextUtils.isEmpty(fileUrl)) {
                    //上传截屏
                    uploadScreen(fileUrl);
                }
            }

            @Override
            public void loadDataError(Throwable e, int requestTag) {

            }
        });
        filePresenter.updateFile(screenPath, GET_SCREEN_URL_REQUESTTAG);
    }

    /**
     * 上传截屏
     *
     * @param screenUrl 截屏url
     */
    public void uploadScreen(String screenUrl) {

        UploadScreenPresenter uploadScreenPresenter = new UploadScreenPresenter(new SimpleIViewToPresenter<BaseResponse>() {
            @Override
            public void loadDataSuccess(BaseResponse data, int requestTag) {
                Toast.makeText(MainActivity.this, data.getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void loadDataError(Throwable e, int requestTag) {
            }
        }, MainActivity.this);
        uploadScreenPresenter.uploadScreen(screenUrl, UPLOAD_SCREEN_REQUESTID);
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
            replaceFragment(data);
        }
    }

    @Override
    public void loadDataError(Throwable e, int requestTag) {
        LogUtil.d(TAG, "loadDataError:" + e.getMessage());
        if (requestTag == LOAD_PROGRAM_TAG) {
            Toast.makeText(this, getString(R.string.load_program_error), Toast.LENGTH_SHORT).show();
        }
    }

    //节目替换
    private void replaceFragment(ProgramResponseBean data) {
        LogUtil.d(TAG, "replaceFragment:" + data);
        if (data != null) {
            //保存数据
            String play_data = new Gson().toJson(data);
            SharedPreferences.Editor sp_edit = getSharedPreferences(Constant.share_key, MODE_PRIVATE).edit();
            sp_edit.putString(Constant.PLAY_DATA, play_data);
            sp_edit.apply();
        } else {
            String play_data = getSharedPreferences(Constant.share_key, MODE_PRIVATE).getString(Constant.PLAY_DATA, "");
            if (!TextUtils.isEmpty(play_data)) {
                data = new Gson().fromJson(play_data, ProgramResponseBean.class);
                LogUtil.d(TAG, "data:" + data);
                if (data == null) {//没有数据加载默认布局
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    DefaultFragment defaultFragment = new DefaultFragment();
                    ft.replace(R.id.framelayout_main, defaultFragment);
                    ft.commit();
                    return;
                }
            } else {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                DefaultFragment defaultFragment = new DefaultFragment();
                ft.replace(R.id.framelayout_main, defaultFragment);
                ft.commit();
                return;
            }
        }

        //展示数据
        String modelId = data.getItem().getModelId();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MainActivity.this.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        switch (modelId) {
            case ModelIds.NAICHA_MODEL_1:
                ProgramFragment_1 fragment1 = ProgramFragment_1.getInstance(data);
                ft.replace(R.id.framelayout_main, fragment1);
                break;
            case ModelIds.IMAGE_MODEL:
                ProgramFragment_2 fragment2 = ProgramFragment_2.getInstance(data);
                ft.replace(R.id.framelayout_main, fragment2);

                break;
            case ModelIds.VIDEO_MODEL:
                ProgramVideoFragment videoFragment = new ProgramVideoFragment(data.getItem().getVideos());
                ft.replace(R.id.framelayout_main, videoFragment);
                MainActivity.this.setRequestedOrientation(//通过程序改变屏幕显示的方向
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR);//横屏
                break;
            default:
                return;
        }
        ft.commit();
    }
}
