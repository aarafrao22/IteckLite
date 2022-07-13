package com.itecknologigroupofcompanies.itecklite;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class splash extends AppCompatActivity {
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private VideoView clip;
    String androidId, k;
    SharedPreferences sh;
    String Lloginid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(1024, 1024);

        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
         Lloginid = sh.getString("apploginid", "");
        Toast.makeText(this, Lloginid, Toast.LENGTH_SHORT).show();

        /** For device id**/
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);
        androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);// this is Android ID.


        /**Referencing Video. **/
        clip = (VideoView) findViewById(R.id.videoView);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(clip);


        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);

        clip.setVideoURI(uri);
        clip.start();


        /*clip.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                //Get VideoView's current width and height
                int videoViewWidth = clip.getWidth();
                int videoViewHeight = clip.getHeight();

                float xScale =5;
                float yScale =0.6f;

                //For Center Crop use the Math.max to calculate the scale
                //float scale = Math.max(xScale, yScale);
                //For Center Inside use the Math.min scale.
                //I prefer Center Inside so I am using Math.min
                float scale = Math.min(xScale, yScale);

                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;

                //Set the new size for the VideoView based on the dimensions of the video
                ViewGroup.LayoutParams layoutParams = clip.getLayoutParams();
                layoutParams.width = (int)scaledWidth;
                layoutParams.height = (int)scaledHeight;
                clip.setLayoutParams(layoutParams);
                clip.start();

            }

        });*/
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        checkConnection();

    }

    private void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (null != networkInfo) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goTologinActivity();
                }
            }, 1490);

        } else {
            showAlertDialogue("Make sure your internet is connected");
//            finish();
        }
//        finish();

    }

    private void goTologinActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatCount(Animation.INFINITE);
                anim.setDuration(700);

                final ImageView splash = (ImageView) findViewById(R.id.logo);
                splash.startAnimation(anim);
                splash.setAnimation(null);


                String fetchloginid = sh.getString("apploginid", "");
                k = fetchloginid.toString();

                postData(Lloginid);


            }
        }, SPLASH_DISPLAY_LENGTH);

    }


    private void showAlertDialogue(String message) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("No Internet");
        builder1.setMessage(message);
        builder1.setIcon(R.drawable.ic_wifi_off_fill);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void postData(String login_id) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iot.itecknologi.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RetrofitAPI2 retrofitAPI = retrofit.create(RetrofitAPI2.class);
        logincheck modal = new logincheck(login_id);
        Call<ResponseLoginCheck> call = retrofitAPI.createComment(login_id);
        Toast.makeText(splash.this, "Active", Toast.LENGTH_LONG).show();

        call.enqueue(new Callback<ResponseLoginCheck>() {
            @Override
            public void onResponse(Call<ResponseLoginCheck> call, Response<ResponseLoginCheck> response) {
                ResponseLoginCheck responseFromAPI = response.body();


                if (responseFromAPI.getSuccess().equals("false")){
                    Intent intent = new Intent(getApplicationContext(),login_one.class);
                    startActivity(intent);
                }else {
                    if (responseFromAPI.getSuccess().equals("true")){
                        String contactNo = responseFromAPI.getContact();
                        Intent intent2 = new Intent(getApplicationContext(),TripDetailActivity.class);
                        intent2.putExtra("contact",contactNo);
                        startActivity(intent2);
                    }
                }
//
//                // String responseString = "Response Code : " + response.code() +"\n Device ID:"+responseFromAPI.getDeviceId()+ "\nEmail: " + responseFromAPI.getEmail() + "\n" + "Phone: " + responseFromAPI.getContact()+"\n"+"response:"+responseFromAPI.getSuccess()+"\n"+"Msg:"+responseFromAPI.getMessage();
//                String responseString = "Response Code:" + response.code() + "\n" + "Response:" + responseFromAPI.getSuccess();
//                Toast.makeText(splash.this, responseString, Toast.LENGTH_SHORT).show();
//                if (responseFromAPI.getSuccess().equals("true")) {
//
//                    Intent intent = new Intent(getApplicationContext(), TripDetailActivity.class);
//                    startActivity(intent);
//
//                    Toast.makeText(splash.this, "Done", Toast.LENGTH_SHORT).show();
//                } else
//
//                    return;
            }

            @Override
            public void onFailure(Call<ResponseLoginCheck> call, Throwable t) {

                Toast.makeText(splash.this, "Error Found:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());

            }
        });

//            call.enqueue(new Callback<logincheck>() {
//                @Override
//                public void onResponse(@NonNull Call<logincheck> call, @NonNull Response<logincheck> response) {
//
//                    logincheck responseFromAPI = response.body();
//
//                    // String responseString = "Response Code : " + response.code() +"\n Device ID:"+responseFromAPI.getDeviceId()+ "\nEmail: " + responseFromAPI.getEmail() + "\n" + "Phone: " + responseFromAPI.getContact()+"\n"+"response:"+responseFromAPI.getSuccess()+"\n"+"Msg:"+responseFromAPI.getMessage();
//                    String responseString = "Response Code:" + response.code() + "\n" + "Response:" + responseFromAPI.getsuccess();
//
//
//                    Toast.makeText(splash.this, responseString, Toast.LENGTH_SHORT).show();
//                    if (responseFromAPI.getsuccess().equals("true")) {
//                        Intent intent = new Intent(getApplicationContext(), TripDetailActivity.class);
//                        startActivity(intent);
//                        Toast.makeText(splash.this, "Done", Toast.LENGTH_SHORT).show();
//                    } else
//                        //Toast.makeText(splash.this,"Something went wrong.", Toast.LENGTH_SHORT).show();
//                        return;
//
//                }
//
//                @Override
//                public void onFailure(Call<logincheck> call, Throwable t) {
//                    //responseTV.setText("Error found is : " + t.getMessage());
//                    Toast.makeText(splash.this, "Error Found:" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });

    }
}



