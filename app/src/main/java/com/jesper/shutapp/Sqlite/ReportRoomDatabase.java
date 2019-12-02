package com.jesper.shutapp.Sqlite;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Report.class}, version = 1, exportSchema = false)
public abstract class ReportRoomDatabase extends RoomDatabase {
    public abstract ReportDao reportDao();

    private static volatile ReportRoomDatabase INSTANCE;
    private static int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    static ReportRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ReportRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder
                            (context.getApplicationContext(),
                                    ReportRoomDatabase.class,
                                    "report_database").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
            databaseWriteExecutor.execute(() ->
            {
                ReportDao dao = INSTANCE.reportDao();


               /* Report report = new Report("hello");
                dao.insert(report);
                report = new Report("World");
                dao.insert(report);*/

            });

        }
    };

   private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final ReportDao mDao;

        PopulateDbAsync(ReportRoomDatabase db) {
            mDao = db.reportDao();

        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.

            /*Report report = new Report("Hello");
            mDao.insert(report);
            report = new Report("World");
            mDao.insert(report);*/
            return null;
        }
    }
}
