package com.itecknologigroupofcompanies.itecklite;

//import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class otp_check extends AppCompatActivity {
    private Button verify;
    private TextView timer, resendotp, otpnotify;
    private EditText otp;
    String code, contact = "";
    //private final int SPLASH_DISPLAY_LENGTH = 4000;
    private VideoView clip;
    private Dialog loadingDialogue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_check);
        getWindow().setFlags(1024, 1024);

        clip = (VideoView) findViewById(R.id.videoView7);
        timer = (TextView) findViewById(R.id.timer);
        otpnotify = (TextView) findViewById(R.id.textViewotpnotify);
        otp = (EditText) findViewById(R.id.textInputEditTextotp);
        resendotp = (TextView) findViewById(R.id.textView4);


        loadingDialogue = new Dialog(this);
        loadingDialogue.setContentView(R.layout.loading);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_main_activity));
        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialogue.setCancelable(false);


        // For device id
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);
        //String deviceId = telephonyManager.getDeviceId();// this is device ID.
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //Creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(clip);

        //specify the location of media file
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.two);

        clip.setVideoURI(uri);
        clip.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();

                int videoViewWidth = clip.getWidth();
                int videoViewHeight = clip.getHeight();

                float xScale = 30;
                float yScale = 2;

                float scale = Math.min(xScale, yScale);

                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;

                ViewGroup.LayoutParams layoutParams = clip.getLayoutParams();
                layoutParams.width = (int) scaledWidth;
                layoutParams.height = (int) scaledHeight;
                clip.setLayoutParams(layoutParams);
                clip.start();

            }

        });


        verify = (Button) findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO
                //Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(intent);
                String a = androidId.trim();
                String b = otp.getText().toString();
                String c = contact.trim();
                String d = "fcm";


                postData(a.trim(), b.trim(), c.trim(), d.trim());
            }
        });

        resendotp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO
                //Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(intent);
                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

                // The value will be default as empty string because for
                // the very first time when the app is opened, there is nothing to show
                String fetchemail = sh.getString("Email", "");
                String fetchphone = sh.getString("Phone", "");

                // We can then use the data
                String eemail = fetchemail;
                String pphone = fetchphone;
                String a = androidId.trim();
                String b = fetchemail;
                String c = fetchphone;
                String d = "ffcm";


                resendotp(a.trim(), b.trim(), c.trim(), d.trim());


            }
        });

        reverseTimer(150, timer);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        String fetchphone = sh.getString("Phone", "");

        String pphone = (fetchphone);
        otpnotify.setText("OTP sent on: " + pphone);


        //catching OTP code.
        SmsVerifyCatcher smsVerifyCatcher = new SmsVerifyCatcher(otp_check.this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                code = parseCode(message);//Parse verification code

                Log.d(" OTP", code);
                if (code != null && code != "") {
                    otp.setText(code);
                    String a = code;
                    otp.setText(a.trim());
                } else {
//                    Toast.makeText(otp_check.this, "Please proceed..", Toast.LENGTH_LONG).show();
                }
            }

        });
        smsVerifyCatcher.onStart();
    }


    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;

    }

    public void reverseTimer(int Seconds, final TextView tv) {

        new CountDownTimer(Seconds * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);

                int hours = seconds / (60 * 60);
                int tempMint = (seconds - (hours * 60 * 60));
                int minutes = tempMint / 60;
                seconds = tempMint - (minutes * 60);

                tv.setText("Expires in: " + String.format("%02d", hours)
                        + ":" + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                tv.setText("OTP Expired.");
                resendotp.setVisibility(View.VISIBLE);
            }

        }.start();
    }


    private void resendotp(String device_id, String email, String contact, String FcmToken) {
        // below line is for displaying our progress bar.
        //loadingPB.setVisibility(View.VISIBLE);

        // on below line we are creating a retrofit
        // builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iot.itecknologi.com/mobile/login.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        DataModal modal = new DataModal(device_id, email, contact, FcmToken);


        if (modal.equals(null) && modal.equals("")) {
//            Toast.makeText(otp_check.this, "Modal Is Empty", Toast.LENGTH_LONG).show();

        } else {
            // For device id
            TelephonyManager telephonyManager;
            telephonyManager = (TelephonyManager) getSystemService(Context.
                    TELEPHONY_SERVICE);

            @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);// this is Android ID.

            SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

            String fetchemail = sh.getString("Email", "");
            String fetchphone = sh.getString("Phone", "");


            String eemail = (fetchemail);
            String pphone = (fetchphone);


            HashMap<String, String> fields = new HashMap<>();
            String a = androidId;
            String b = eemail;
            String c = pphone;
            String d = "ffcm";
            fields.put("login_id", a);
            fields.put("Email", b);
            fields.put("Contact", c);
            fields.put("FcmToken", d);

//            Toast.makeText(otp_check.this, "Active", Toast.LENGTH_LONG).show();
            Call<DataModal> call = retrofitAPI.createComment(fields);

            call.enqueue(new Callback<DataModal>() {
                @Override
                public void onResponse(Call<DataModal> call, retrofit2.Response<DataModal> response) {


                    DataModal responseFromAPI = response.body();
                    String responseString = "Response Code:" + response.code() + "\n" + "Response:" + responseFromAPI.getSuccess() + "\n" + "Msg:" + responseFromAPI.getMessage();
//                    Toast.makeText(otp_check.this, responseString, Toast.LENGTH_SHORT).show();
                    if (responseFromAPI.getSuccess().equals("true") && responseFromAPI.getMessage().equals("OTP Sent")) {
                        Intent intent = new Intent(getApplicationContext(), otp_check.class);
                        startActivity(intent);

                    } else
//                        Toast.makeText(otp_check.this, "ERROR 404.", Toast.LENGTH_SHORT).show();
                        return;
                }

                @Override
                public void onFailure(Call<DataModal> call, Throwable t) {

//                    Toast.makeText(otp_check.this, "Error Found:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void postData(String deviceId, String otp, String contact, String FcmToken) {
        loadingDialogue.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iot.itecknologi.com/mobile/login.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        DataModal modal = new DataModal(deviceId, otp, contact, FcmToken);


        if (modal.equals(null) && modal.equals("")) {
//            Toast.makeText(otp_check.this, "Modal Is Empty", Toast.LENGTH_LONG).show();
            loadingDialogue.dismiss();
        } else {
            TelephonyManager telephonyManager;
            telephonyManager = (TelephonyManager) getSystemService(Context.
                    TELEPHONY_SERVICE);
            @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);// this is Android ID.

            HashMap<String, String> fields = new HashMap<>();
            String a = androidId;
            String b = otp.toString();
            String c = contact;
            fields.put("login_id", a);
            fields.put("OTP", b);
            fields.put("contact", c);

            Call<DataModal> call = retrofitAPI.createComment(fields);

            call.enqueue(new Callback<DataModal>() {
                @Override
                public void onResponse(Call<DataModal> call, retrofit2.Response<DataModal> response) {

                    DataModal responseFromAPI = response.body();
                    String responseString = "Response Code:" + response.code() + "\n" + "Response:" + responseFromAPI.getSuccess() + "\n" + "Msg:" + responseFromAPI.getMessage();

                    //Toast.makeText(otp_check.this, responseString, Toast.LENGTH_SHORT).show();
                    if (responseFromAPI.getSuccess().equals("true") && responseFromAPI.getMessage().equals("OTP Verified")) {
                        Intent intent = new Intent(getApplicationContext(), TripDetailActivity.class);

                        startActivity(intent);
                        finish();
                        loadingDialogue.dismiss();
                    } else
                        loadingDialogue.dismiss();
//                        Toast.makeText(otp_check.this, "Invalid OTP.", Toast.LENGTH_SHORT).show();
                    return;
                }


                @Override
                public void onFailure(Call<DataModal> call, Throwable t) {
                    loadingDialogue.dismiss();

//                    Toast.makeText(otp_check.this, "Error Found:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }

    }


}