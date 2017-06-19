package com.jld.install;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int SEND_BRODDCAST = 0x11;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_BRODDCAST:
                    Intent intent = new Intent("install_and_start");
                    sendBroadcast(intent);
                    Toast.makeText(MainActivity.this, "发送广播", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button install = (Button) findViewById(R.id.bt_install);
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRoot()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String path = Environment.getExternalStorageDirectory().getPath();
                            boolean install = install(path + File.separator + "demo-debug.apk");
                            if (install) {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "安装成功", Toast.LENGTH_SHORT).show();
                                mHandler.sendEmptyMessageDelayed(SEND_BRODDCAST, 1000);//一秒后发送广播启动安装的应用
                                Looper.loop();
                            } else {
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "安装失败", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    }).start();
                } else
                    Toast.makeText(MainActivity.this, "请先root", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public boolean install(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d(TAG, "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 判断手机是否拥有Root权限。
     *
     * @return 有root权限返回true，否则返回false。
     */
    public boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

}
