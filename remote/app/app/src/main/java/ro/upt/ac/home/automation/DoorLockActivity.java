package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DoorLockActivity extends AppCompatActivity {
    private ImageView mPowerBtn;
    private ImageView mUserPhoto;
    private ImageView mDashboard;

    private FirebaseAuth mAuthService;

    private static final int POWER_ON                    = 1;
    private static final int POWER_OFF                   = 0;
    private int SWITCH_VALUE                             = POWER_OFF;
    public static final String SWITCH_DOORLOCK_VALUE_KEY = "SWITCH_VALUE_DOOR_LOCK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_lock);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mPowerBtn = findViewById(R.id.iv_doorlock_power_btn);
        mUserPhoto = findViewById(R.id.iv_doorlock_user_photo);
        mDashboard = findViewById(R.id.iv_doorlock_app_logo);

        mAuthService = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        setUserProfilePicture();
        // get current value of the switch

        mPowerBtn.setBackgroundResource(R.drawable.off_button); // if off
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
                        mUserPhoto.setImageDrawable(photo);
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

        mPowerBtn.setOnClickListener(view -> {
            if (SWITCH_VALUE == POWER_OFF) {
                mPowerBtn.setBackgroundResource(R.drawable.on_button);
                SWITCH_VALUE = POWER_ON;
            } else if (SWITCH_VALUE == POWER_ON) {
                mPowerBtn.setBackgroundResource(R.drawable.off_button);
                SWITCH_VALUE = POWER_OFF;
            }

            // send request
        });

        mUserPhoto.setOnClickListener(view -> {
            Intent userIntent = new Intent(DoorLockActivity.this, UserActivity.class);
            startActivity(userIntent);
        });

        mDashboard.setOnClickListener(view -> {
            Intent dashboardIntent = new Intent(DoorLockActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
        });
    }
}