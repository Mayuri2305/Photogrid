package com.example.photogrid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout linearLayoutCamera;
    private ImageView imageViewCamera;
    private LinearLayout linearLayoutGrid;
    private ImageView imageViewGrid;
    private LinearLayout linearLayoutEdit;
    private ImageView imageViewEdit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {
        linearLayoutCamera = (LinearLayout)findViewById( R.id.linearLayoutCamera );
        imageViewCamera = (ImageView)findViewById( R.id.imageViewCamera );
        linearLayoutGrid = (LinearLayout)findViewById( R.id.linearLayoutGrid );
        imageViewGrid = (ImageView)findViewById( R.id.imageViewGrid );
        linearLayoutEdit = (LinearLayout)findViewById( R.id.linearLayoutEdit );
        imageViewEdit = (ImageView)findViewById( R.id.imageViewEdit );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayoutCamera:
                break;
            case R.id.linearLayoutGrid:
                break;
            case R.id.linearLayoutEdit:
                break;
            default:
                break;
        }

    }
}
