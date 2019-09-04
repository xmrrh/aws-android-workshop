package com.example.socialandroidapp;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.S3ObjectManagerImplementation;
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;

public class ClientFactory {

    private static AWSAppSyncClient mAWSAppSyncClient;

    public static void appSyncInit(Context context) {
        if (mAWSAppSyncClient == null) {
            mAWSAppSyncClient = AWSAppSyncClient.builder()
                    .context(context)
                    .awsConfiguration(new AWSConfiguration(context))
                    .cognitoUserPoolsAuthProvider(new CognitoUserPoolsAuthProvider() {
                        @Override
                        public String getLatestAuthToken() {
                            try {

                                return AWSMobileClient.getInstance().getTokens().getIdToken().getTokenString();
                            } catch (Exception e) {
                                Log.e("APPSYNC_ERROR", e.getLocalizedMessage());
                                return e.getLocalizedMessage();
                            }
                        }
                    }).s3ObjectManager(getS3ObjectManager(context)).build();

        }
    }

    public static AWSAppSyncClient getAppSyncClient() {
        return mAWSAppSyncClient;
    }

    private static S3ObjectManagerImplementation s3ObjectManager;

    // Copy the below two methods and add the .s3ObjectManager builder parameter
    // initialize and fetch the S3 Client
    public static S3ObjectManagerImplementation getS3ObjectManager(final Context context) {
        if (s3ObjectManager == null) {


            AmazonS3Client s3Client = new AmazonS3Client(ClientFactory.getCredentialsProvider(context));
            s3Client.setRegion(Region.getRegion("us-east-1")); // you can set the region of bucket here
            s3ObjectManager = new S3ObjectManagerImplementation(s3Client);
        }
        return s3ObjectManager;
    }

    // initialize and fetch cognito credentials provider for S3 Object Manager
    public static AWSCredentialsProvider getCredentialsProvider(final Context context) {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context, AWSMobileClient.getInstance().getConfiguration()
        );
        return credentialsProvider;
    }

    public static String getUserID() {
        return AWSMobileClient.getInstance().getUsername();
    }

    public static AWSCredentials getAWSCredentials()  {
        AWSCredentials awsCredentials = null;
        try {
            awsCredentials = AWSMobileClient.getInstance().getAWSCredentials();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return awsCredentials;
    }
}

