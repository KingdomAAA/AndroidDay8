package com.example.day8;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.hardware.Camera;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    Camera mCamera;
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    ImageView mImageView;
    VideoView mVideoView;
    MediaRecorder mMediaRecorder;

    Button btn_camera;
    Button btn_video;

    private String mp4Path;
    boolean isRecording = false;
    Camera.PictureCallback mPictureCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        btn_camera = findViewById(R.id.btn_camera);
        btn_video = findViewById(R.id.btn_video);
        mImageView = findViewById(R.id.iv);
        mVideoView = findViewById(R.id.vv);
        mSurfaceView = findViewById(R.id.sv);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);

        initCamera();

        mPictureCallback = new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream fos = null;
                String filepath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+ File.separator+".jpg";
                File file = new File(filepath);
                try{
                    fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.flush();
                    Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                    mImageView.setVisibility(View.VISIBLE);
                    mVideoView.setVisibility(View.GONE);
                    mImageView.setImageBitmap(bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    mCamera.startPreview();
                    if(fos != null){
                        try{
                            fos.close();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null,null,mPictureCallback);
            }
        });

        btn_video.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                record();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCamera == null)
            initCamera();
        mCamera.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    private void initCamera(){
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.set("orientation","portrait");
        parameters.set("rotation",90);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
    }

    private boolean prepareVideoRecoder(){
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mp4Path = getOutputMediaPath();
        mMediaRecorder.setOutputFile(mp4Path);
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setOrientationHint(90);
        try{
            mMediaRecorder.prepare();
        }catch (Exception e){
            mMediaRecorder.release();
            return false;
        }
        return true;
    }

    private String getOutputMediaPath(){
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir,"VIDEO_"+timestamp+".mp4");
        if(!mediaFile.exists()){
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    private void record(){
        if(isRecording){
            btn_video.setText("开始");
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder=null;
            mCamera.lock();

            mVideoView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
            mVideoView.setVideoPath(mp4Path);
            mVideoView.start();
            isRecording=false;
        }else {
            if(prepareVideoRecoder()){
                btn_video.setText("暂停");
                isRecording=true;
                mMediaRecorder.start();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(holder.getSurface() == null){
            return ;
        }
        mCamera.stopPreview();
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}
