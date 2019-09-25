package com.example.socialandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.HostedUIOptions;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;

public class AuthMainActivity extends AppCompatActivity {

    private static final String TAG = AuthMainActivity.class.getSimpleName();

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);

        context = this;

        CommonAction.checkSession(this, true);
    }

    private void _openFacebookLogin() {
        // Add code here

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Add code here

    }

    public void openLogin(View view) {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    public void openRegistration(View view) {
        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
    }

    public void openFacebookLogin(View view) {
        _openFacebookLogin();
    }

}
