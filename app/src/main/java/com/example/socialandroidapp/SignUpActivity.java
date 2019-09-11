package com.example.socialandroidapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

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
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("email", email);
        AWSMobileClient.getInstance().signUp(userName, password, attributes, null, new Callback<SignUpResult>() {
            @Override
            public void onResult(final SignUpResult signUpResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-up callback state: " + signUpResult.getConfirmationState());
                        if (!signUpResult.getConfirmationState()) {
                            final UserCodeDeliveryDetails details = signUpResult.getUserCodeDeliveryDetails();
                            //makeToast("Confirm sign-up with: " + details.getDestination());
                            Log.d(TAG, "Confirm sign-up with: " + details.getDestination());
//                            _signIn(userName, password);
                            setSignUpConfirmFragment();
                        } else {
//                            makeToast("Sign-up done.");
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Sign-up error", e);
            }
        });
    }

    public void confirmSignIn(String signInChallengeResponse) {
        AWSMobileClient.getInstance().confirmSignIn(signInChallengeResponse, new Callback<SignInResult>() {
            @Override
            public void onResult(SignInResult signInResult) {
                Log.d(TAG, "Sign-in callback state: " + signInResult.getSignInState());
                switch (signInResult.getSignInState()) {
                    case DONE:
//                        makeToast("Sign-in done.");
                        Toast.makeText(context, "Sign-in done.", Toast.LENGTH_SHORT).show();
                        CommonAction.openMain(context);
                        break;
                    case SMS_MFA:
//                        makeToast("Please confirm sign-in with SMS.");
                        Toast.makeText(context, "Please confirm sign-in with SMS.", Toast.LENGTH_SHORT).show();
                        break;
                    case NEW_PASSWORD_REQUIRED:
//                        makeToast("Please confirm sign-in with new password.");
                        Toast.makeText(context, "Please confirm sign-in with new password.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
//                        makeToast("Unsupported sign-in confirmation: " + signInResult.getSignInState());
                        Toast.makeText(context, "Unsupported sign-in confirmation: " + signInResult.getSignInState(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Sign-in error", e);
            }
        });
    }

    @Override
    public void confirmSignUp(String code) {
        AWSMobileClient.getInstance().confirmSignUp(userName, code, new Callback<SignUpResult>() {
            @Override
            public void onResult(final SignUpResult signUpResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-up callback state: " + signUpResult.getConfirmationState());
                        if (!signUpResult.getConfirmationState()) {
                            final UserCodeDeliveryDetails details = signUpResult.getUserCodeDeliveryDetails();
                            makeToast(context,"Confirm sign-up with: " + details.getDestination());
                        } else {
                            makeToast(context, "Sign-up done.");
                            _signIn(userName, password);
                            CommonAction.openMain(context);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Confirm sign-up error", e);
            }
        });
    }

    private void _signIn(String username, String password) {
        AWSMobileClient.getInstance().signIn(username, password, null, new Callback<SignInResult>() {
            @Override
            public void onResult(final SignInResult signInResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-in callback state: " + signInResult.getSignInState());
                        switch (signInResult.getSignInState()) {
                            case DONE:
                                makeToast(context, "Sign-in done.");
                                CommonAction.openMain(context);
                                break;
                            case SMS_MFA:
                                makeToast(context, "Please confirm sign-in with SMS.");
                                break;
                            case NEW_PASSWORD_REQUIRED:
                                makeToast(context, "Please confirm sign-in with new password.");
                                break;
                            default:
                                makeToast(context, "Unsupported sign-in confirmation: " + signInResult.getSignInState());
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Sign-in error", e);
            }
        });
    }
}
