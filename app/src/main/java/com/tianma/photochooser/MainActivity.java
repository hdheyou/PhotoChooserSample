package com.tianma.photochooser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianma.photochooser.image.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHOOSE_IMAGE = 0x01;

    private TextView photoPath;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.choose_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSelectPhoto();
            }
        });
        photoPath = (TextView) findViewById(R.id.photo_path);
        photo = (ImageView) findViewById(R.id.photo);
    }

    private void startSelectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHOOSE_IMAGE) {
            Uri uri =  data.getData();
            String path = ImageUtils.getRealPathFromUri(this, uri);
            photoPath.setVisibility(View.VISIBLE);
            photoPath.setText(path);
            int requiredHight = photo.getHeight();
            int requiredWidth = photo.getWidth();
            Bitmap bm = ImageUtils.decodeSampledBitmapFromDisk(path, requiredWidth, requiredHight);
            photo.setImageBitmap(bm);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
