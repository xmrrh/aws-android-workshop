package com.example.socialandroidapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.amazonaws.mobile.client.results.SignInResult;

public class CommonAction {
    public static void openMain(Context context){
        CommonAction.openActivityOnTop(context, MainActivity.class);
    }

    public static void openAuthMain(Context context){
        CommonAction.openActivityOnTop(context, AuthMainActivity.class);
    }

    public static void openActivityOnTop(Context context, Class targetClass) {
        Intent intent=new Intent(context, targetClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void initCognito(Context context) {
        // Add code here
        AWSMobileClient.getInstance().initialize(context.getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case SIGNED_IN:
                        // Open Main Activity
                        CommonAction.openMain(context);
                        break;
                    default:
                        AWSMobileClient.getInstance().signOut();
                        CommonAction.openAuthMain(context);
                        break;
                }


            }

            @Override
            public void onError(Exception e) {
                Log.e("INIT", e.toString());
            }
        });
    }

    public static void checkSession(Context context) {
        // Add code here
        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case SIGNED_OUT:
                        // user clicked signout button and signedout
                        Log.i("userState", "user signed out");
                        break;
                    case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                        Log.i("userState", "need to login again.");
                        //Alternatively call .showSignIn()
                        CommonAction.openAuthMain(context);
                        break;
                    default:
                        Log.i("userState", "unsupported");
                }
            }
        });
    }

}
