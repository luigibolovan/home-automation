package ro.upt.ac.home.automation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText     mEmailUserToBe;
    private EditText     mPwdUserToBe;
    private Button       mRegisterBtn;
    private TextView     mInputErrorTView;

    private FirebaseAuth mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailUserToBe   = findViewById(R.id.et_signup_mail);
        mPwdUserToBe     = findViewById(R.id.et_signup_password);
        mRegisterBtn     = findViewById(R.id.btn_signup_submit_data);
        mInputErrorTView = findViewById(R.id.tv_register_error);

        mAuthService = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRegisterBtn.setOnClickListener(view -> {
            String userToBeEmail    = mEmailUserToBe.getText().toString();
            String userToBePassword = mPwdUserToBe.getText().toString();
            LoginRegisterErrors isInputValid = validateUserInput(userToBeEmail, userToBePassword);
            switch (isInputValid) {
                case AUTH_OK:
                    registerUser(userToBeEmail, userToBePassword);
                    break;
                case E_EMAIL_WRONG_FORMAT:
                    mInputErrorTView.setText("E-mail has a wrong format");
                    break;
                case E_EMPTY_FIELDS:
                    mInputErrorTView.setText("Enter e-mail & password");
                    break;
                case E_PWD_INVALID_LENGTH:
                    mInputErrorTView.setText("Password's length should be greater or equal than 6 characters");
                    break;
                default:
                    mInputErrorTView.setText("Register failed unexpectedly");
                    break;
            }
        });
    }

    private void registerUser(String userToBeEmail, String userToBePassword) {
        mAuthService.createUserWithEmailAndPassword(userToBeEmail, userToBePassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("register", "createUserWithEmail:success");
                            FirebaseUser user = mAuthService.getCurrentUser();
                            goToLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("register", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Register failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToLogin() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private LoginRegisterErrors validateUserInput(String mail, String password) {
        if (mail.equals(""))        return LoginRegisterErrors.E_EMPTY_FIELDS;
        if (password.equals(""))    return LoginRegisterErrors.E_EMPTY_FIELDS;
        if (!mail.contains("@"))    return LoginRegisterErrors.E_EMAIL_WRONG_FORMAT;
        if (password.length() < 6)  return LoginRegisterErrors.E_PWD_INVALID_LENGTH;

        return LoginRegisterErrors.AUTH_OK;
    }
}