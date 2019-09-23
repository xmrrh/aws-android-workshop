package com.example.socialandroidapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails;
import com.example.socialandroidapp.fragments.SignUpConfirmFragment;
import com.example.socialandroidapp.fragments.SignUpFragment;

import java.util.HashMap;
import java.util.Map;

import static com.example.socialandroidapp.CommonUtil.makeToast;


public class SignUpActivity extends FragmentActivity
        implements SignUpFragment.OnFragmentInteractionListener,
        SignUpConfirmFragment.OnFragmentInteractionListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private String userName, password;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setSignUpFragment();
        context = this;
    }

    private void setSignUpFragment() {
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.signup_layout, signUpFragment);
        transaction.commit();
    }

    private void setSignUpConfirmFragment() {
        SignUpConfirmFragment signUpConfirmFragment = new SignUpConfirmFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.signup_layout, signUpConfirmFragment);
        transaction.commit();
    }

    @Override
    public void signUp(String email, String password) {
        userName = email;
        this.password = password;

        // Add code here

    }

    @Override
    public void confirmSignUp(String code) {
        // Add code here

    }

    private void _signIn(String username, String password) {
        // Add code here

    }
}
