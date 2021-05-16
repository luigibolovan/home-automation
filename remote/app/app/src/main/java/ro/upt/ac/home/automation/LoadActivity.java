package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class LoadActivity extends AppCompatActivity {
    private final static int FAKE_LOADING_TIME_VALUE    = 5000;
    private final CountDownTimer mDummyLoadingTimer     = new CountDownTimer(FAKE_LOADING_TIME_VALUE, 1000) {
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
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        mDummyLoadingTimer.start();
    }
}