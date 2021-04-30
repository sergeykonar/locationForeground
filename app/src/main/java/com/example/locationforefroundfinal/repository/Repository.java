package com.example.locationforefroundfinal.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import com.example.locationforefroundfinal.db.LocationDao;
import com.example.locationforefroundfinal.db.LocationDatabase;
import com.example.locationforefroundfinal.model.LocationList;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    private static LocationDatabase locationDatabase;

    private MutableLiveData<ArrayList<LocationList>> data;

    public Repository(Application application) {
        locationDatabase = Room.databaseBuilder(application.getApplicationContext(), LocationDatabase.class, "myDB").allowMainThreadQueries().build();
        data = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        LocationDao locationDao = locationDatabase.locationDao();
        ArrayList<LocationList> locationList = (ArrayList<LocationList>) locationDao.getAll();
        data.setValue(locationList);
    }

    public MutableLiveData<ArrayList<LocationList>> getData() {
        return data;
    }

    public void insertLocationList(LocationList locationList){
        LocationDao locationDao = locationDatabase.locationDao();
        locationDao.insertTask(locationList);
        ArrayList<LocationList> list = new ArrayList<>();

        ArrayList<LocationList> old = (ArrayList<LocationList>) locationDao.getAll();
        list.addAll(old);

        Log.d(getClass().getName(), "LatLngs added : " + locationList.size() + "to List of Trainings");

        data.setValue(list);
    }
}
