package com.example.socialandroidapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.amazonaws.mobile.client.results.SignInResult;

import static com.example.socialandroidapp.CommonUtil.makeToast;

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

    public static void checkSession(Context context) {
        // Add code here
        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case SIGNED_IN:
                        Log.i("checkSession", "user signed in");
                        break;
                    default:
                        Log.i("checkSession", "unsupported");
                        makeToast(context, "Session is expired");
                        CommonAction.openAuthMain(context);
                        break;
                }
            }
        });
    }

}
