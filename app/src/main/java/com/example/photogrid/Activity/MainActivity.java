package com.example.photogrid.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.photogrid.R;
import com.example.photogrid.Utils.CameraPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout linearLayoutCamera;
    private ImageView imageViewCamera;
    private LinearLayout linearLayoutGrid;
    private ImageView imageViewGrid;
    private LinearLayout linearLayoutEdit;
    private ImageView imageViewEdit;
    public static final int CAMERA_PERMISSION = 111;
    public static final int GALLERY_PERMISSION = 112;
    private static final String TAG = "MainActivity";
    public static int count = 0;
    private final String dir = Environment.getExternalStorageDirectory() + "/mPhotoGrid/";
    Bitmap bitmap = null;

    public static Uri selectedImageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        init();
    }
    private void init() {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }

        linearLayoutCamera = (LinearLayout)findViewById( R.id.linearLayoutCamera );
        imageViewCamera = (ImageView)findViewById( R.id.imageViewCamera );
        linearLayoutGrid = (LinearLayout)findViewById( R.id.linearLayoutGrid );
        imageViewGrid = (ImageView)findViewById( R.id.imageViewGrid );
        linearLayoutEdit = (LinearLayout)findViewById( R.id.linearLayoutEdit );
        imageViewEdit = (ImageView)findViewById( R.id.imageViewEdit );

        linearLayoutCamera.setOnClickListener(this);
        linearLayoutEdit.setOnClickListener(this);
        linearLayoutGrid.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayoutCamera:
                getImageFromCamera();
                break;
            case R.id.linearLayoutGrid:

                break;
            case R.id.linearLayoutEdit:
                getImageFromGalleryToEdit();
                break;
            default:
                break;
        }

    }
    public void getImageFromCamera(){
        CameraPermissions permissions = new CameraPermissions(this);
        if (!permissions.checkPermissionForCamera()) {
            permissions.requestPermissionForCamera();
        } else {
            if (!permissions.checkPermissionForExternalStorage()) {
                permissions.requestPermissionForExternalStorage();
            } else {
                count++;
                File file = new File(dir + count + ".jpg");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(file);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraIntent, CAMERA_PERMISSION);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CameraPermissions.CAMERA_PERMISSION_REQUEST_CODE || requestCode == CameraPermissions.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    getImageFromCamera();
                }
            }
        }
    }
public void getImageFromGalleryToEdit(){
    CameraPermissions permissions = new CameraPermissions(this);
    if (!permissions.checkPermissionForExternalRead()) {
        permissions.requestPermissionForExternalRead();
    } else {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_PERMISSION);

    }

}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PERMISSION) {
            try {
                bitmap = BitmapFactory.decodeFile(dir + count + ".jpg");
                String myBase64Image=bitmapToBase64(bitmap);
                String finalImage=resizeBase64Image(myBase64Image);
                Intent nextIntent=new Intent(MainActivity.this,FullScreenImageActivity.class);
                nextIntent.putExtra("cameraImage",finalImage);
                startActivity(nextIntent);
                finish();
                Log.d(TAG, "Image sent to next Activity");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Failed!!");
            }
        }
        if (resultCode == RESULT_OK && requestCode == GALLERY_PERMISSION) {
            try {
                selectedImageUri = data.getData();
                String imagepath = getPath(selectedImageUri);
                bitmap = BitmapFactory.decodeFile(imagepath);
                String myBase64Image=bitmapToBase64(bitmap);
                String finalImage=resizeBase64Image(myBase64Image);
                Intent nextIntent=new Intent(MainActivity.this,FullScreenImageActivity.class);
                nextIntent.putExtra("cameraImage",finalImage);
                startActivity(nextIntent);
                Log.d(TAG, "Profile Image Changed from gallery" + imagepath);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to update Image");
            }
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String bitmapToBase64(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);

    }


    public static Bitmap base64ToBitmap(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String resizeBase64Image(String base64image) {
        byte[] encodeByte = Base64.decode(base64image.getBytes(), Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);


        if (image.getHeight() <= 400 && image.getWidth() <= 400) {
            return base64image;
        }
        image = Bitmap.createScaledBitmap(image, 300, 300, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);

    }

}
