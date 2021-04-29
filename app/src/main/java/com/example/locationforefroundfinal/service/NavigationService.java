package com.example.locationforefroundfinal.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.locationforefroundfinal.MainActivity;
import com.example.locationforefroundfinal.R;

public class NavigationService extends Service {


    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private final int messageId=1000;
    private boolean running = false;
    private final String TAG = "NavigationService";

    private LocationManager locationManager;
    private Intent intentNew = new Intent("location");


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);



        Log.e(TAG, "onCreate() completed");
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        if(intent !=null && intent.getAction().equals("START")){
            startForeground(messageId, makeNotification("Foreground Service"));
            running = true;


//            new Thread(new Runnable() {
//                @RequiresApi(api = Build.VERSION_CODES.N)
//                @Override
//                public void run() {
//
//
//                }
//            }).start();

            Log.e(TAG, "onStartCommand()");
        }

        else if (intent!= null && intent.getAction().equals("STOP_ACTION")) {
            Log.i("LOG_TAG", "Received Stop Foreground Intent");
            //your end servce code
            stopForeground(true);
            stopSelfResult(startId);
            running = false;
            locationManager.removeUpdates(locationListener);
        }

        return START_NOT_STICKY;
    }





    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }



    @Override
    public void onDestroy() {
        running = true;

        super.onDestroy();
    }

    // Вывод уведомления в строке состояния
    @SuppressLint("MissingPermission")
    private Notification makeNotification(String message){
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Main service notification")
                .setContentText(message)
                .build();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.e("SD", location.toString());


            intentNew.putExtra("Location", location);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentNew);
        }
    };

    public class LocalBinder extends Binder {
        public NavigationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NavigationService.this;
        }
    }


}
