package com.itecknologigroupofcompanies.itecklite;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private BottomSheetBehavior bottomSheetBehavior;
    LinearLayout layout;
    TextView txt_time, txtSpeed, txtIgnition, txtLatLong, txtVehicleNo;
    MarkerOptions markerOptions;
    Marker mMarker;
    private ImageView gas;
    private ImageView parking;
    private ImageView resturant;
    private ImageView banks;
    private ImageView carmechanic;
    private ImageView hospitals;


    Drawable drawable1;

    private RecyclerView recyclerView;
    TripAdapter adapter;
    private List<TripModel> list;
    ArrayList<String> VehicleId = new ArrayList<>();
    ArrayList<String> VehicleRegId = new ArrayList<>();
    ArrayList<String> VehicleObjId = new ArrayList<>();
    int i = 0;

    String selectedCarVid;
    String selectedObjID;

    TextView txtNoCar, txtUserName, txtVehicleDetails, txtDate;
    private Dialog loadingDialogue;
    LatLng myCarLocation;
    double locationX;
    double locationY;
    int angle = 90;
    int vehicleColor = R.drawable.black_car;
    int bgDrawable = R.drawable.bg_red;

    private static RemoteViews contentView;
    private static Notification notification;
    private static NotificationManager notificationManager;
    private static final int NotificationID = 1005;
    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder mBuilder;
    String phNo;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire(10 * 60 * 1000L /*10 minutes*/);

        wl.release();
//        circularProgressBar();

        loadingDialogue = new Dialog(this);
        loadingDialogue.setContentView(R.layout.loading);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_main_activity));
        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialogue.setCancelable(false);

//        Intent intent = getIntent();
//        phNo = intent.getStringExtra("contact");
        loadingDialogue.show();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        notificationWalaKaam();
        getToken();

        layout = findViewById(R.id.txtNumP);
        txtSpeed = findViewById(R.id.txtSpeed);
        txtVehicleNo = findViewById(R.id.txtSelectedVehicle);
        txtLatLong = findViewById(R.id.txtLatLong);
        txtIgnition = findViewById(R.id.txtIgnition);
        txt_time = findViewById(R.id.txt_time);
        txtDate = findViewById(R.id.txt_date);
        txtVehicleDetails = findViewById(R.id.txtVehicleDetails);
        txtNoCar = findViewById(R.id.txt_car_no);
        txtUserName = findViewById(R.id.txtUserName);

        parking = findViewById(R.id.imageView9);
        gas = findViewById(R.id.imageView10);
        resturant = (ImageView) findViewById(R.id.imageView11);
        banks = (ImageView) findViewById(R.id.imageView12);
        hospitals = (ImageView) findViewById(R.id.imageView13);
        carmechanic = (ImageView) findViewById(R.id.imageView14);


        layout.setOnClickListener(this);
        parking.setOnClickListener(this);
        gas.setOnClickListener(this);
        resturant.setOnClickListener(this);
        banks.setOnClickListener(this);
        hospitals.setOnClickListener(this);
        carmechanic.setOnClickListener(this);


        checkConnection();
//        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
//        bottomSheetBehavior.setPeekHeight(400);


        circularBars();
        circularBars2();


        try {

            if (checkConnection()) {
                getCarData(getIntent().getStringExtra("contact"));
                drawable1 = getDrawable(bgDrawable);
                layout.setBackground(drawable1);
                loadingDialogue.dismiss();
            } else {
                showAlertDialogue2("No Internet",
                        "Make sure your internet is connected and try again", R.drawable.ic_wifi_off_fill);
            }


        } catch (Exception e) {

            loadingDialogue.dismiss();

        }


//        rvInitialization();
//
//        listData();


    }

    private boolean checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (null != networkInfo) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 1490);

            return true;
        } else {
            showAlertDialogue2("No Internet",
                    "Make sure your internet is connected and try again", R.drawable.ic_wifi_off_fill);
//            finish();
            return false;
        }
