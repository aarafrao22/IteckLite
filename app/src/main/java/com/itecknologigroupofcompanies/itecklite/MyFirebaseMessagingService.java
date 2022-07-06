package com.itecknologigroupofcompanies.itecklite;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

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

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements OnMapReadyCallback {

    private static final String TAG = "MyFirebaseMsgService";
    private GoogleMap mMap;

    String latitude, longitude;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String context = remoteMessage.getData().get("context");
        latitude = remoteMessage.getData().get("lat");
        longitude = remoteMessage.getData().get("long");


        RunNotification(title, body, context);

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
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


//    private void sendNotification(String title, String messageBody) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//        Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),
//                R.drawable.img);
//
//        String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        RemoteViews r = new RemoteViews(getPackageName(), R.layout.notification);
//        r.setImageViewResource(R.id.image, R.drawable.img);
//        r.setImageViewResource(R.id.icon, R.drawable.img);
//        r.setTextViewText(R.id.title, title);
//        r.setTextViewText(R.id.text, messageBody);
//
//
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.ic_baseline_location_on_24)
//                        .setLargeIcon(rawBitmap)
//                        .setCustomContentView(r)
//                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//
//
//    }


    private void RunNotification(String title, String messageBody, String context) {
        RemoteViews contentView;
        Notification notification;
        NotificationManager notificationManager;
        int NotificationID = 1005;
        NotificationCompat.Builder mBuilder;

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");

        //TODO:Expanded Notification View


        @SuppressLint("RemoteViewLayout")
        RemoteViews r = new RemoteViews(getPackageName(), R.layout.expanded_notification);

        mBuilder.setCustomBigContentView(r);
        mBuilder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());


//        r.setImageViewResource(R.id.icon, R.drawable.img);
        r.setTextViewText(R.id.title2, title);
        r.setTextViewText(R.id.text2, messageBody);

        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, messageBody);

        /*
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        */



        if (Objects.equals(context, "battery charged"))
            contentView.setImageViewResource(R.id.image, R.drawable.battery);

        else if (Objects.equals(context, "battery low"))
            contentView.setImageViewResource(R.id.image, R.drawable.battery_low);

        else if (Objects.equals(context, "fence"))
            contentView.setImageViewResource(R.id.image, R.drawable.fence);

        else if (Objects.equals(context, "engine"))
            contentView.setImageViewResource(R.id.image, R.drawable.engine);

        else if (Objects.equals(context, "ignition on"))
            contentView.setImageViewResource(R.id.image, R.drawable.ignition_on);

        else if (Objects.equals(context, "ignition off"))
            contentView.setImageViewResource(R.id.image, R.drawable.ignition_off);

        else
            contentView.setImageViewResource(R.id.image, R.drawable.img_logo);


        mBuilder.setSmallIcon(R.drawable.img_logo_uni);


        mBuilder.setAutoCancel(false);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(messageBody);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
        mBuilder.setContent(contentView);

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        double longi = Double.parseDouble(longitude);
        double lati = Double.parseDouble(latitude);

        LatLng sydney = new LatLng(lati, longi);
        mMap.addMarker(new MarkerOptions().position(sydney).title("iTeck Group of companies"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}