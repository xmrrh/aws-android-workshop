package com.example.socialandroidapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;

public class SplashActivity extends AppCompatActivity {

    private final static String TAG = SplashActivity.class.getSimpleName();

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        _initCognito();
    }

    private void _initCognito() {
        // Add code here
        if (AWSMobileClient.getInstance().getConfiguration() == null){
            // Initialize user
            AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    switch (userStateDetails.getUserState()){
                        case SIGNED_IN:
                            // Open Main Activity
                            CommonAction.openMain(context);
                            break;
                        case SIGNED_OUT:
                            Log.d(TAG, "Do nothing yet");
                            CommonAction.openAuthMain(context);
                            break;
                        default:
                            AWSMobileClient.getInstance().signOut();
                            break;
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", e.toString());
                }
            });

        } else if (AWSMobileClient.getInstance().isSignedIn()){
            // Logined user
            CommonAction.openMain(context);
        } else {
            // Logouted user
            CommonAction.openAuthMain(context);
        }

    }


}
