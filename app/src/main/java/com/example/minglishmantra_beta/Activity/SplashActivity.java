package com.example.minglishmantra_beta.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.minglishmantra_beta.ApplicationClass;
import com.example.minglishmantra_beta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {


    ProgressBar progressBar;
    ApplicationClass applicationClass;

    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private HashMap<String, Object> firebaseDefaultMap;
    public static final String VERSION_CODE_KEY = "latest_app_version";


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mAuth =FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().getRoot();


        if(mAuth.getCurrentUser() == null){

            Intent intent =new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;

        }



        checkForUpdate();


    }




    private void checkCourses(){

        FirebaseUser user = mAuth.getCurrentUser();
        applicationClass = (ApplicationClass) getApplicationContext();
        applicationClass.setUserName(user.getDisplayName());
        applicationClass.setImageUrl(String.valueOf(user.getPhotoUrl()));
        applicationClass.setMobileNumber(user.getPhoneNumber());

        mRef.child("student").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() !=0) {

                    applicationClass.getEnrolledCourse().clear();


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        final String courseName = snapshot.getKey();
                        applicationClass.getEnrolledCourse().add(courseName);

                    }

                }

                Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});

    }



    private void checkForUpdate() {
        firebaseDefaultMap = new HashMap<>();
        firebaseDefaultMap.put(VERSION_CODE_KEY, getCurrentVersionCode());
        mFirebaseRemoteConfig.setDefaults(firebaseDefaultMap);

        mFirebaseRemoteConfig.setConfigSettings(
                new FirebaseRemoteConfigSettings.Builder()
                        .build());
        mFirebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFirebaseRemoteConfig.activateFetched();
                    //Log.d(TAG, "Fetched value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));
                    //calling function to check if new version is available or not
                    checkForUpdateAvailability();
                } else {

                    checkCourses();

                }
            }
        });

        // Log.d(TAG, "Default value: " + mFirebaseRemoteConfig.getString(VERSION_CODE_KEY));
    }



    private void checkForUpdateAvailability() {
        int latestAppVersion = (int) mFirebaseRemoteConfig.getDouble(VERSION_CODE_KEY);
        if (latestAppVersion > getCurrentVersionCode()) {
            new AlertDialog.Builder(this).setTitle("Please Update the App")
                    .setMessage("A new version of this app is available. Please update it").setPositiveButton(
                    "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=com.nexm.lucidity")));

                        }
                    }).
                    setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();

                        }
                    }).setCancelable(false).show();
        } else {
            // Toast.makeText(this,"This app is already up to date", Toast.LENGTH_SHORT).show();

            checkCourses();


        }
    }








    private int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
