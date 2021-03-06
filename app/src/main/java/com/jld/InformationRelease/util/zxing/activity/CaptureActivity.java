package com.jld.InformationRelease.util.zxing.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.jld.InformationRelease.R;
import com.jld.InformationRelease.base.BaseActivity;
import com.jld.InformationRelease.util.LogUtil;
import com.jld.InformationRelease.util.zxing.camera.CameraManager;
import com.jld.InformationRelease.util.zxing.decoding.CaptureActivityHandler;
import com.jld.InformationRelease.util.zxing.decoding.InactivityTimer;
import com.jld.InformationRelease.util.zxing.decoding.RGBLuminanceSource;
import com.jld.InformationRelease.util.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.R.attr.data;
import static com.jld.InformationRelease.view.MainActivity.mScanResultCode;

/**
 * Initial the camera
 * 
 * @author zhangguoyu
 * 
 */
@RuntimePermissions
public class CaptureActivity extends BaseActivity implements Callback {


	private final static String TAG = "CaptureActivity";
	private boolean playBeep;
	private boolean vibrate;
	private boolean hasSurface;
	private String characterSet;
	private int ifOpenLight = 0;//判断是否开启闪光灯
	private MediaPlayer mediaPlayer;
	private ViewfinderView viewfinderView;
	private CaptureActivityHandler handler;
	private Vector<BarcodeFormat> decodeFormats;
	private InactivityTimer inactivityTimer;
	private static final float BEEP_VOLUME = 0.10f;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG,"onCreate");
		setContentView(R.layout.activity_capture);
		CameraManager.init(getApplication());

		//titlebar
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
//		View view = findViewById(R.id.scan_title_bar);
//		LinearLayout back = (LinearLayout) view.findViewById(R.id.title_back);
//		TextView center = (TextView) view.findViewById(R.id.title_center);
//		TextView reght = (TextView) findViewById(R.id.title_right);
//		reght.setVisibility(View.INVISIBLE);
//		center.setText(getString(R.string.scan_bind));
		ImageView back = (ImageView) findViewById(R.id.iv_scan_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent resultIntent = new Intent();
				CaptureActivity.this.setResult(0, resultIntent);
				finish();
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
//		setListener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(event.getAction()==KeyEvent.KEYCODE_BACK){
			Intent resultIntent = new Intent();
			CaptureActivity.this.setResult(0, resultIntent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 注册事件
	 */
//	private void setListener() {
//		((TextView) findViewById(R.id.tv_left_title)).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
//	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
//			initCamera(surfaceHolder);
			CaptureActivityPermissionsDispatcher.initCameraWithCheck(CaptureActivity.this,surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 * @param result
	 * @param barcode
	 * 获取结果
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		Log.d("onActivityResult:", "playBeepSoundAndVibrate:" + data);

		if (resultString.equals("")) {
			Toast.makeText(CaptureActivity.this, "扫描失败!", Toast.LENGTH_SHORT)
					.show();
		} else {
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			resultIntent.putExtras(bundle);
			this.setResult(mScanResultCode, resultIntent);
		}
		CaptureActivity.this.finish();
	}

	/*
	 * 获取带二维码的相片进行扫描
	 */
	public void pickPictureFromAblum(View v) {
		Intent mIntent = new Intent(
				Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(mIntent, 1);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent) 对相册获取的结果进行分析
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				Result resultString = scanningImage1(picturePath);
				if (resultString == null) {
					Toast.makeText(getApplicationContext(), "解析错误，请选择正确的二维码图片", Toast.LENGTH_LONG).show();
				} else {

					String resultImage = resultString.getText();
					if (resultImage.equals("")) {

						Toast.makeText(CaptureActivity.this, "扫描失败",
								Toast.LENGTH_SHORT).show();
					} else {

						Intent resultIntent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("result", resultImage);
						resultIntent.putExtras(bundle);
						CaptureActivity.this.setResult(mScanResultCode, resultIntent);
					}

					CaptureActivity.this.finish();
				}
				
				break;
				
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 解析QR图内容
	 * 
	 * @return
	 */
	// 解析QR图片
	private Result scanningImage1(String picturePath) {

		if (TextUtils.isEmpty(picturePath)) {
			return null;
		}
		
		Map<DecodeHintType, String> hints1 = new Hashtable<DecodeHintType, String>();
		hints1.put(DecodeHintType.CHARACTER_SET, "utf-8");

		// 获得待解析的图片
		Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		Result result;
		try {

			result =  reader.decode(bitmap1, (Hashtable<DecodeHintType, String>) hints1);
			return result;
		} catch (NotFoundException e) {
			Toast.makeText(CaptureActivity.this, "解析错误，请选择正确的二维码图片",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (ChecksumException e) {
			Toast.makeText(CaptureActivity.this, "解析错误，请选择正确的二维码图片",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (FormatException e) {
			Toast.makeText(CaptureActivity.this, "解析错误，请选择正确的二维码图片",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		return null;
	}

//	// 是否开启闪光灯
//	public void IfOpenLight(View v) {
//		ifOpenLight++;
//
//		switch (ifOpenLight % 2) {
//		case 0:
//			//关闪光灯
//			CameraManager.get().closeLight();
//			btnLight.setText(getString(R.string.str_open_light));
//			break;
//		case 1:
//			//开闪光灯
//			CameraManager.get().openLight();
//			btnLight.setText(getString(R.string.str_close_light));
//			break;
//		default:
//			break;
//		}
//	}

	@NeedsPermission(Manifest.permission.CAMERA)
	public void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
//			initCamera(holder);
			CaptureActivityPermissionsDispatcher.initCameraWithCheck(CaptureActivity.this,holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {

			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.qrcode);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}