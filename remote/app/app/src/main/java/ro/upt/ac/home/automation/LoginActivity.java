package ro.upt.ac.home.automation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private TextView        mRegisterLink;
    private Button          mEmailLoginButton;
    private RelativeLayout  mGoogleLoginButton;
    private EditText        mEmailInput;
    private EditText        mPwdInput;
    private TextView        mLoginErr;

    private static final int RC_SIGN_IN = 1310;

    private GoogleSignInClient  mGoogleSignInClient;
    private FirebaseAuth        mFirebaseAuthService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mRegisterLink       = findViewById(R.id.tv_login_signup_clickable);
        mEmailLoginButton   = findViewById(R.id.btn_login_submit_data);
        mGoogleLoginButton  = findViewById(R.id.rl_login_google);
        mEmailInput         = findViewById(R.id.et_login_mail);
        mPwdInput           = findViewById(R.id.et_login_password);
        mLoginErr           = findViewById(R.id.tv_login_error);

        mFirebaseAuthService = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRegisterLink.setOnClickListener(view -> {
            Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerActivity);
        });

        mEmailLoginButton.setOnClickListener(view -> {
            // Firebase authentication w/ email and password
            String userMail         = mEmailInput.getText().toString();
            String userPassword     = mPwdInput.getText().toString();

            LoginRegisterErrors inputIsOk = isInputValid(userMail, userPassword);
            switch (inputIsOk) {
                case E_EMPTY_FIELDS:
                    mLoginErr.setText("Enter e-mail & password");
                    break;
                case E_PWD_INVALID_LENGTH:
                    mLoginErr.setText("Password's length must be greater or equal than 6 characters");
                    break;
                case E_EMAIL_WRONG_FORMAT:
                    mLoginErr.setText("Wrong e-mail format");
                    break;
                case AUTH_OK:
                    signInWithEmail(userMail, userPassword);
                    break;
                default:
                    mLoginErr.setText("Authentication failed unexpectedly");
                    break;
            }
        });

        mGoogleLoginButton.setOnClickListener(view -> {
            googleSignInStart();
            googleSignIn();
        });

}

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("googlesignin", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("googlesignin", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuthService.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signinwithcredential", "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuthService.getCurrentUser();
                            goToDashboard(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signingwithcredential", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Google authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void googleSignInStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithEmail(String userMail, String userPassword) {
        mFirebaseAuthService.signInWithEmailAndPassword(userMail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("signin", "signInWithEmail:success");
                            FirebaseUser user = mFirebaseAuthService.getCurrentUser();
                            goToDashboard(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void goToDashboard(FirebaseUser user) {
        Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }

    private LoginRegisterErrors isInputValid(String mail, String password) {
        if (mail.equals(""))        return LoginRegisterErrors.E_EMPTY_FIELDS;
        if (password.equals(""))    return LoginRegisterErrors.E_EMPTY_FIELDS;
        if (!mail.contains("@"))    return LoginRegisterErrors.E_EMAIL_WRONG_FORMAT;
        if (password.length() < 6)  return LoginRegisterErrors.E_PWD_INVALID_LENGTH;

        return LoginRegisterErrors.AUTH_OK;
    }
}