//        finish();

    }

    private void circularBars() {

        CircularProgressBar circularProgressBar6 = (CircularProgressBar) findViewById(R.id.yourCircularProgressbarY);
        circularProgressBar6.setProgressWithAnimation(65.0f, 1000L);
        circularProgressBar6.setProgressMax(100.0f);
        circularProgressBar6.setProgressBarColor(ViewCompat.MEASURED_STATE_MASK);

        //C00219 r49 = r8;
        circularProgressBar6.setProgressBarColorStart(Integer.valueOf(Color.rgb(102, 204, 0)));
        circularProgressBar6.setProgressBarColorEnd(Integer.valueOf(Color.rgb(102, 204, 0)));
        circularProgressBar6.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBar6.setBackgroundProgressBarColor(Color.rgb(224, 224, 244));
        circularProgressBar6.setBackgroundProgressBarColorStart(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBar6.setBackgroundProgressBarColorEnd(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBar6.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBar6.setProgressBarWidth(4.0f);
        circularProgressBar6.setBackgroundProgressBarWidth(3.0f);
        circularProgressBar6.setRoundBorder(true);
        circularProgressBar6.setStartAngle(365.0f);
        circularProgressBar6.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);


        CircularProgressBar circularProgressBarr7 = (CircularProgressBar) findViewById(R.id.yourCircularProgressbarrY2);
        circularProgressBarr7.setProgressWithAnimation(65.0f, 1000L);
        circularProgressBarr7.setProgressMax(200.0f);
        circularProgressBarr7.setProgressBarColor(ViewCompat.MEASURED_STATE_MASK);

        circularProgressBarr7.setProgressBarColorStart(Integer.valueOf(Color.rgb(255, 153, 51)));
        circularProgressBarr7.setProgressBarColorEnd(Integer.valueOf(Color.rgb(255, 153, 51)));
        circularProgressBarr7.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBarr7.setBackgroundProgressBarColor(Color.rgb(224, 224, 224));
        circularProgressBarr7.setBackgroundProgressBarColorStart(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBarr7.setBackgroundProgressBarColorEnd(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBarr7.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBarr7.setProgressBarWidth(4.0f);
        circularProgressBarr7.setBackgroundProgressBarWidth(3.0f);
        circularProgressBarr7.setRoundBorder(true);
        circularProgressBarr7.setStartAngle(365.0f);
        circularProgressBarr7.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);

        CircularProgressBar circularProgressBarrr8 = (CircularProgressBar) findViewById(R.id.yourCircularProgressbarrrY3);

        circularProgressBarrr8.setProgressWithAnimation(65.0f, 1000L);
        circularProgressBarrr8.setProgressMax(200.0f);
        circularProgressBarrr8.setProgressBarColor(ViewCompat.MEASURED_STATE_MASK);
        CircularProgressBar circularProgressBar2 = circularProgressBarr7;
        circularProgressBarrr8.setProgressBarColorStart(Integer.valueOf(Color.rgb(0, 128, 255)));
        circularProgressBarrr8.setProgressBarColorEnd(Integer.valueOf(Color.rgb(0, 128, 255)));
        circularProgressBarrr8.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBarrr8.setBackgroundProgressBarColor(Color.rgb(224, 224, 224));
        circularProgressBarrr8.setBackgroundProgressBarColorStart(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBarrr8.setBackgroundProgressBarColorEnd(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBarrr8.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBarrr8.setProgressBarWidth(4.0f);
        circularProgressBarrr8.setBackgroundProgressBarWidth(3.0f);
        circularProgressBarrr8.setRoundBorder(true);
        circularProgressBarrr8.setStartAngle(365.0f);
        circularProgressBarrr8.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);


    }

    private void circularBars2() {
        CircularProgressBar circularProgressBar1 = (CircularProgressBar) findViewById(R.id.yourCircularProgressbar7days);
        circularProgressBar1.setProgressWithAnimation(65.0f, 1000L);
        circularProgressBar1.setProgressMax(100.0f);
        circularProgressBar1.setProgressBarColor(ViewCompat.MEASURED_STATE_MASK);
        circularProgressBar1.setProgressBarColorStart(Integer.valueOf(Color.rgb(102, 204, 0)));
        circularProgressBar1.setProgressBarColorEnd(Integer.valueOf(Color.rgb(102, 204, 0)));
        circularProgressBar1.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBar1.setBackgroundProgressBarColor(Color.rgb(224, 224, 244));
        circularProgressBar1.setBackgroundProgressBarColorStart(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBar1.setBackgroundProgressBarColorEnd(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBar1.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBar1.setProgressBarWidth(4.0f);
        circularProgressBar1.setBackgroundProgressBarWidth(3.0f);
        circularProgressBar1.setRoundBorder(true);
        circularProgressBar1.setStartAngle(365.0f);
        circularProgressBar1.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);

        CircularProgressBar circularProgressBarr2 = (CircularProgressBar) findViewById(R.id.yourCircularProgressbar7days2);
        circularProgressBarr2.setProgressWithAnimation(65.0f, 1000L);
        circularProgressBarr2.setProgressMax(200.0f);
        circularProgressBarr2.setProgressBarColor(ViewCompat.MEASURED_STATE_MASK);
        CircularProgressBar circularProgressBar4 = circularProgressBar1;
        circularProgressBarr2.setProgressBarColorStart(Integer.valueOf(Color.rgb(255, 153, 51)));
        circularProgressBarr2.setProgressBarColorEnd(Integer.valueOf(Color.rgb(255, 153, 51)));
        circularProgressBarr2.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBarr2.setBackgroundProgressBarColor(Color.rgb(224, 224, 224));
        circularProgressBarr2.setBackgroundProgressBarColorStart(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBarr2.setBackgroundProgressBarColorEnd(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBarr2.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBarr2.setProgressBarWidth(4.0f);
        circularProgressBarr2.setBackgroundProgressBarWidth(3.0f);
        circularProgressBarr2.setRoundBorder(true);
        circularProgressBarr2.setStartAngle(365.0f);
        circularProgressBarr2.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);

        CircularProgressBar circularProgressBarrr3 = (CircularProgressBar) findViewById(R.id.yourCircularProgressbar7days3);
        circularProgressBarrr3.setProgressWithAnimation(65.0f, 1000L);
        circularProgressBarrr3.setProgressMax(200.0f);
        circularProgressBarrr3.setProgressBarColor(ViewCompat.MEASURED_STATE_MASK);
        circularProgressBarrr3.setProgressBarColorStart(Integer.valueOf(Color.rgb(0, 128, 255)));
        circularProgressBarrr3.setProgressBarColorEnd(Integer.valueOf(Color.rgb(0, 128, 255)));
        circularProgressBarrr3.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBarrr3.setBackgroundProgressBarColor(Color.rgb(224, 224, 224));
        circularProgressBarrr3.setBackgroundProgressBarColorStart(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBarrr3.setBackgroundProgressBarColorEnd(Integer.valueOf(Color.rgb(224, 224, 224)));
        circularProgressBarrr3.setBackgroundProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
        circularProgressBarrr3.setProgressBarWidth(4.0f);
        circularProgressBarrr3.setBackgroundProgressBarWidth(3.0f);
        circularProgressBarrr3.setRoundBorder(true);
        circularProgressBarrr3.setStartAngle(365.0f);
        circularProgressBarrr3.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);
    }


    private void getCarData(String contactNo) {
        loadingDialogue.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iot.itecknologi.com/mobile/loadcustomerdata.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<ResponseModel> call = retrofitAPI.getCarDataList(contactNo);
        call.enqueue(new Callback<ResponseModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                assert response.body() != null;
                String success = response.body().getSuccess();

                if (success.equals("true")) {
                    String name = response.body().getName();
                    txtUserName.setText("Hi, " + name);
                    List<VehicleModel> vehicleModelArrayList = response.body().vehicle;

                    for (VehicleModel vehicleModel : vehicleModelArrayList) {
                        VehicleRegId.add(vehicleModel.getVeh_reg());
                        VehicleId.add(vehicleModel.getVehicle_id());
                        VehicleObjId.add(vehicleModel.getObject_id());
                    }

                    selectedCarVid = VehicleId.get(i);
                    selectedObjID = VehicleObjId.get(i);

                    try {
                        if (checkConnection()) {
                            getSelectedCarData(VehicleId.get(0), VehicleObjId.get(0));
                        } else {
                            loadingDialogue.dismiss();
                            showAlertDialogue2("No Internet",
                                    "Make sure your internet is connected and try again", R.drawable.ic_wifi_off_fill);
                        }

                    } catch (Exception e) {
                    }
                    txtNoCar.setText(VehicleRegId.get(0).toString());
                }
                loadingDialogue.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                showAlertDialogue2("Server Not Responding", "", R.drawable.ic_close_circle_line);
                loadingDialogue.dismiss();
            }
        });
    }

    private void notificationWalaKaam() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
    }

    public void gassearch() {
        Intent mapIntent = new Intent("android.intent.action.VIEW", Uri.parse("geo:24.8607,67.0011?q=gas station"));
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?daddr= (gas station )")));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void resturantsearch() {
        Intent mapIntent = new Intent("android.intent.action.VIEW", Uri.parse("geo:24.8607,67.0011?q=Restaurants"));
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?daddr= (Restaurants )")));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void banksearch() {
        Intent mapIntent = new Intent("android.intent.action.VIEW", Uri.parse("geo:24.8607,67.0011?q=Banks"));
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?daddr= (Banks )")));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void hospitalssearch() {
        Intent mapIntent = new Intent("android.intent.action.VIEW", Uri.parse("geo:24.8607,67.0011?q=Hospitals"));
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?daddr= (Hospitals )")));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void carmechanicssearch() {
        Intent mapIntent = new Intent("android.intent.action.VIEW", Uri.parse("geo:24.8607,67.0011?q=car mechanic"));
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?daddr= (car mechanic )")));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().toString();
//                        Toast.makeText(TripDetailActivity.this, token, Toast.LENGTH_LONG).show();
                        String device_id = getDeviceId();
//                        Toast.makeText(TripDetailActivity.this, device_id.toString(), Toast.LENGTH_SHORT).show();
//                        postData(token, device_id);
//                        responseTV.setText("DataPosted");

                    }
                });
    }

