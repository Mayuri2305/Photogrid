package com.example.photogrid.Activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.example.photogrid.R;

/**
 * Created by mayuri on 14/9/17.
 */

public class FullScreenImageActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView fullScreenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        fullScreenImageView = (ImageView) findViewById(R.id.imageViewFullScreenImage);
        String imagePath = getIntent().getStringExtra("cameraImage");
        Bitmap bitmap = base64ToBitmap(imagePath);
        fullScreenImageView.setImageBitmap(bitmap);

    }

    public static Bitmap base64ToBitmap(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public void onClick(View v) {

    }
}
