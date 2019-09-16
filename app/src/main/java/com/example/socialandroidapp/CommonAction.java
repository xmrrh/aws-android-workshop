package com.example.socialandroidapp;

import android.content.Context;
import android.content.Intent;

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

}
