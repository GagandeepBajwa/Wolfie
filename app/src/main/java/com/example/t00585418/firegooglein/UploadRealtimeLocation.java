package com.example.t00585418.firegooglein;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.Job;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by t00585387 on 4/11/2018.
 */

public class UploadRealtimeLocation extends com.firebase.jobdispatcher.JobService

    {

    BackgroundTask backgroundTask;
    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters jobParameters) {

        backgroundTask = new BackgroundTask()
        {
            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(getApplicationContext(),"Message from Background Task:" +s,Toast.LENGTH_LONG).show();
                jobFinished(jobParameters,false);
            }
        };

        backgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters jobParameters) {
        return true;
    }

    public static class BackgroundTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return "Hello from background job";
        }
    }
}


