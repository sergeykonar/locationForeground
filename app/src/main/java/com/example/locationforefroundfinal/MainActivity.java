package com.example.locationforefroundfinal;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.locationforefroundfinal.model.LocationList;
import com.example.locationforefroundfinal.service.NavigationService;

import com.example.locationforefroundfinal.viewmodel.LocationListViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Button startBtn;
    private boolean tracking = false;
    private Intent intent;
    private LocationList latLngList;
    private Marker marker;


    private LocationListViewModel locationListViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = new Intent(MainActivity.this, NavigationService.class);
        intent.setAction("START");
        latLngList = new LocationList();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initStartButton();
        initNotificationChannel();

        locationListViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);
        locationListViewModel.getData().observe(this, new Observer<ArrayList<LocationList>>() {
            @Override
            public void onChanged(ArrayList<LocationList> locationLists) {
                Log.e("VALUES", "CHANGED: " + locationLists.size());
            }
        });
    }


    // Start button. On click starts/stops foreground service NavigationService. Checking permissions

    private void initStartButton() {
        startBtn = (Button) findViewById(R.id.startTrack);
        startBtn.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!tracking){
                checkPermissions();
                tracking = true;
                startTracking();
                startBtn.setText("Stop");
            }
            else{
                tracking = false;
                stopTracking();
                startBtn.setText("Start");

            }
        }
    };

    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }
    }

    private void startTracking() {
        startService(intent);
    }

    private void stopTracking() {
        Intent stopIntent = new Intent(MainActivity.this, NavigationService.class);
        stopIntent.setAction("STOP_ACTION");
        startService(stopIntent);
        locationListViewModel.insertLocationList(latLngList);
        Log.d("Locations size", String.valueOf(latLngList.size()));
        latLngList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("location"));
    }



    // Creating the BroadcastReceiver to get Location from Location Service

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location s2 = intent.getParcelableExtra("Location");
            Log.e(getPackageName(), "Location received");
            updateMarker(s2);
        }
    };


    // INITIALIZATION and UPDATE the position marker

    private void initMarker(){
        marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(37.7750, 122.4183))
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_baseline_my_location_24))
        );
    }

    private void updateMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latLngList.add(latLng);
        marker.setPosition(latLng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
        try {
            Polyline line = map.addPolyline(new PolylineOptions()
                    .add(latLngList.get(latLngList.size() - 2), latLng)
                    .width(5)
                    .color(Color.RED));
        }catch (Exception e){
            Log.e(getPackageName(), "updateMarker :" + e.toString() );
        }
    }


    // DO NOT TOUCH THIS CODE BELOW

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("ForegroundServiceChannel", "name", importance);
            notificationManager.createNotificationChannel(mChannel);
            Log.e("Not", "initNotChannel");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }
        map.clear();
        initMarker();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_baseline_my_location_24);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);

        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        Canvas canvas2 = new Canvas(bitmap);
        canvas2.drawBitmap(bitmap, 0, 0, paint);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}