package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.google.firebase.auth.FirebaseAuth;

public class LoadActivity extends AppCompatActivity {
    private FirebaseAuth mAuthService;

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
        mDummyLoadingTimer.start();
        mAuthService = FirebaseAuth.getInstance();
    }
}