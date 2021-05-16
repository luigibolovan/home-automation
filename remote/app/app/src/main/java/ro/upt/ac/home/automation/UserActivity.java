package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UserActivity extends AppCompatActivity {
    private ImageView   mUserPhoto;
    private TextView    mUserEmail;
    private TextView    mUserInfo;
    private Button      mLogoutButton;

    private FirebaseAuth mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mUserPhoto      = findViewById(R.id.iv_user_user_photo);
        mUserEmail      = findViewById(R.id.tv_user_user_email);
        mUserInfo       = findViewById(R.id.tv_user_user_info);
        mLogoutButton   = findViewById(R.id.btn_user_user_logout);

        mAuthService = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
        FirebaseUser user = mAuthService.getCurrentUser();
        if (user != null) {
            mUserEmail.setText(user.getEmail());
            mUserInfo.setText(user.getDisplayName());
        }

        mLogoutButton.setOnClickListener(view -> {
            mAuthService.signOut();
            Intent loginIntent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });

    }
}