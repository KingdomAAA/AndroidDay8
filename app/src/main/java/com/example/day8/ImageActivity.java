package com.example.day8;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_TAKE_PHOTO = 666;

    ImageView imageView;
    private String takeImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.iv);
        openCamera();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeImagePath = getOutputMediaPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(ImageActivity.this, takeImagePath));
        if(intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }



    private String getOutputMediaPath(){
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir,"IMG_"+timestamp+".jpg");
        if(!mediaFile.exists()){
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    public static Uri getUriForFile(Context context, String path) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context.getApplicationContext(), context.getApplicationContext().getPackageName() + ".fileprovider", new File(path));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK){
            int targetWidth = imageView.getWidth();
            int targetHeight = imageView.getHeight();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(takeImagePath, options);
            int photoWidth = options.outWidth;
            int photoHeight = options.outHeight;
            int scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            Bitmap bitmap = BitmapFactory.decodeFile(takeImagePath, options);
            imageView.setImageBitmap(bitmap);
        }
    }
}
