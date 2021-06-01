package ro.upt.ac.home.automation;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.upt.ac.home.automation.models.DataUnit;
import ro.upt.ac.home.automation.requests.RemoteServerApi;
import ro.upt.ac.home.automation.requests.ServiceBuilder;
import ro.upt.ac.home.automation.requests.model.Controls;

public class LightsActivity extends AppCompatActivity {
    private ImageView mPowerBtn;
    private ImageView mUserPhoto;
    private ImageView mDashboard;
    private RecyclerView mLightsRecyclerView;
    private Controls mLatestControl;

    private FirebaseAuth mAuthService;

    private static final int POWER_ON                  = 1;
    private static final int POWER_OFF                 = 0;
    private int SWITCH_VALUE                           = POWER_OFF;

    private List<DataUnit> listOfLights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mPowerBtn  = findViewById(R.id.iv_lights_power_btn);
        mUserPhoto = findViewById(R.id.iv_lights_user_photo);
        mDashboard = findViewById(R.id.iv_lights_in_app_logo);
        mLightsRecyclerView = findViewById(R.id.rv_lights_stats);
        mAuthService = FirebaseAuth.getInstance();
        listOfLights = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserProfilePicture();
        RemoteServerApi rsa = ServiceBuilder.getInstance().buildService(RemoteServerApi.class);
        Call<List<Controls>> controlCall = rsa.getAllControls();
        controlCall.enqueue(new Callback<List<Controls>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Controls>> call, Response<List<Controls>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LightsActivity.this, "Error code control", Toast.LENGTH_SHORT).show();
                } else {
                    List<Controls> currentControls = response.body();

                    if (currentControls == null) {
                        Toast.makeText(LightsActivity.this, "Request body null - control", Toast.LENGTH_SHORT).show();
                    } else {
                        decryptControlAndUpdateUI(currentControls);
                        mLightsRecyclerView.setAdapter(new ControlsListAdapter(LightsActivity.this, listOfLights));
                        mLightsRecyclerView.setLayoutManager(new LinearLayoutManager(LightsActivity.this));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Controls>> call, Throwable t) {
                Toast.makeText(LightsActivity.this, "Something is wrong - control", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decryptControlAndUpdateUI(List<Controls> controls) {
        String key = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv = "C196C6DB0B0EDC093E4C5F513C75B75A";

        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBarray, 0, keyBarray.length, "AES");

        for (int i = controls.size() - 1; i > 0; i --) {
            if ( i == controls.size() - 1 || !controls.get(i).getLights().equals(controls.get(i + 1).getLights())) {
                Cipher cipher = null;
                byte[] lightsBytes = new byte[64];
                try {
                    cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                    lightsBytes = cipher.doFinal(Base64.getDecoder().decode(controls.get(i).getLights()));
                } catch (NoSuchAlgorithmException |
                        NoSuchPaddingException |
                        InvalidAlgorithmParameterException |
                        InvalidKeyException |
                        BadPaddingException |
                        IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
                String lightsData = new String(lightsBytes);
                StringTokenizer doorlockTokenizer = new StringTokenizer(lightsData, "_");
                String doorLockValue = doorlockTokenizer.nextToken();
                String doorLockDate = doorlockTokenizer.nextToken();
                String doorLockTime = doorlockTokenizer.nextToken();
                listOfLights.add(new DataUnit(Integer.parseInt(doorLockValue), doorLockDate, doorLockTime));
            }
        }
        if (listOfLights.get(0).getDataUnitValue() == POWER_ON) {
            mPowerBtn.setBackgroundResource(R.drawable.on_button);
            SWITCH_VALUE = POWER_ON;
        } else {
            mPowerBtn.setBackgroundResource(R.drawable.off_button);
            SWITCH_VALUE = POWER_OFF;
        }
        mLatestControl = controls.get(0);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        mLightsRecyclerView.setAdapter(new ControlsListAdapter(this, listOfLights));
        mLightsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPowerBtn.setOnClickListener(view -> {
            if (SWITCH_VALUE == POWER_OFF) {
                mPowerBtn.setBackgroundResource(R.drawable.on_button);
                SWITCH_VALUE = POWER_ON;
            } else if (SWITCH_VALUE == POWER_ON) {
                mPowerBtn.setBackgroundResource(R.drawable.off_button);
                SWITCH_VALUE = POWER_OFF;
            }

            String date = LocalDate.now().toString();
            String time = LocalTime.now().toString();
            String lightsRecord = SWITCH_VALUE + "_" + date + "_" + time;
            encryptThenSend(lightsRecord);
        });

        mDashboard.setOnClickListener(view -> {
            Intent dashboardIntent = new Intent(LightsActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        });

        mUserPhoto.setOnClickListener(view -> {
            Intent userIntent = new Intent(LightsActivity.this, UserActivity.class);
            startActivity(userIntent);
        });
    }

    @Override
    public void onBackPressed() {
        Intent dashboardIntent = new Intent(LightsActivity.this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void encryptThenSend(String lights) {
        String cipherLights = null;

        String key = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv = "C196C6DB0B0EDC093E4C5F513C75B75A";

        int blockSize = 16;
        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBarray, 0, keyBarray.length, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] cipherText = cipher.doFinal(pad(lights, blockSize).getBytes());
            cipherLights = Base64.getEncoder().encodeToString(cipherText);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidAlgorithmParameterException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        Controls controlToPost = new Controls(mLatestControl.getDoorLock(), cipherLights);
        RemoteServerApi rsa = ServiceBuilder.getInstance().buildService(RemoteServerApi.class);
        Call<Controls> postControls = rsa.setControl(controlToPost);

        postControls.enqueue(new Callback<Controls>() {
            @Override
            public void onResponse(Call<Controls> call, Response<Controls> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LightsActivity.this, "Error code control - post", Toast.LENGTH_SHORT).show();
                } else {
                    // ok
                }
            }

            @Override
            public void onFailure(Call<Controls> call, Throwable t) {
                // fail
            }
        });
    }

    public static String pad(String s, int BLOCK_SIZE) {
        String new_s = s;
        for (int i = 0; i < BLOCK_SIZE - s.length() % BLOCK_SIZE; i++) {
            new_s = new_s + ((char) (BLOCK_SIZE - s.length() % BLOCK_SIZE));
        }
        return new_s;
    }
}