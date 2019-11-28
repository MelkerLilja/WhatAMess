package com.jesper.shutapp.Sqlite;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class ReportViewModel extends AndroidViewModel {

    private ReportRepository mRepository;

    private LiveData<List<Report>> mAllReports;

    public ReportViewModel(Application application)
    {
        super(application);
        mRepository = new ReportRepository(application);
        mAllReports = mRepository.getAllReports();
    }

    LiveData<List<Report>> getmAllReports(){return mAllReports;}

    public void insert(Report report){
        mRepository.insert(report);}
}
