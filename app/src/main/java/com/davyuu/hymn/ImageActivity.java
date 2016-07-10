package com.davyuu.hymn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        int imageId = i.getIntExtra("imageId", 0);
        setTitle(name);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(imageId);
    }
}
