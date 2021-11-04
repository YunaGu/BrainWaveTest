package com.geekb.brainwavetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gyf.immersionbar.ImmersionBar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isServiceRunning()){
            intent = new Intent(this,BlueService.class);
            startService(intent);
        }
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor("#010101").init();
        Button startTest =  findViewById(R.id.bt_test);
        EditText editText =  findViewById(R.id.et_info);
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TestActivity.class);
                intent.putExtra("info",editText.getText().toString());
                startActivity(intent);
            }
        });
    }

    boolean isServiceRunning(){
        boolean running = false;
        List<ActivityManager.RunningServiceInfo> serviceInfos = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(30);
        if (serviceInfos.size()<=0){
            return false;
        }
        for (int i = 0; i < serviceInfos.size(); i++) {
            if (serviceInfos.get(i).service.getClassName().equals("BlueService")){
                running = true;
                break;
            }
        }
        return running;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }
}