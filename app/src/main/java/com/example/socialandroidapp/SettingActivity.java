package com.example.socialandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;


public class SettingActivity extends AppCompatActivity {


    private Spinner customergrade, translatedlanguage;
    private String TAG = "dev-day-setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        Button cancelBtn = findViewById(R.id.cancel);
        Button saveBtn = findViewById(R.id.save);
        Button btnLogout = findViewById(R.id.btnLogout);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();

                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        setSpinnerDestinationLanguage();
        setSpinnerCustomerGrade();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AWSMobileClient.getInstance().signOut();
                CommonAction.openAuthMain(SettingActivity.this);
            }
        });
    }

    private void saveSettings() {
        Util.setPreference(getApplicationContext(), "preferlanguage", String.valueOf(Util.nPreferLanguage));
        Util.setPreference(getApplicationContext(), "grade", String.valueOf(Util.nGrade));
    }

    private void setSpinnerCustomerGrade() {

        customergrade = findViewById(R.id.customergrade);
        customergrade.setSelection(Integer.valueOf(Util.getPreferenceString(getApplicationContext(), "grade", "0")));
        customergrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Util.nGrade = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void setSpinnerDestinationLanguage() {
        translatedlanguage = findViewById(R.id.languagelist);
        translatedlanguage.setSelection(Integer.valueOf(Util.getPreferenceString(getApplicationContext(), "preferlanguage", "7")));//english
        translatedlanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Util.nPreferLanguage = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    //////////////
/*

    private void queryCurrentSettings() {

        ModelStringFilterInput filter = ModelStringFilterInput.builder().eq(Util.getUserID()).build();
        ModelSettingFilterInput settingFilter = ModelSettingFilterInput.builder().userid(filter).build();
        Util.getAppSyncClient().query(ListSettingsQuery.builder()
                .filter(settingFilter).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(settingQueryCallback);
    }

    private GraphQLCall.Callback<ListSettingsQuery.Data> settingQueryCallback = new GraphQLCall.Callback<ListSettingsQuery.Data>() {

        @Override
        public void onResponse(@Nonnull Response<ListSettingsQuery.Data> response) {
            if(response.data().listSettings().items().size()>0){
                //Log.e(TAG, "a =" +response.data().listSettings().items().get(0));
                Util.nGrade = Integer.parseInt(response.data().listSettings().items().get(0).grade());
                Util.translateCountryCode = response.data().listSettings().items().get(0).preferLanguage();
                Util.ID = response.data().listSettings().items().get(0).id();

            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, "query error = "+e.toString());
        }
    };

    private void saveSettings() {



        setPreference("preferlanguage", String.valueOf(Util.nPreferLanguage));
        setPreference("grade", String.valueOf(Util.nGrade));
//-
        if(Util.ID == null){
            CreateSettingInput input = CreateSettingInput.builder().grade(String.valueOf(Util.nGrade)).userid(Util.getUserID()).
                    preferLanguage(Util.translateCountryCode).build();
            CreateSettingMutation addSetting = CreateSettingMutation.builder().input(input).build();
            Util.getAppSyncClient().mutate(addSetting).enqueue(settingMutateCallback);
        }else {
            UpdateSettingInput input = UpdateSettingInput.builder().grade(String.valueOf(Util.nGrade)).userid(Util.getUserID()).id(Util.ID).
                    preferLanguage(Util.translateCountryCode).build();
            UpdateSettingMutation updateSetting = UpdateSettingMutation.builder().input(input).build();
            Util.getAppSyncClient().mutate(updateSetting).enqueue(settingUpdateMutateCallback);
        }
//-
    }
    private GraphQLCall.Callback<UpdateSettingMutation.Data> settingUpdateMutateCallback = new GraphQLCall.Callback<UpdateSettingMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<UpdateSettingMutation.Data> response) {
            Log.e(TAG, "update ok");

        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, "update error = "+e.toString());

        }
    };


    private GraphQLCall.Callback<CreateSettingMutation.Data> settingMutateCallback = new GraphQLCall.Callback<CreateSettingMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateSettingMutation.Data> response) {

        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, "insert error = "+e.toString());
        }
    };

*/


}
