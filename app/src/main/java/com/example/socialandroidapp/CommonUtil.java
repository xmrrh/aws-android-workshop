package com.example.socialandroidapp;

import android.content.Context;
import android.widget.Toast;

public class CommonUtil {
    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
