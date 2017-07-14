package com.tianma.photochooser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianma.photochooser.image.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHOOSE_IMAGE = 0x01;

    private static final int REQUEST_WRITE_EXTERNAL_PERMISSION_GRANT = 0xff;

    private TextView photoPath;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.choose_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareToOpenAlbum();
            }
        });
        photoPath = (TextView) findViewById(R.id.photo_path);
        photo = (ImageView) findViewById(R.id.photo);
    }

    private void prepareToOpenAlbum() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_PERMISSION_GRANT);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_PERMISSION_GRANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(MainActivity.this, "You denied the write_external_storage permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            Uri uri =  data.getData();
            Log.d("Tianma", "Uri = " + uri);
            String path = ImageUtils.getRealPathFromUri(this, uri);
            Log.d("Tianma", "realPath = " + path);

            photoPath.setVisibility(View.VISIBLE);
            photoPath.setText(path);
            int requiredHeight = photo.getHeight();
            int requiredWidth = photo.getWidth();
            Bitmap bm = ImageUtils.decodeSampledBitmapFromDisk(path, requiredWidth, requiredHeight);
            photo.setImageBitmap(bm);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
