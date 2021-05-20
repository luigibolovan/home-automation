package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class LoadActivity extends AppCompatActivity {
    private FirebaseAuth mAuthService;
    private static int JOB_ID = 30620;

    private final static int LOADING_TIME_VALUE         = 3000;
    private final CountDownTimer mDummyLoadingTimer     = new CountDownTimer(LOADING_TIME_VALUE, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            Intent nextActivityIntent;

            if(isAuthenticated()){
                nextActivityIntent= new Intent(LoadActivity.this, DashboardActivity.class);
            }else{
                nextActivityIntent = new Intent(LoadActivity.this, LoginActivity.class);
            }
            startActivity(nextActivityIntent);
            finish();
        }
    };

    private boolean isAuthenticated() {
        return mAuthService.getCurrentUser() != null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        checkIfNotificationJobIsRunning();
        mDummyLoadingTimer.start();
        mAuthService = FirebaseAuth.getInstance();
    }

    private void checkIfNotificationJobIsRunning() {
        final JobScheduler jobScheduler = (JobScheduler)getSystemService( Context.JOB_SCHEDULER_SERVICE ) ;
        boolean found = false;
        for ( JobInfo jobInfo : jobScheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == JOB_ID ) {
                found = true;
                Log.d("LOADACTIVITY", "Job found");
            }
        }
        if (!found) {
            ComponentName componentName = new ComponentName(this, AtmosphereService.class);
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setPeriodic(900000)
                    .build();

            JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            int result = scheduler.schedule(jobInfo);
            if (result == JobScheduler.RESULT_SUCCESS) {
                Log.d("LOADACTIVITY", "Job scheduled");
            } else {
                Log.d("LOADACTIVITY", "Job not scheduled");
            }
        }
    }
}