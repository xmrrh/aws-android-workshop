package com.example.socialandroidapp;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "aws-dev-auth";
    private EditText userid, userpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_layout);

    }

    @Override
    protected void onResume() {
        super.onResume();
        AWSMobileClient.getInstance().initialize(getApplicationContext(), initListenerCallback);

    }

    Callback<UserStateDetails> initListenerCallback = new Callback<UserStateDetails>() {

        @Override
        public void onResult(UserStateDetails userStateDetails) {
            Log.i(TAG, "onResult: " + userStateDetails.getUserState());
            switch (userStateDetails.getUserState()) {
                case SIGNED_IN:
                    //AWSMobileClient.getInstance().signOut();
                    goMainActivity();
                    break;
                case SIGNED_OUT:
                    //break;
                default:
                    //AWSMobileClient.getInstance().signOut();
                    showSignIn();
                    break;
            }
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "Initialization error.", e);
            AWSMobileClient.getInstance().initialize(getApplicationContext(), initListenerCallback);
        }
    };

    public void goMainActivity() {


        try {
            Log.e(TAG, " AWSMobileClient.getInstance().getAWSCredentials()=" +
                    AWSMobileClient.getInstance().getAWSCredentials());
            Log.e(TAG, " AWSMobileClient.getInstance().getConfiguration()=" +
                    AWSMobileClient.getInstance().getConfiguration());

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showSignIn() {
        try {
            Log.e(TAG, "Show singIN" );
            finish();
            AWSMobileClient.getInstance().showSignIn(this,
                    SignInUIOptions.builder().logo(R.drawable.devday).backgroundColor(Color.BLUE).//hostedUIOptions()
                            nextActivity(MainActivity.class).build());//, lisenercallback);
        } catch (Exception e) {
            Log.e(TAG, "Show singIN" + e.toString());
        }
    }


}