package com.example.locationforefroundfinal.db;

import android.location.Location;

import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.locationforefroundfinal.model.LocationList;

@androidx.room.Database(entities = {LocationList.class}, version = 1)
@TypeConverters({LocationList.LocationConverter.class})
public abstract class LocationDatabase extends RoomDatabase {

    public abstract LocationDao locationDao();


}
