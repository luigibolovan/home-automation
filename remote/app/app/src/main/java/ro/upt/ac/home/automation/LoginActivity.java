package ro.upt.ac.home.automation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerLink = findViewById(R.id.tv_login_signup_clickable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerLink.setOnClickListener(view -> {
            Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerActivity);
        });
    }
}