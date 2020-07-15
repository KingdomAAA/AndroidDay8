package com.example.day8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST_CODE = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        open(R.id.btn_camera, ImageActivity.class);
        open(R.id.btn_video, VideoActivity.class);
        open(R.id.btn_my, MyCameraActivity.class);
    }

    String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean checkPermission(String[] spermissions){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            for(String permission:spermissions){
                if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, spermissions,PERMISSION_REQUEST_CODE);
                    return false;
                }
            }
        }
        return true;
    }

    private void open(int buttonId, final Class<?> clz) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermission(permissions)) return;
                startActivity(new Intent(MainActivity.this, clz));
            }
        });
    }
}
