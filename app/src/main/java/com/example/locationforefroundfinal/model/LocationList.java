package com.example.locationforefroundfinal.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@TypeConverters({LocationList.LocationConverter.class})
@Entity
public class LocationList {


    @PrimaryKey(autoGenerate = true)
    private int id;



    @ColumnInfo(name = "lol")
    private ArrayList<LatLng> locations;

    public LocationList() {
        this.locations = new ArrayList<>();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public ArrayList<LatLng> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<LatLng> locations) {
        this.locations = locations;
    }

    public int size() {
        return locations.size();
    }

    public void clear() {
        locations.clear();
    }

    public void add(LatLng latLng) {
        locations.add(latLng);
    }

    public LatLng get(int i) {
        return locations.get(i);
    }


    public static class LocationConverter {
        @TypeConverter
        public String fromOptionValuesList(ArrayList<LatLng> optionValues) {
            if (optionValues == null) {
                return (null);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<LatLng>>() {
            }.getType();
            String json = gson.toJson(optionValues, type);
            return json;
        }

        @TypeConverter
        public ArrayList<LatLng> toOptionValuesList(String optionValuesString) {
            if (optionValuesString == null) {
                return (null);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<LatLng>>() {
            }.getType();
            ArrayList<LatLng> productCategoriesList = gson.fromJson(optionValuesString, type);
            return productCategoriesList;
        }

    }
}
