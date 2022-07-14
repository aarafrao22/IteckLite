package com.itecknologigroupofcompanies.itecklite;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
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
    private Runnable runnableCode;
    Handler handler = new Handler();
    MarkerOptions markerOptions;
    Marker mMarker;

    private RecyclerView recyclerView;
    TripAdapter adapter;
    private List<TripModel> list;
    ArrayList<String> VehicleId = new ArrayList<>();
    ArrayList<String> VehicleRegId = new ArrayList<>();
    ArrayList<String> VehicleObjId = new ArrayList<>();
    TextView txtNoCar, txtUserName, txtVehicleDetails, txtDate;
    private Dialog loadingDialogue;
    LatLng myCarLocation;
    double locationX;
    double locationY;
    int angle = 90;
    int vehicleColor = R.drawable.black_car;

    private static RemoteViews contentView;
    private static Notification notification;
    private static NotificationManager notificationManager;
    private static final int NotificationID = 1005;
    private static NotificationCompat.Builder mBuilder;
    String phNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

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
        layout.setOnClickListener(this);

        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(400);

        try {
            getCarData(getIntent().getStringExtra("contact"));
            loadingDialogue.dismiss();
        } catch (Exception e) {
            loadingDialogue.dismiss();
        }

//        rvInitialization();
//
//        listData();

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

                    try {
                        getSelectedCarData(VehicleId.get(0), VehicleObjId.get(0));
                    } catch (Exception e) {
                    }
                    txtNoCar.setText(VehicleRegId.get(0).toString());
                }

                loadingDialogue.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
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
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setIcon(R.drawable.ic_baseline_arrow_drop_down_24);
        alt_bld.setTitle("Select Car");
        alt_bld.setSingleChoiceItems(VehicleRegId.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                txtNoCar.setText(VehicleRegId.get(item).toString());
                String selectedCarVid = VehicleId.get(item);
                String selectedObjID = VehicleObjId.get(item);

                try {
                    getSelectedCarData(selectedCarVid, selectedObjID);
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
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onResponse(Call<SelectedVehicleResponseModel> call, Response<SelectedVehicleResponseModel> response) {

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
                    String string = gpsTime.date;
                    String[] parts = string.split(" ");
                    String date = parts[0]; // 004
                    String time = parts[1];
                    txtUserName.setText("Hi, " + CustName);
                    String[] dayyyy = date.split("-");
                    String vDay = dayyyy[2];

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
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String s = sdf.format(new Date());
                    String[] tmp1 = s.split(":");
                    int currentTime = Integer.parseInt(tmp1[0]);


                    @SuppressLint("SimpleDateFormat")
                    String currentDay = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
                    int intDay = Integer.parseInt(vDay);

                    Drawable drawable1 = null;

                    if (hour > currentTime && intDay > Integer.parseInt(currentDay)) {
                        vehicleColor = R.drawable.gray_car;
                        drawable1 = getDrawable(R.drawable.bg_grey);

                    } else if (Integer.parseInt(ignition) == 0) {
                        vehicleColor = R.drawable.red_car;
                        drawable1 = getDrawable(R.drawable.bg_red);

                    } else if (Integer.parseInt(ignition) == 1 || speed.equals("0")) {
                        vehicleColor = R.drawable.green_car;
                        drawable1 = getDrawable(R.drawable.bg_green);
                    } else {
                        vehicleColor = R.drawable.black_car;
                        drawable1 = getDrawable(R.drawable.bg_black);
                    }
                    layout.setBackground(drawable1);


                    onMapReady(mMap);
                    loadingDialogue.dismiss();
                    updateMapAfter30Sec();
                } else {
                    Log.d(TAG, "onResponse: Something is null");
                    Toast.makeText(TripDetailActivity.this, "Vehicle Not Found", Toast.LENGTH_SHORT).show();
                    txtSpeed.setText("-" + " KM/H");
                    txtIgnition.setText("-");
                    txtLatLong.setText("-");
                    txtVehicleDetails.setText("Last Reported Location of your vehicle is\n" + "-");
                    txtDate.setText("00-00-0000");
                    txt_time.setText("00:00:00");
                    txtUserName.setText(CustName);
                    mMarker.remove();
                    Drawable drawable = getDrawable(R.drawable.bg_grey);
                    layout.setBackground(drawable);
                    loadingDialogue.dismiss();

                }
            }

            @Override
            public void onFailure(Call<SelectedVehicleResponseModel> call, Throwable t) {

                loadingDialogue.dismiss();
                Toast.makeText(TripDetailActivity.this, "Data Not Available", Toast.LENGTH_SHORT).show();
                mMap.clear();
                txtSpeed.setText("-" + " KM/H");
                txtIgnition.setText("-");
                txtLatLong.setText("-");
                txtVehicleDetails.setText("Last Reported Location of your vehicle is\n" + "-");
                txtDate.setText("00-00-0000");
                txt_time.setText("00:00:00");
                markerOptions.visible(false);
                txtUserName.setText("---");
                Drawable drawable = getDrawable(R.drawable.bg_grey);
                layout.setBackground(drawable);
                mMarker.remove();
            }
        });
    }

    private void updateMapAfter30Sec() {

        runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: MapUpdated");
                onMapReady(mMap);
                handler.postDelayed(runnableCode, 30000);
            }
        };
        handler.post(runnableCode);
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