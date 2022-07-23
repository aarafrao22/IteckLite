package com.itecknologigroupofcompanies.itecklite;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private GoogleMap mMap;

    String latitude, longitude;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String context = remoteMessage.getData().get("context");

        if (context!=null){
            try {
                RunNotification(title, body, Integer.parseInt(context));
                
            }catch (NumberFormatException numberFormatException){

            }
        }else {

        }


//        sendNotification(title, body);

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        saveUpdatedToken(token);
    }

    private void scheduleJob() {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    private void saveUpdatedToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("RefreshedToken", token);
        Log.d(TAG, "saveUpdatedToken: tokenSaved" + token);
        myEdit.apply();
    }


    private void RunNotification(String title, String messageBody, int context) {
        RemoteViews contentView;
        Notification notification;
        NotificationManager notificationManager;

        NotificationCompat.Builder mBuilder;

        Random rand = new Random();

        int n = rand.nextInt(5000);
        n += 1;

        int NotificationID = n;

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");


//        @SuppressLint("RemoteViewLayout")
//        RemoteViews r = new RemoteViews(getPackageName(), R.layout.expanded_notification);
//        mBuilder.setCustomBigContentView(r);
//        mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
////        r.setImageViewResource(R.id.icon, R.drawable.img);
//        r.setTextViewText(R.id.title2, title);
//        r.setTextViewText(R.id.text2, messageBody);

        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, messageBody);


        String currentTime = new SimpleDateFormat("HH:mm aa", Locale.getDefault()).format(new Date());
        contentView.setTextViewText(R.id.txtTime, currentTime);


        /*
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        */


        switch(context){
            case 0:
                contentView.setImageViewResource(R.id.image, R.drawable.img_logo);
                break;
            case 1:
                contentView.setImageViewResource(R.id.image, R.drawable.battery_disconnected);
                break;
            case 2:
                contentView.setImageViewResource(R.id.image, R.drawable.battery_connected);
                break;
            case 3:
                contentView.setImageViewResource(R.id.image, R.drawable.fence);
                break;
            case 4:
                contentView.setImageViewResource(R.id.image, R.drawable.ig_on);
                break;
            case 5:
                contentView.setImageViewResource(R.id.image, R.drawable.ig_of);
                break;
        }


//        {
//            if (context == 1)
//
//            else if (context ==2)
//            contentView.setImageViewResource(R.id.image, R.drawable.battery_connected);
//
//            else if (Objects.equals(context, "fence"))
//                contentView.setImageViewResource(R.id.image, R.drawable.fence);
//
//            else if (Objects.equals(context, "ignition on"))
//                contentView.setImageViewResource(R.id.image, R.drawable.ig_on);
//
//            else if (Objects.equals(context, "ignition off"))
//                contentView.setImageViewResource(R.id.image, R.drawable.ig_of);
//
//            else
//                contentView.setImageViewResource(R.id.image, R.drawable.img_logo);
//        }

        mBuilder.setSmallIcon(R.drawable.img_logo_uni);


        mBuilder.setAutoCancel(false);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(messageBody);
        mBuilder.setCustomContentView(contentView);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
//        mBuilder.setContent(contentView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        notification = mBuilder.build();
        notificationManager.notify(NotificationID, notification);

    }
}