package com.itecknologigroupofcompanies.itecklite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class login_one extends AppCompatActivity {
    private Button register;
    private VideoView regclip;
    private Dialog loadingDialogue;

    String tokenn = "1", responsDeviceid;
    private EditText ContactNo, Email;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    String validNumber = "^[+]?[0-9]{8,15}$";

    //String DeviceID="ABCDEF4321";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_one);

        getWindow().setFlags(1024,1024);

        register = (Button) findViewById(R.id.reg);
        regclip = (VideoView) findViewById(R.id.videoView8);
        Email = (EditText) findViewById(R.id.textInputEditText);
        ContactNo = (EditText) findViewById(R.id.textInputEditText2);

        loadingDialogue = new Dialog(this);
        loadingDialogue.setContentView(R.layout.loading);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_main_activity));
        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialogue.setCancelable(false);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(regclip);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.two);

        regclip.setVideoURI(uri);
        regclip.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                //Get VideoView's current width and height
                int videoViewWidth = regclip.getWidth();
                int videoViewHeight = regclip.getHeight();

                float xScale = 30;
                float yScale = 2;

                float scale = Math.min(xScale, yScale);

                float scaledWidth = scale * videoWidth;
                float scaledHeight = scale * videoHeight;

                //Set the new size for the VideoView based on the dimensions of the video
                ViewGroup.LayoutParams layoutParams = regclip.getLayoutParams();
                layoutParams.width = (int) scaledWidth;
                layoutParams.height = (int) scaledHeight;
                regclip.setLayoutParams(layoutParams);
                regclip.start();
            }

        });
        /** getting token **/

        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                tokenn.equals(token);
                // send it to server
            }
        });*/

        //        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        tokenn = token;
//                         Toast.makeText(login_one.this, tokenn, Toast.LENGTH_SHORT).show();
//                        // Log and toast
//                       // String msg = getString(R.string.msg_token_fmt, token);
//                       // Log.d(TAG, msg);
//                       // Toast.makeText(login_one.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });


        /**Working for device id:**/

        // For device id
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);
        //String deviceId = telephonyManager.getDeviceId();// this is device ID.
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);// this is Android ID.


/** On Click Register Button **/
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(Email.getText())) {
                    if (!TextUtils.isEmpty(ContactNo.getText())) {
                        if (Email.getText().toString().matches(emailPattern)) {
                            if (ContactNo.getText().toString().matches(validNumber)) {
                                regclip.stopPlayback();
                                String a = androidId.toString().trim();
                                String b = Email.getText().toString().trim();
                                String c = ContactNo.getText().toString().trim();
                                String d = tokenn.trim();

                                postData(a, b, c, d);

                            } else ContactNo.setError("Enter Valid Contact");
                        } else Email.setError("Enter Valid Email");
                    } else ContactNo.setError("Enter Contact No");
                } else Email.setError("Enter Email");


            }
        });
    }

    private void postData(String device_id, String email, String contact, String FcmToken) {
        loadingDialogue.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iot.itecknologi.com/mobile/login.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        DataModal modal = new DataModal(device_id, email, contact, FcmToken);

        if (modal.equals(null) && modal.equals("")) {
//            Toast.makeText(login_one.this, "Modal Is Empty", Toast.LENGTH_LONG).show();
        } else {
            TelephonyManager telephonyManager;
            telephonyManager = (TelephonyManager) getSystemService(Context.
                    TELEPHONY_SERVICE);
            String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);// this is Android ID.

            HashMap<String, String> fields = new HashMap<>();
            String a = androidId;
            String b = Email.getText().toString();
            String c = ContactNo.getText().toString();
            String d = tokenn;
            fields.put("device_id", a);
            fields.put("Email", b);
            fields.put("Contact", c);
            fields.put("FcmToken", d);
//            Toast.makeText(login_one.this, "Active", Toast.LENGTH_LONG).show();
            Call<DataModal> call = retrofitAPI.createComment(fields);

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("AndroidId", a);
            myEdit.putString("Email", b);
            myEdit.putString("Phone", c);
//            Toast.makeText(login_one.this, "Data Saved", Toast.LENGTH_LONG).show();
            myEdit.commit();


            call.enqueue(new Callback<DataModal>() {
                @Override
                public void onResponse(Call<DataModal> call, retrofit2.Response<DataModal> response) {
                    DataModal responseFromAPI = response.body();
                    String responseString = "Response Code:" + response.code() + "\n" + "Response:" +
                            responseFromAPI.getSuccess() + "\n" + "Msg:" + responseFromAPI.getMessage();
                    responsDeviceid = responseFromAPI.getapploginid();
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("apploginid", responsDeviceid);

                    if (responseFromAPI.getSuccess().equals("true") && responseFromAPI.getMessage().equals("OTP Sent")) {
                        Intent intent = new Intent(getApplicationContext(), otp_check.class);
                        startActivity(intent);
                        finish();
                        loadingDialogue.dismiss();

                    } else {
                        loadingDialogue.dismiss();
                        showAlertDialogue("User Not Registered");
                    }
//                        Toast.makeText(login_one.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    return;
                }

                @Override
                public void onFailure(Call<DataModal> call, Throwable t) {


//                    Toast.makeText(login_one.this, "Error Found:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showAlertDialogue(String message) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
//        builder1.setIcon()
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void setBtnEnabled() {
        register.setEnabled(true);
        register.setTextColor(Color.rgb(255, 255, 255));
    }

    public void setBtnDisabled() {
        register.setEnabled(false);
        register.setTextColor(Color.argb(50, 255, 255, 255));
    }


}