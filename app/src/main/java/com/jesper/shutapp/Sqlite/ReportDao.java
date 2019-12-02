package com.jesper.shutapp.Sqlite;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReportDao {


    @Insert (onConflict = OnConflictStrategy.IGNORE)
    void insert(Report report);

    @Query("Select reportTxt from report")
    LiveData<List<Report>> getAllReports();

}
