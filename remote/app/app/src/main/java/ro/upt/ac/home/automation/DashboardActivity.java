package ro.upt.ac.home.automation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
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
import ro.upt.ac.home.automation.requests.RemoteServerApi;
import ro.upt.ac.home.automation.requests.ServiceBuilder;
import ro.upt.ac.home.automation.requests.model.Atmosphere;
import ro.upt.ac.home.automation.requests.model.Controls;

public class DashboardActivity extends AppCompatActivity {
    private RelativeLayout mLightStats;
    private RelativeLayout mAirStats;
    private RelativeLayout mDoorLockStats;
    private RelativeLayout mGasStats;
    private ImageView      mUserProfile;
    private TextView       mTemperatureInfo;
    private TextView       mHumidityInfo;
    private TextView       mMethaneGasInfo;
    private Switch         mDoorLockSwitch;
    private Switch         mLightsSwitch;

    private TextView       mTemperatureStatic;

    private FirebaseAuth   mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mLightStats      = findViewById(R.id.rl_dashboard_stats_lights);
        mAirStats        = findViewById(R.id.rl_dashboard_stats_air_humidity);
        mDoorLockStats   = findViewById(R.id.rl_dashboard_stats_doorlock);
        mGasStats        = findViewById(R.id.rl_dashboard_stats_methane_gas);
        mUserProfile     = findViewById(R.id.iv_dashboard_user_photo);
        mTemperatureInfo = findViewById(R.id.tv_dashboard_temperature_info);
        mHumidityInfo    = findViewById(R.id.tv_dashboard_humidity_info);
        mMethaneGasInfo  = findViewById(R.id.tv_dashboard_methane_gas_concentration_info);
        mDoorLockSwitch  = findViewById(R.id.switch_doorLock);
        mLightsSwitch    = findViewById(R.id.switch_lights);


