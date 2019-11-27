package com.jesper.shutapp.Sqlite;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ReportRepository {
    private ReportDao mReportDao;
    private LiveData<List<Report>> allReports;

    ReportRepository(Application application)
    {
        ReportRoomDatabase db = ReportRoomDatabase.getDatabase(application);
        mReportDao = db.reportDao();
        allReports = mReportDao.getAllReports();
    }

    LiveData<List<Report>> getAllReports()
    {
        return allReports;
    }

    void insert(Report report)
    {
        /*ReportRoomDatabase.databaseWriteExecutor.execute(()->
        {
            mReportDao.insert(report);
        });*/
        new insertAsyncTask(mReportDao).execute(report);
    }

    private static class insertAsyncTask extends AsyncTask<Report,Void,Void>
    {
        private ReportDao mAsyncTaskDao;

        insertAsyncTask(ReportDao dao)
        {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Report... reports) {
            mAsyncTaskDao.insert(reports[0]);
            return null;
        }
    }
}
