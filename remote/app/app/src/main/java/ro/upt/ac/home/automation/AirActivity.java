package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AirActivity extends AppCompatActivity {
    private ImageView mDashboard;
    private ImageView mUserProfile;
    private TextView mTemperature;
    private TextView mHumidity;

    private FirebaseAuth mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mDashboard      = findViewById(R.id.iv_air_app_logo);
        mUserProfile    = findViewById(R.id.iv_temperature_user_photo);
        mTemperature    = findViewById(R.id.tv_air_temperature_value);
        mHumidity       = findViewById(R.id.tv_air_humidity_value);

        mAuthService = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserProfilePicture();
        //get current temperature and humidity
    }

    private void setUserProfilePicture() {
        FirebaseUser user = mAuthService.getCurrentUser();
        if (user == null) {
            finish();
        } else {
            if (user.getPhotoUrl() != null) {
                String userPhotoURL = user.getPhotoUrl().toString();
                Thread userPhotoThread = new Thread(() -> {
                    try {
                        InputStream is = (InputStream) new URL(userPhotoURL).getContent();
                        Drawable photo = Drawable.createFromStream(is, "userprofilephoto");
                        mUserProfile.setImageDrawable(photo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                userPhotoThread.start();
                try {
                    userPhotoThread.join(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDashboard.setOnClickListener(view -> {
            Intent dashboardIntent = new Intent(AirActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
        });

        mUserProfile.setOnClickListener(view -> {
            Intent userProfileIntent = new Intent(AirActivity.this, UserActivity.class);
            startActivity(userProfileIntent);
        });

    }
}