package com.example.locationforefroundfinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;


import com.example.locationforefroundfinal.model.LocationList;

import com.example.locationforefroundfinal.viewmodel.LocationListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    private BottomNavigationView bottomNavigationView;

    private LocationListViewModel locationListViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.mapsFragment, R.id.profileFragment).build();
        NavController navController = Navigation.findNavController(this, R.id.hostFragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        locationListViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);
        locationListViewModel.getData().observe(this, new Observer<ArrayList<LocationList>>() {
            @Override
            public void onChanged(ArrayList<LocationList> locationLists) {
                Log.e("VALUES", "CHANGED: " + locationLists.size());
            }
        });

    }

}