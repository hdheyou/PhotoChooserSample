package com.tianma.photochooser;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianma.photochooser.image.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHOOSE_IMAGE = 0x01;
    private static final int REQUEST_WRITE_PERMISSION_GRANT = 0x02;

    private TextView photoPath;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.choose_image_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                if (permissionCheck(MainActivity.this, WRITE_PERMISSION)) {
                    startSelectPhoto();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_PERMISSION}, REQUEST_WRITE_PERMISSION_GRANT);
                }
            }
        });
        photoPath = (TextView) findViewById(R.id.photo_path);
        photo = (ImageView) findViewById(R.id.photo);
    }

    private boolean permissionCheck(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void startSelectPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            Uri uri =  data.getData();
            String path = ImageUtils.getRealPathFromUri(this, uri);
            if (!TextUtils.isEmpty(path)) {
                photoPath.setVisibility(View.VISIBLE);
                photoPath.setText(path);
                int requiredHeight = photo.getHeight();
                int requiredWidth = photo.getWidth();
                Bitmap bm = ImageUtils.decodeSampledBitmapFromDisk(path, requiredWidth, requiredHeight);
                photo.setImageBitmap(bm);
            } else {
                Toast.makeText(this, "Fail to get image", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION_GRANT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startSelectPhoto();
                } else {
                    Toast.makeText(MainActivity.this, "You denied the write_external_storage permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
