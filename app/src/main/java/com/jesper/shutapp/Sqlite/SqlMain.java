package com.jesper.shutapp.Sqlite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jesper.shutapp.Activities.MainActivity;
import com.jesper.shutapp.R;

import java.util.List;

public class SqlMain extends AppCompatActivity {

    private ReportViewModel mReportViewModel;
    private static final int NEW_REPORT_ACTIVITY_REQUES_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sqlmain_layout);

        RecyclerView recyclerView = findViewById(R.id.report_recyclerview);
        final ReportListAdapter adapter = new ReportListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReportViewModel = ViewModelProviders.of(this).get(ReportViewModel.class);

        mReportViewModel.getmAllReports().observe(this, new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                adapter.setReports(reports);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SqlMain.this, NewReportActivity.class);
                startActivityForResult(intent, NEW_REPORT_ACTIVITY_REQUES_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == NEW_REPORT_ACTIVITY_REQUES_CODE && resultCode == RESULT_OK) {
            Report report = new Report(data.getStringExtra(NewReportActivity.EXTRA_REPLY));

            mReportViewModel.insert(report);
        } else {
            Toast.makeText(this, R.string.empty_not_saved, Toast.LENGTH_SHORT).show();
        }
    }
}
