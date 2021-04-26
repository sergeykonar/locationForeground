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

    private final String TAG = "NavigationService";


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Log.e(TAG, "onCreate() completed");
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        createNotificationChannel();

        // Для такого сервиса надо обязательно вызвать метод startForeground
        startForeground(messageId, makeNotification("Foreground Service"));

        // Делаем тяжёлую работу в потоке
        new Thread(new Runnable() {
            @Override
            public void run() {

//                factorial *= ++factorial;
//                Notification notification = makeNotification("Next factorial ");
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(messageId, notification);

            }
        }).start();

        Log.e(TAG, "onStartCommand()");
        return START_NOT_STICKY;
    }



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

            Intent intentNew = new Intent("location");
            intentNew.putExtra("Location", location);

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentNew);
        }
    };


}
