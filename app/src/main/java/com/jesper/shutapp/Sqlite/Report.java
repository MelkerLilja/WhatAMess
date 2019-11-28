package com.jesper.shutapp.Sqlite;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "report")
public class Report {


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "reportTxt")
    private String reportTxt;

    protected Report(String reportTxt)
    {
        this.reportTxt = reportTxt;
    }

    @NonNull
    public String getReportTxt() {
        return reportTxt;
    }

    public void setReportTxt(@NonNull String reportTxt) {
        this.reportTxt = reportTxt;
    }
}