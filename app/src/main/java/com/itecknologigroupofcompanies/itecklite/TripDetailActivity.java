package com.itecknologigroupofcompanies.itecklite;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private BottomSheetBehavior bottomSheetBehavior;
    ConstraintLayout layout;

    private RecyclerView recyclerView;
    TripAdapter adapter;
    private List<TripModel> list;
    ArrayList<String> VehicleId = new ArrayList<>();
    ArrayList<String> VehicleRegId = new ArrayList<>();
    TextView txtNoCar, txtUserName,txtVehicleDetails;
    private Dialog loadingDialogue;

    private static RemoteViews contentView;
    private static Notification notification;
    private static NotificationManager notificationManager;
    private static final int NotificationID = 1005;
    private static NotificationCompat.Builder mBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


//        notificationWalaKaam();
//        getToken();
        loadingDialogue = new Dialog(this);
        loadingDialogue.setContentView(R.layout.loading);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_main_activity));
        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialogue.setCancelable(false);


        layout = findViewById(R.id.txtNumP);
        txtVehicleDetails = findViewById(R.id.txtVehicleDetails);
        txtNoCar = findViewById(R.id.txt_car_no);
        txtUserName = findViewById(R.id.txtUserName);
        layout.setOnClickListener(this);

        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(420);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
//                        Toast.makeText(TripDetailActivity.this, "STATE HIDDEN", Toast.LENGTH_SHORT).show();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
//                        Toast.makeText(TripDetailActivity.this, "STATE Exp", Toast.LENGTH_SHORT).show();

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
//                        Toast.makeText(TripDetailActivity.this, "STATE Collapsed", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        try {
            getCarData("03313344034");
        }catch (Exception e){

        }
        rvInitialization();

        listData();

    }

    private void getCarData(String contactNo) {
//        loadingDialogue.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iot.itecknologi.com/mobile/loadcustomerdata.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<ResponseModel> call = retrofitAPI.getCarDataList(contactNo);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                String name = response.body().getName();
                txtUserName.setText("Hi, "+name);
                String success = response.body().getSuccess();
                List<VehicleModel> vehicleModelArrayList = response.body().vehicle;

                for (VehicleModel vehicleModel : vehicleModelArrayList) {
                    VehicleRegId.add(vehicleModel.getVeh_reg());
                    VehicleId.add(vehicleModel.getVehicle_id());
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TripDetailActivity.this, token, Toast.LENGTH_LONG).show();
                        String device_id = getDeviceId();
                        Toast.makeText(TripDetailActivity.this, device_id.toString(), Toast.LENGTH_SHORT).show();
//                        postData(token, device_id);
//                        responseTV.setText("DataPosted");

                    }
                });
    }

    private void rvInitialization() {
        recyclerView = findViewById(R.id.RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        adapter = new TripAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    private void listData() {
        list.add(new TripModel("2:54pm", "9:10pm", "iTecknologi Group of Companies"
                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));

        list.add(new TripModel("1:22pm", "10:01pm", "Clifton BLock 7 Mai Kolachi Bypass"
                , "University Of Karachi", "20 KM", "1:01am"));

        list.add(new TripModel("2:54pm", "9:10pm", "Nandos Clifton Opp. Shaheed Benazir Bhutto Park"
                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));

        list.add(new TripModel("2:54pm", "9:10pm", "iTecknologi Group of Companies"
                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));

        list.add(new TripModel("2:54pm", "9:10pm", "iTecknologi Group of Companies"
                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));

        list.add(new TripModel("2:54pm", "9:10pm", "iTecknologi Group of Companies"
                , "Department of Computer Science UBIT", "21 KM", "3:01pm"));

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        double lat = 24.827286;
        double longi = 67.024115;

        LatLng sydney = new LatLng(lat, longi);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMinZoomPreference(18f);

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
                getSelectedCarData(selectedCarVid);

                dialog.dismiss();
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void getSelectedCarData(String selectedCarVid) {
//        loadingDialogue.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://iot.itecknologi.com/mobile/get_vehicle_latest_info.php/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<SelectedVehicleResponseModel> call = retrofitAPI.getSingleCarData(selectedCarVid);
        call.enqueue(new Callback<SelectedVehicleResponseModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<SelectedVehicleResponseModel> call, Response<SelectedVehicleResponseModel> response) {

                String location = response.body().getLocation();
                String health = response.body().getBatteryHealth();
                double volt = response.body().getBatteryVolt();
                int ignition = response.body().getIgnition();
                int speed = response.body().getSpeed();
                String vehicleNo = response.body().getVehicleNo();

                txtVehicleDetails.setText("Last Reported Location of your vehicle is\n"+location);
            }

            @Override
            public void onFailure(Call<SelectedVehicleResponseModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}