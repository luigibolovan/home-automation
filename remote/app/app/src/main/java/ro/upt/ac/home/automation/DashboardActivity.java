package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class DashboardActivity extends AppCompatActivity {
    RelativeLayout mLightStats;
    RelativeLayout mAirStats;
    RelativeLayout mDoorLockStats;
    RelativeLayout mGasStats;
    ImageView mUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mLightStats     = findViewById(R.id.rl_dashboard_stats_lights);
        mAirStats       = findViewById(R.id.rl_dashboard_stats_air_humidity);
        mDoorLockStats  = findViewById(R.id.rl_dashboard_stats_doorlock);
        mGasStats       = findViewById(R.id.rl_dashboard_stats_methane_gas);
        mUserProfile    = findViewById(R.id.iv_dashboard_user_photo);
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