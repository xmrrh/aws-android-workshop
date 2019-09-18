package com.example.socialandroidapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class WriteActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_ALBUM = 1004;
    private static final String TAG = "dev-day-write";

    private EditText contents, title;
    private ProgressDialog dialog;
    private String photoPath, bitmapPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);


        Button cancelBtn = findViewById(R.id.cancel);
        Button saveBtn = findViewById(R.id.save);
        Button uploadBtn = findViewById(R.id.upload);

        contents = findViewById(R.id.contents);
        title = findViewById(R.id.title);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissWaitDialog();

                Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmapPath == null) {
                    Toast.makeText(getApplicationContext(), getString(R.string.warning_picture), Toast.LENGTH_SHORT).show();
                    return;
                }

                WriteActivity.this.finish();

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        showFileChooser();
                    }
                }.start();
            }
        });

    }


    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {

                    Uri photoUri = data.getData();

                    Cursor cursor = null;
                    String picturePath;
                    try {

                        String[] proj = {MediaStore.Images.Media.DATA};
                        cursor = getContentResolver().query(photoUri, proj, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        cursor.moveToFirst();

                        picturePath = cursor.getString(column_index);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }

                    photoPath = picturePath;
                    setImage();

                    break;
                }
        }
    }
    private void showWaitDialog() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait..");
        dialog.show();
    }

    private void dismissWaitDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
    private void setImage() {

        ImageView imageView = findViewById(R.id.picture);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(photoPath, options);
        Bitmap rotatedBitmap = null;

        File fileCacheItem = new File(getApplicationContext().getFilesDir(), UUID.randomUUID().toString());
        bitmapPath = fileCacheItem.getAbsolutePath();

        try {

            int height = originalBm.getHeight();
            int width = originalBm.getWidth();

            ExifInterface eif = new ExifInterface(photoPath);

            int exifOrientation = eif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = Util.controlDegrees(exifOrientation);


            Matrix m = new Matrix();
            m.setRotate(exifDegree, 300, height / (width / 300));

            OutputStream out = new FileOutputStream(fileCacheItem);
            Bitmap bitmap = Bitmap.createScaledBitmap(originalBm, 300,height / (width / 300), true);
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);


        } catch (Exception e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(rotatedBitmap);

    }
}
