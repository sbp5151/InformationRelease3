package com.jld.InformationRelease.view.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.jld.InformationRelease.R;
import com.jld.InformationRelease.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

public class ProgramVideoPreview extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private ArrayList<String> videoPath;
    //当前播放视频
    private int currentPlayNum = 0;
    private Display mCurrDisplay;
    private SurfaceHolder mHolder;
    private SurfaceView mSurfaceView;
    public static final String TAG = "ProgramVideoPreview-----";
    private View mController_view;
    public static final int CONTROLLER_HIDE = 0x01;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONTROLLER_HIDE://3s后隐藏
                    mController_view.setVisibility(View.GONE);
                    break;
            }
        }
    };
    private View mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_video_preview);
        videoPath = getIntent().getStringArrayListExtra("videoPath");
        LogUtil.d(TAG, "videoPath:" + videoPath);
        if (videoPath == null || videoPath.size() == 0) {
            // TODO: 2017/6/21 视频加载出错
            finish();
        }
        initView();
        initPlay();
    }

    public void initView() {
        mController_view = findViewById(R.id.media_controller);
        mController_view.setVisibility(View.GONE);
        View backward = mController_view.findViewById(R.id.ib_backward);
        backward.setOnClickListener(mOnClickListener);
        View forward = mController_view.findViewById(R.id.ib_forward);
        forward.setOnClickListener(mOnClickListener);
        mPlay = mController_view.findViewById(R.id.ib_play);
        mPlay.setOnClickListener(mOnClickListener);
        mSurfaceView = (SurfaceView) findViewById(R.id.sv_program_video);
        mSurfaceView.setOnClickListener(mOnClickListener);
    }

    public void initPlay() {
        mHolder = mSurfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(mCallback);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                LogUtil.d(TAG, "onCompletion:" + currentPlayNum);
                currentPlayNum++;
                if (currentPlayNum >= videoPath.size()) {
                    currentPlayNum = 0;
                }
                try {
                    mMediaPlayer.setDataSource(videoPath.get(currentPlayNum));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                LogUtil.d(TAG, "onPrepared:");
                // 当prepare完成后，该方法触发，在这里我们播放视频
                mMediaPlayer.start();
                //首先取得video的宽和高
//                int vWidth = mMediaPlayer.getVideoWidth();
//                int vHeight = mMediaPlayer.getVideoHeight();
//
//                if (vWidth > mCurrDisplay.getWidth() || vHeight > mCurrDisplay.getHeight()) {
//                    //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
//                    float wRatio = (float) vWidth / (float) mCurrDisplay.getWidth();
//                    float hRatio = (float) vHeight / (float) mCurrDisplay.getHeight();
//
//                    //选择大的一个进行缩放
//                    float ratio = Math.max(wRatio, hRatio);
//
//                    vWidth = (int) Math.ceil((float) vWidth / ratio);
//                    vHeight = (int) Math.ceil((float) vHeight / ratio);
//
//                    //设置surfaceView的布局参数
//                    mSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(vWidth, vHeight));
//
//                    //然后开始播放视频
//
//
//                }
            }
        });

        try {
            LogUtil.d(TAG, "currentPlayNum:" + videoPath.get(currentPlayNum));
            mMediaPlayer.setDataSource(videoPath.get(currentPlayNum));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //然后，我们取得当前Display对象
        mCurrDisplay = this.getWindowManager().getDefaultDisplay();
    }

    public void last() {
        if (currentPlayNum == 0) {
            return;
        }
        currentPlayNum--;
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(videoPath.get(currentPlayNum));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }

    public void next() {
        currentPlayNum++;
        if (currentPlayNum >= videoPath.size()) {
            currentPlayNum = 0;
        }
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(videoPath.get(currentPlayNum));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.ib_backward://上一首
                    last();
                    showController();
                    break;
                case R.id.ib_forward://下一首
                    next();
                    showController();
                    break;
                case R.id.ib_play://暂停/播放
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mPlay.setBackgroundResource(R.drawable.ic_play);
                    } else {
                        mMediaPlayer.start();
                        mPlay.setBackgroundResource(R.drawable.ic_pause);
                    }
                    showController();
                    break;
                case R.id.sv_program_video://显示controller
                    showController();
                    break;
            }

        }
    };
    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            LogUtil.d(TAG, "surfaceCreated:");
            mHolder = surfaceHolder;
            mMediaPlayer.setDisplay(mHolder);
            //准备播放
            mMediaPlayer.prepareAsync();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            LogUtil.d(TAG, "surfaceChanged:");
            mHolder = surfaceHolder;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            LogUtil.d(TAG, "surfaceDestroyed:");
            //释放资源
            mHolder.removeCallback(mCallback);
            mMediaPlayer.release();
        }
    };

    /**
     * 显示控制窗口
     */
    public void showController() {
        if (mController_view.getVisibility() == View.VISIBLE) {//
            mHandler.removeMessages(CONTROLLER_HIDE);
            mHandler.sendEmptyMessageDelayed(CONTROLLER_HIDE, 3000);
            return;
        }
        mController_view.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(CONTROLLER_HIDE, 3000);
    }
}
