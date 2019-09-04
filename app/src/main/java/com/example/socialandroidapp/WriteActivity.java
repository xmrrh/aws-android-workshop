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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.PutPostWithPhotoMutation;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.annotation.Nonnull;

import type.S3ObjectInput;

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
        //appsync
        ClientFactory.appSyncInit(getApplicationContext());

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

                //WriteActivity.this.finish();
                addComment();

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


    //appsync upload
    private final String putYourBucketName = "xmrrh-east-1";
    private final String mimeType = "image/jpg";
    private final String region = "xmrrh-east-1";
    private final String folderName = "public/";

    private void addComment() {

        showWaitDialog();

        S3ObjectInput s3ObjectInput = S3ObjectInput.builder()
                .bucket(putYourBucketName)
                .key(folderName + UUID.randomUUID().toString())
                .region(region)
                .localUri(bitmapPath)
                .mimeType(mimeType).build();
        PutPostWithPhotoMutation addPostMutation = PutPostWithPhotoMutation.builder()
                .title(title.getText().toString())
                .author(ClientFactory.getUserID())
                .url(bitmapPath)
                .content(contents.getText().toString())
                .ups(0)
                .downs(0)
                .photo(s3ObjectInput)
                .id(UUID.randomUUID().toString())
                .build();

        ClientFactory.getAppSyncClient().mutate(addPostMutation).enqueue(postsCallback);
    }
    // Mutation callback code
    private GraphQLCall.Callback<PutPostWithPhotoMutation.Data> postsCallback = new GraphQLCall.Callback<PutPostWithPhotoMutation.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<PutPostWithPhotoMutation.Data> response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissWaitDialog();
                    WriteActivity.this.finish();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull final ApolloException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissWaitDialog();

                    Log.e("", "Failed to perform AddPostMutation", e);
                    WriteActivity.this.finish();
                }
            });
        }
    };
}