//    private void rvInitialization() {
//        recyclerView = findViewById(R.id.RecyclerView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(RecyclerView.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//
//        list = new ArrayList<>();
//        adapter = new TripAdapter(list);
//        recyclerView.setAdapter(adapter);
//    }

//    private void listData() {
//        list.add(new TripModel("2:54pm", "9:10pm", "iTecknologi Group of Companies"
//                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));
//
//        list.add(new TripModel("1:22pm", "10:01pm", "Clifton BLock 7 Mai Kolachi Bypass"
//                , "University Of Karachi", "20 KM", "1:01am"));
//
//        list.add(new TripModel("2:54pm", "9:10pm", "Nandos Clifton Opp. Shaheed Benazir Bhutto Park"
//                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));
//
//        list.add(new TripModel("2:54pm", "9:10pm", "iTecknologi Group of Companies"
//                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));
//
//        list.add(new TripModel("2:54pm", "9:10pm", "iTecknologi Group of Companies"
//                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));
//
//        list.add(new TripModel("2:54pm", "9:10pm", "iTecknologi Group of Companies"
//                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));
//
//    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.clear();
        myCarLocation = new LatLng(locationY, locationX);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCarLocation, 18f));

        markerOptions = new MarkerOptions().position(myCarLocation).title("Your Location")
                .icon(bitmapDescriptorFromVector(this, vehicleColor)).rotation(angle);
        mMarker = mMap.addMarker(markerOptions);

        //        mMap.addMarker(markerOptions);

