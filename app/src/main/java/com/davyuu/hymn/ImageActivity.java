package com.davyuu.hymn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        int imageId = i.getIntExtra("imageId", 0);
        setTitle(name);

        TouchImageView image = new TouchImageView(this);
        image.setImageResource(imageId);
        image.setMaxZoom(4f);
        setContentView(image);
    }
}
