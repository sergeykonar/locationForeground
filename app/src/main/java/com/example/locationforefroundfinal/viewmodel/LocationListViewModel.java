package com.example.locationforefroundfinal.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.locationforefroundfinal.model.LocationList;
import com.example.locationforefroundfinal.repository.Repository;

import java.util.ArrayList;

public class LocationListViewModel extends AndroidViewModel {

    private Repository repository;
    private MutableLiveData<ArrayList<LocationList>> data;

    public LocationListViewModel(@NonNull Application application) {
        super(application);
        this.repository = new Repository(application);
        this.data = getData();
    }

    public MutableLiveData<ArrayList<LocationList>> getData() {
        return repository.getData();
    }

    public void insertLocationList(LocationList locationList){

        repository.insertLocationList(locationList);
    }
}
