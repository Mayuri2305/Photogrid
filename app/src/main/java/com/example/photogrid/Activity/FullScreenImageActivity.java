package com.example.photogrid.Activity;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.photogrid.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by mayuri on 14/9/17.
 */

public class FullScreenImageActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageViewFullScreen;
    ImageView imageViewClose;
    ImageView imageViewSave;
    ImageView imageViewCrop;
    ImageView imageViewRotateLeft;
    ImageView imageViewRotateRight;
    String imagePath;
    Bitmap bitmap;
    Bitmap rotatedBitmap = null;
    public static final int CROP_IMAGE_PERMISSION = 113;
    private static final String TAG = "FullScreen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        imageViewFullScreen = (ImageView) findViewById(R.id.imageViewFullScreenImage);
        imageViewClose = (ImageView) findViewById(R.id.imageViewClose);
        imageViewSave = (ImageView) findViewById(R.id.imageViewSave);
        imageViewCrop = (ImageView) findViewById(R.id.imageViewCrop);
        imageViewRotateLeft = (ImageView) findViewById(R.id.imageViewRotateLeft);
        imageViewRotateRight = (ImageView) findViewById(R.id.imageViewRotateRight);
        imagePath = getIntent().getStringExtra("cameraImage");
        bitmap = base64ToBitmap(imagePath);
        imageViewFullScreen.setImageBitmap(bitmap);
        imageViewClose.setOnClickListener(this);
        imageViewCrop.setOnClickListener(this);
        imageViewSave.setOnClickListener(this);
        imageViewRotateLeft.setOnClickListener(this);
        imageViewRotateRight.setOnClickListener(this);


    }

    public static Bitmap base64ToBitmap(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewClose:
                startActivity(new Intent(FullScreenImageActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.imageViewSave:
                saveImage();
                break;
            case R.id.imageViewCrop:
                getCropImage();
                break;
            case R.id.imageViewRotateLeft:
                getRotateLeftImage();
                break;
            case R.id.imageViewRotateRight:
                getRotateRightImage();
                break;
            default:
                break;
        }

    }

    public void saveImage() {

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void getCropImage() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cropIntent.setDataAndType(getImageUri(this,bitmap), "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, CROP_IMAGE_PERMISSION);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void getRotateLeftImage() {


        rotatedBitmap = rotateImage(bitmap, -90);
        imageViewFullScreen.setImageBitmap(rotatedBitmap);


    }

    public void getRotateRightImage() {
        rotatedBitmap = rotateImage(bitmap, 90);
        imageViewFullScreen.setImageBitmap(rotatedBitmap);

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CROP_IMAGE_PERMISSION) {
            try {
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
               // ImageView picView = (ImageView) findViewById(R.id.picture);
                imageViewFullScreen.setImageBitmap(thePic);
                finish();
                Log.d(TAG, "Crop Image Successfully");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Failed!!");
            }
        }


    }
}
