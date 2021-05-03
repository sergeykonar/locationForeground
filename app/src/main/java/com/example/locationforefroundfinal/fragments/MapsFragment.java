package com.example.locationforefroundfinal.fragments;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.locationforefroundfinal.MainActivity;
import com.example.locationforefroundfinal.R;
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

public class MapsFragment extends Fragment {

    private Button startBtn;
    private boolean tracking = false;
    private Intent intent;
    private LocationListViewModel locationListViewModel;
    private LocationList latLngList;

    private GoogleMap map;
    private Marker marker;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            try {
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.style_json));
                if (!success) {
                    Log.e("TAG", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("TAG", "Can't find style. Error: ", e);
            }
            map.clear();
            initMarker();
        }
    };

    private void initMarker(){
        marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(37.7750, 122.4183))
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_my_location_24))
        );
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location s2 = intent.getParcelableExtra("Location");
            Log.e("MAPS FRAGMENT", "Location received");
            updateMarker(s2);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("location"));
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
            Log.e("MAPS FRAGMENT", "updateMarker :" + e.toString() );
        }
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        locationListViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);
        locationListViewModel.getData().observe(getViewLifecycleOwner(), new Observer<ArrayList<LocationList>>() {
            @Override
            public void onChanged(ArrayList<LocationList> locationLists) {
                Log.e("VALUES", "CHANGED: " + locationLists.size());
            }
        });
        latLngList = new LocationList();

        intent = new Intent(getActivity(), NavigationService.class);
        intent.setAction("START");

        initStartButton();
        initNotificationChannel();

    }

    private void initStartButton() {
        startBtn = (Button) getView().findViewById(R.id.startTrack);
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
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }
    }

    private void startTracking() {
        getActivity().startService(intent);
    }

    private void stopTracking() {
        Intent stopIntent = new Intent(getActivity(), NavigationService.class);
        stopIntent.setAction("STOP_ACTION");
        getActivity().startService(stopIntent);
        locationListViewModel.insertLocationList(latLngList);
        Log.d("Locations size", String.valueOf(latLngList.size()));
        latLngList.clear();
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("ForegroundServiceChannel", "name", importance);
            notificationManager.createNotificationChannel(mChannel);
            Log.e("Not", "initNotChannel");
        }
    }
}