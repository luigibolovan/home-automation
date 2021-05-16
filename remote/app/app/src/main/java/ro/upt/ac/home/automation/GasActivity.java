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

public class GasActivity extends AppCompatActivity {
    private ImageView mDashboard;
    private ImageView mUserProfile;
    private TextView mMethaneConcentration;

    private FirebaseAuth mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mUserProfile    = findViewById(R.id.iv_gas_user_photo);
        mDashboard      = findViewById(R.id.iv_gas_app_logo);
        mMethaneConcentration = findViewById(R.id.tv_methane_gas_concentration_value);

        mAuthService = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserProfilePicture();
        //get current gas
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
                        Drawable photo = Drawable.createFromStream(is, "userprofilephotogas");
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
            Intent dashboardIntent = new Intent(GasActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
        });

        mUserProfile.setOnClickListener(view -> {
            Intent userProfileIntent = new Intent(GasActivity.this, UserActivity.class);
            startActivity(userProfileIntent);
        });

    }
}