//        mMap.addMarker(new MarkerOptions().position(myCarLocation).title("Your Location")
//                .icon(bitmapDescriptorFromVector(this, vehicleColor)).rotation(angle));


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtNumP:
                showAlertDialog();
                break;

            case R.id.imageView9:
                //parking
                TripDetailActivity.this.startActivity(new Intent(TripDetailActivity.this.getApplicationContext(),
                        mapref.class));
                break;

            case R.id.imageView10:
                //gas
                TripDetailActivity.this.gassearch();
                break;

            case R.id.imageView11:
                //resturant
                TripDetailActivity.this.resturantsearch();
                break;

            case R.id.imageView12:
                //banks
                TripDetailActivity.this.banksearch();

                break;

            case R.id.imageView13:
                //hospital
                TripDetailActivity.this.hospitalssearch();

                break;

            case R.id.imageView14:
                //car mechanic
                TripDetailActivity.this.carmechanicssearch();
                break;

        }
    }

    private void showAlertDialogue2(String title, String message, int icon) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setIcon(icon);
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


    private void showAlertDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setIcon(R.drawable.ic_baseline_arrow_drop_down_24);
        alt_bld.setTitle("Select Car");
        alt_bld.setSingleChoiceItems(VehicleRegId.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                txtNoCar.setText(VehicleRegId.get(item).toString());
                i = item;
                selectedCarVid = VehicleId.get(item);
                selectedObjID = VehicleObjId.get(item);

                try {
                    if (checkConnection()) {
                        getSelectedCarData(selectedCarVid, selectedObjID);
                    } else {
                        showAlertDialogue2("No Internet",
                                "Make sure your internet is connected and try again", R.drawable.ic_wifi_off_fill);
                    }
                } catch (Exception e) {
                }

                dialog.dismiss();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void getSelectedCarData(String selectedCarVid, String selectedCarObjId) {
        loadingDialogue.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iot.itecknologi.com/mobile/get_vehicle_latest_info.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<SelectedVehicleResponseModel> call = retrofitAPI.getSingleCarData(selectedCarVid, selectedCarObjId);
        call.enqueue(new Callback<SelectedVehicleResponseModel>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onResponse(@NonNull Call<SelectedVehicleResponseModel> call, @NonNull Response<SelectedVehicleResponseModel> response) {

                String location = response.body().getLocation();
                String health = response.body().getBatteryHealth();
                String volt = response.body().getBatteryVolt();
                String ignition = response.body().getIgnition();
                String speed = response.body().getSpeed();
                String vehicleNo = response.body().getVehicleNo();
                String x = response.body().getX();
                String y = response.body().getY();
                GpsTime gpsTime = response.body().GpsTime;
                String V_Ang = response.body().getAng();
                String CustName = response.body().getCustName();

                if (location != null && health != null && volt != null && ignition != null && speed != null && vehicleNo != null && x != null && y != null && gpsTime != null && V_Ang != null) {
                    txtSpeed.setText(speed + " KM/H");
                    txtVehicleNo.setText(vehicleNo);
                    String ign;
                    if (Integer.parseInt(ignition) == 0) {
                        ign = "Ignition OFF";
                    } else {
                        ign = "Ignition ON";
                    }
                    txtIgnition.setText(ign);
                    txtLatLong.setText(y + " , " + x);
                    txtVehicleDetails.setText("Last Reported Location of your vehicle is\n" + location);
                    String stringTime = gpsTime.date;
                    String[] parts = stringTime.split(" ");
                    String date = parts[0]; // 004
                    String time = parts[1];
                    txtUserName.setText("Hi, " + CustName);

                    String[] dayyyy = date.split("-");
                    String vDay = dayyyy[2];
                    String vMonth = dayyyy[1];

                    txtDate.setText(date);
                    locationX = Double.parseDouble(x);
                    locationY = Double.parseDouble(y);
                    angle = Integer.parseInt(V_Ang);


                    String[] parts2 = time.split(":");
                    int hour = Integer.parseInt(parts2[0]);
                    String minute = parts2[1];
                    String timeStatus;

                    String finalHour;

                    if (hour > 12) {
                        finalHour = convertTime(String.valueOf(hour));
                        timeStatus = "PM";
                    } else {
                        finalHour = String.valueOf(hour);
                        timeStatus = "AM";
                    }

                    txt_time.setText(finalHour + ":" + minute + " " + timeStatus);


                    @SuppressLint("SimpleDateFormat")
                    String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

                    String dateStart = stringTime;
                    Log.d(TAG, "onResponse: dateStart" + dateStart);
                    String dateStop = sdf.toString();
                    Log.d(TAG, "onResponse: dateStop " + dateStop);

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long diffHours = 0;

                    Date d1 = null;
                    Date d2 = null;
                    try {
                        d1 = format.parse(dateStart);
                        d2 = format.parse(dateStop);
                        long diff = d2.getTime() - d1.getTime();

                        long diffSeconds = diff / 1000 % 60;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        diffHours = diff / (60 * 60 * 1000);

                        Log.d(TAG, "onResponse: diffSeconds " + diffSeconds);
                        Log.d(TAG, "onResponse: diffMinutes " + diffMinutes);
                        Log.d(TAG, "onResponse: diffHours " + diffHours);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (diffHours <= 24) {
                        if (Integer.parseInt(ignition) == 0) {
                            vehicleColor = R.drawable.red_car;
                            bgDrawable = R.drawable.bg_red;
                            drawable1 = getDrawable(bgDrawable);

                        } else if (Integer.parseInt(ignition) == 1 || speed.equals("0")) {
                            vehicleColor = R.drawable.green_car;
                            bgDrawable = R.drawable.bg_green;
                            drawable1 = getDrawable(bgDrawable);
                        }
                        layout.setBackground(drawable1);

                    } else {
                        vehicleColor = R.drawable.gray_car;
                        bgDrawable = R.drawable.bg_grey;
                        drawable1 = getDrawable(bgDrawable);
                        layout.setBackground(drawable1);
                    }
                    layout.setBackground(drawable1);
                    onMapReady(mMap);
                    loadingDialogue.dismiss();
                    updateMapAfter30Sec();
                } else {
                    Log.d(TAG, "onResponse: Something is null");
                    showAlertDialogue("Warning", "Data Not Available", R.drawable.ic_close_circle_line);
                    txtSpeed.setText("-" + " KM/H");
                    txtIgnition.setText("-");
                    txtLatLong.setText("-");
                    txtVehicleDetails.setText("Last Reported Location of your vehicle is\n" + "-");
                    txtDate.setText("00-00-0000");
                    txt_time.setText("00:00:00");
                    txtVehicleNo.setText("-");
                    txtUserName.setText("Hi, " + CustName);
                    mMarker.remove();
                    Drawable drawable = getDrawable(R.drawable.bg_grey);
                    layout.setBackground(drawable);
                    loadingDialogue.dismiss();

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<SelectedVehicleResponseModel> call, Throwable t) {

                loadingDialogue.dismiss();
//                Toast.makeText(TripDetailActivity.this, "Data Not Available", Toast.LENGTH_SHORT).show();
                showAlertDialogue("Warning", "Data Not Available", R.drawable.ic_close_circle_line);
                mMap.clear();
                txtSpeed.setText("-" + " KM/H");
                txtIgnition.setText("-");
                txtLatLong.setText("-");
                txtVehicleDetails.setText("Last Reported Location of your vehicle is\n" + "-");
                txtDate.setText("00-00-0000");
                txt_time.setText("00:00:00");
                markerOptions.visible(false);
                txtUserName.setText("---");
                @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getDrawable(R.drawable.bg_grey);
                layout.setBackground(drawable);
                mMarker.remove();
            }
        });
    }

    private void showAlertDialogue(String title, String message, int icon) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setIcon(icon);
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

    private void updateMapAfter30Sec() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: MapUpdated");
                if (checkConnection()) {
                    getSelectedCarData(selectedCarVid, selectedObjID);
                } else {
                    showAlertDialogue2("No Internet",
                            "Make sure your internet is connected and try again", R.drawable.ic_wifi_off_fill);
                }
                loadingDialogue.dismiss();
            }
        }, 30000);


    }

    private String convertTime(String time1) {
        String finalHour;
        switch (time1) {
            case "13":
                time1 = "1";
                break;
            case "14":
                time1 = "2";
                break;
            case "15":
                time1 = "3";
                break;
            case "16":
                time1 = "4";
                break;
            case "17":
                time1 = "5";
                break;
            case "18":
                time1 = "6";
                break;
            case "19":
                time1 = "7";
                break;
            case "20":
                time1 = "8";
                break;
            case "21":
                time1 = "9";
                break;
            case "22":
                time1 = "10";
                break;
            case "23":
                time1 = "11";
                break;
            case "24":
                time1 = "12";
                break;
        }
        finalHour = time1;
        return finalHour;
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vector) {

        Drawable drawable = ContextCompat.getDrawable(context, vector);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}