package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DashboardActivity extends AppCompatActivity {
    private RelativeLayout mLightStats;
    private RelativeLayout mAirStats;
    private RelativeLayout mDoorLockStats;
    private RelativeLayout mGasStats;
    private ImageView      mUserProfile;

    private FirebaseAuth   mAuthService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mLightStats     = findViewById(R.id.rl_dashboard_stats_lights);
        mAirStats       = findViewById(R.id.rl_dashboard_stats_air_humidity);
        mDoorLockStats  = findViewById(R.id.rl_dashboard_stats_doorlock);
        mGasStats       = findViewById(R.id.rl_dashboard_stats_methane_gas);
        mUserProfile    = findViewById(R.id.iv_dashboard_user_photo);

        mAuthService = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserProfilePicture();
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

        mLightStats.setOnClickListener(view -> {
            Intent lightsStatsIntent = new Intent(DashboardActivity.this, LightsActivity.class);
            startActivity(lightsStatsIntent);
        });

        mAirStats.setOnClickListener(view -> {
            Intent airStatsIntent = new Intent(DashboardActivity.this, AirActivity.class);
            startActivity(airStatsIntent);
        });

        mDoorLockStats.setOnClickListener(view -> {
            Intent doorlockIntent = new Intent(DashboardActivity.this, DoorLockActivity.class);
            startActivity(doorlockIntent);
        });

        mGasStats.setOnClickListener(view -> {
            Intent gasStatsIntent = new Intent(DashboardActivity.this, GasActivity.class);
            startActivity(gasStatsIntent);
        });

        mUserProfile.setOnClickListener(view -> {
            Intent userProfile = new Intent(DashboardActivity.this, UserActivity.class);
            startActivity(userProfile);
        });
    }
}