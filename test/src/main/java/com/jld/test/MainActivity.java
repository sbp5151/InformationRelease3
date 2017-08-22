package com.jld.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent("jld_adjust_time");
                intent.putExtra("adjust_time", System.currentTimeMillis() + "");
                sendBroadcast(intent);
            }
        });
        new ShowDownReceive();
    }
    class ShowDownReceive extends BroadcastReceiver {
        public ShowDownReceive() {
            IntentFilter inf = new IntentFilter("show_shutdown_dialog");
            registerReceiver(this,inf);
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "-----------------onReceive---------------------");
        }
    }
}
