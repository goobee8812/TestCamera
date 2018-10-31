package com.example.goobee_yuer.testcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    DragSurfaceView mSurfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //检查访问摄像头权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            //拥有权限
            initCameraView();
        }
    }

    private void initCameraView() {
        mSurfaceView = findViewById(R.id.surfaceview);
        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                initCameraView();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
//                openAppDetails();
            }
        }
    }

    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (camera == null){
                        camera = Camera.open(1);
                    }

                    camera.setPreviewDisplay(surfaceHolder);
                    camera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {
                        }
                    });
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