        mTemperatureStatic = findViewById(R.id.tv_dashboard_info_and_control_static);


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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        mLightsSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.getId() == mLightsSwitch.getId()) {
                if (b) {
                    String date = LocalDate.now().toString();
                    String time = LocalTime.now().toString();
                    String lights = "1_" + date + "_" + time;

                    RemoteServerApi rsa = ServiceBuilder.getInstance().buildService(RemoteServerApi.class);

                    Call<Controls> controlCall = rsa.getControl();
                    controlCall.enqueue(new Callback<Controls>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call<Controls> call, Response<Controls> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(DashboardActivity.this, "Error code control", Toast.LENGTH_SHORT).show();
                            } else {
                                Controls currentControl = response.body();

                                if (currentControl == null) {
                                    Toast.makeText(DashboardActivity.this, "Request body null - control", Toast.LENGTH_SHORT).show();
                                } else {
                                    encryptThenSend(lights, currentControl.getDoorLock(), true, false);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Controls> call, Throwable t) {
                            Toast.makeText(DashboardActivity.this, "Something is wrong - control", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // don't change the doorlock status and date

                    // then encrypt

                    // then do for door lock as well

                    // then do charts

                    // then do notifications(maybe last thing to do; hardware awaits as well to be done)

                }
            }
        });

        RemoteServerApi rsa = ServiceBuilder.getInstance().buildService(RemoteServerApi.class);

        Call<Atmosphere> atmosphereCall = rsa.getAtmosphere();
        atmosphereCall.enqueue(new Callback<Atmosphere>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Atmosphere> call, Response<Atmosphere> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(DashboardActivity.this, "Error code", Toast.LENGTH_SHORT).show();
                } else {
                    Atmosphere currentAtmosphere = response.body();
                    if (currentAtmosphere == null) {
                        Toast.makeText(DashboardActivity.this, "Request body null", Toast.LENGTH_SHORT).show();
                    } else {
                        decryptAtmosphereAndUpdateUI(currentAtmosphere);
                    }
                }
            }

            @Override
            public void onFailure(Call<Atmosphere> call, Throwable t) {
                mTemperatureStatic.setText(t.getMessage());
            }
        });

        Call<Controls> controlCall = rsa.getControl();
        controlCall.enqueue(new Callback<Controls>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Controls> call, Response<Controls> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(DashboardActivity.this, "Error code control", Toast.LENGTH_SHORT).show();
                } else {
                    Controls currentControl = response.body();

                    if (currentControl == null) {
                        Toast.makeText(DashboardActivity.this, "Request body null - control", Toast.LENGTH_SHORT).show();
                    } else {
                        decryptControlAndUpdateUI(currentControl);
                    }
                }
            }

            @Override
            public void onFailure(Call<Controls> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Something is wrong - control", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void encryptThenSend(String lights, String doorLock, boolean lightsChanged, boolean doorlockChanged) {
        String cipherLights = null;
        String cipherDoorLock = null;

        String key      = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv       = "C196C6DB0B0EDC093E4C5F513C75B75A";
        int blockSize   = 16;
        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec (keyBarray, 0, keyBarray.length, "AES");
        Cipher cipher = null;
        if (lightsChanged) {
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
        }

        if (doorlockChanged) {
            try {
                cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
                byte[] cipherText = cipher.doFinal(pad(doorLock, blockSize).getBytes());
                cipherDoorLock = Base64.getEncoder().encodeToString(cipherText);
            } catch (NoSuchAlgorithmException |
                    NoSuchPaddingException |
                    InvalidAlgorithmParameterException |
                    InvalidKeyException |
                    BadPaddingException |
                    IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
        Controls control = null;
        if (lightsChanged && !doorlockChanged) {
            if (cipherLights == null) {
                // error
            } else {
                control = new Controls(doorLock, cipherLights);
            }
        }

        if (!lightsChanged && doorlockChanged) {
            if (cipherDoorLock == null) {
                // error
            } else {
                control = new Controls(cipherDoorLock, lights);
            }
        }
        if (control != null) {
            RemoteServerApi rsa = ServiceBuilder.getInstance().buildService(RemoteServerApi.class);
            Call<Void> postControls = rsa.setControl(control);

            postControls.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(DashboardActivity.this, "Error code control - post", Toast.LENGTH_SHORT).show();
                    } else {
                        // request sent
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    mTemperatureStatic.setText(t.getMessage());
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decryptControlAndUpdateUI(Controls control) {
        String key  = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv   = "C196C6DB0B0EDC093E4C5F513C75B75A";

        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec (keyBarray, 0, keyBarray.length, "AES");

        Cipher cipher        = null;
        byte[] doorlockBytes = new byte[64];
        byte[] lightsBytes   = new byte[64];
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            doorlockBytes = cipher.doFinal(Base64.getDecoder().decode(control.getDoorLock()));
            lightsBytes = cipher.doFinal(Base64.getDecoder().decode(control.getLights()));
        }  catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidAlgorithmParameterException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String doorLockData = new String(doorlockBytes);
        String lightsData   = new String(lightsBytes);

        StringTokenizer doorlockTokenizer = new StringTokenizer(doorLockData, "_");
        StringTokenizer lightsTokenizer   = new StringTokenizer(lightsData, "_");

        String doorLockValue    = doorlockTokenizer.nextToken();
        String lightsValue      = lightsTokenizer.nextToken();

        mDoorLockSwitch.setChecked(doorLockValue.equals("1"));
        mLightsSwitch.setChecked(lightsValue.equals("1"));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decryptAtmosphereAndUpdateUI(Atmosphere atmosphere) {
        String key  = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv   = "C196C6DB0B0EDC093E4C5F513C75B75A";

        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec (keyBarray, 0, keyBarray.length, "AES");

        Cipher cipher           = null;
        byte[] temperatureBytes = new byte[64];
        byte[] humidityBytes    = new byte[64];
        byte[] methaneBytes     = new byte[64];
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            humidityBytes = cipher.doFinal(Base64.getDecoder().decode(atmosphere.getHumidity()));
            temperatureBytes = cipher.doFinal(Base64.getDecoder().decode(atmosphere.getTemperature()));
            methaneBytes = cipher.doFinal(Base64.getDecoder().decode(atmosphere.getMethaneGasConcentration()));
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidAlgorithmParameterException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String temperatureData  = new String(temperatureBytes);
        String humidityData     = new String(humidityBytes);
        String methaneData      = new String(methaneBytes);

        StringTokenizer temperatureTokenizer    = new StringTokenizer(temperatureData, "_");
        StringTokenizer humidityTokenizer       = new StringTokenizer(humidityData, "_");
        StringTokenizer methaneTokenizer        = new StringTokenizer(methaneData, "_");

        String temperatureValue          = temperatureTokenizer.nextToken() + " C";
        String humidityValue             = humidityTokenizer.nextToken() + " %";
        String methaneConcentrationValue = methaneTokenizer.nextToken() + " ppm";

        mTemperatureInfo.setText(temperatureValue);
        mHumidityInfo.setText(humidityValue);
        mMethaneGasInfo.setText(methaneConcentrationValue);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String pad(String s, int BLOCK_SIZE) {
        String new_s = s;
        for (int i = 0; i < BLOCK_SIZE - s.length() % BLOCK_SIZE; i++)
        {
            new_s = new_s + ((char)(BLOCK_SIZE - s.length() % BLOCK_SIZE));
        }
        return new_s;
    }
}