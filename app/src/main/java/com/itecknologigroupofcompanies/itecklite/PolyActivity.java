package com.itecknologigroupofcompanies.itecklite;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itecknologigroupofcompanies.itecklite.databinding.ActivityPolyBinding;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tech.gusavila92.websocketclient.WebSocketClient;

public class PolyActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private ImageView pausePlay;
    private ImageView btnBack;
    private boolean btnStatus = true;
    private WebSocketClient webSocketClient;
    TripSocketResponseModel[] tripSocketResponseModel;
    TripSocketResponseModel socketResponseModel;
    List<LatLng> latLngList = new ArrayList<>();
    List<LatLng> tripLatLngList = new ArrayList<>();
    private Dialog loadingDialogue;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView recyclerView;
    TripAdapter adapter;
    private List<TripModel> list;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    double first;
    double second;
    int i;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poly);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        i = getIntent().getIntExtra("status", 0);

        loadingItem();

        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        pausePlay = findViewById(R.id.btnPlay);
        btnBack = findViewById(R.id.btnBack);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(160);

        rvInitialization();
        listData();

        btnBack.setOnClickListener(this);
        pausePlay.setOnClickListener(this);

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

    private void loadingItem() {
        loadingDialogue = new Dialog(this);
        loadingDialogue.setContentView(R.layout.loading);
        loadingDialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corner_main_activity));
        loadingDialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialogue.setCancelable(false);
    }

    private void yesterDayActivityActions() {
        loadingDialogue.show();
        String yesterdayStart = getYesterday().concat("00:00:00.000");
        String yesterdayEnd = getYesterday().concat("23:59:00.000");

        getTripData("65566", "101837", yesterdayStart, yesterdayEnd);
    }

    private void past7DaysActivityActions() {
        loadingDialogue.show();
//        String yesterdayStart = getYesterday().concat("00:00:00.000");
//        String yesterdayEnd = getYesterday().concat("23:59:00.000");
//
//        getTripData("65566", "101837", yesterdayStart, yesterdayEnd);
    }

    private void getTripData(String objId, String vehicleId, String startTime, String endTime) {
        URI uri;
        try {
            uri = new URI("ws://iot.itecknologi.com:5599");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {

                //*objectId,vehicleId,st,et#
                String tripReq = "*"
                        .concat(objId).concat(",")
                        .concat(vehicleId).concat(",")
                        .concat(startTime).concat(",")
                        .concat(endTime).concat("#");

                webSocketClient.send(tripReq);
                Log.d(TAG, "onOpen: " + tripReq);

            }

            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received");
                final String message = s;
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        try {

                            Log.d(TAG, "onTextReceived: " + message);
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();

                            tripSocketResponseModel =
                                    (gson.fromJson(message, TripSocketResponseModel[].class));

                            for (TripSocketResponseModel trp : tripSocketResponseModel) {
                                Log.d(TAG, "model: " + trp);
                                double x = trp.getX();
                                double y = trp.getY();
                                LatLng latLng = new LatLng(y, x);
                                latLngList.add(latLng);


                                double ignition = trp.getIgn();

//                                while (ignition != 0.0){
//                                    double trX = trp.getX();
//                                    double trY = trp.getX();
//                                    LatLng latLng2 = new LatLng(trY,trX);
//
//                                    tripLatLngList.add(latLng2);
//                                }

                            }
                            for (int i = 0; i < latLngList.size(); i++) {
                                Log.d(TAG, "onMapReady: " + latLngList.get(i).toString());
                            }
                            drawPolyLineOnMap(latLngList, mMap);

                            LatLng sydney = latLngList.get(0);
                            LatLng sydney1 = latLngList.get(latLngList.size() - 1);
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                            mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in Sydney"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f));
                            Log.d(TAG, "updated");

//                            Toast.makeText(PolyActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            //RefreshMapAgain

                        } catch (Exception e) {
                            Log.d(TAG, "exception1111: " + e);

                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
                loadingDialogue.dismiss();
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    private String getYesterday() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (i == 1) {
            yesterDayActivityActions();
        } else if (i == 2) {

        }
    }

    public void drawPolyLineOnMap(List<LatLng> list, GoogleMap googleMap) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(R.color.blue));
        polyOptions.width(8);
        polyOptions.addAll(list);

        googleMap.clear();
        googleMap.addPolyline(polyOptions);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;

            case R.id.btnPlay:
                if (btnStatus) {
                    btnStatus = false;
                    pausePlay.setImageResource(R.drawable.ic_pause_circle_fill);
                } else {
                    pausePlay.setImageResource(R.drawable.ic_play_circle_fill);
                    btnStatus = true;
                }
                break;

        }
    }
}