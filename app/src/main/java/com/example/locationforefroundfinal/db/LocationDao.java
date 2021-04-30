package com.example.locationforefroundfinal.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.locationforefroundfinal.model.LocationList;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insertTask(LocationList task);

    @Query("SELECT * FROM locationList")
    List<LocationList> getAll();

    @Query("SELECT * FROM locationList WHERE id=(SELECT max(id) FROM locationList)")
    LocationList getLast();

    @Query("DELETE FROM locationList")
    void deleteAll();
}
