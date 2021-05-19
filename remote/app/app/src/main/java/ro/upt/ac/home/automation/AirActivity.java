package ro.upt.ac.home.automation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
import ro.upt.ac.home.automation.requests.model.Atmosphere;

public class AirActivity extends AppCompatActivity {
    private ImageView mDashboard;
    private ImageView mUserProfile;
    private TextView mTemperature;
    private TextView mHumidity;
    private List<DataUnit> mTemperatureList;
    private List<DataUnit> mHumidityList;
    private BarChart mBarchartTemperature;
    private BarChart mBarchartHumidity;
    private TextView mTemperatureDateAndTime;
    private TextView mHumidityDateAndTime;

    private FirebaseAuth mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mDashboard      = findViewById(R.id.iv_air_app_logo);
        mUserProfile    = findViewById(R.id.iv_temperature_user_photo);
        mTemperature    = findViewById(R.id.tv_air_temperature_value);
        mHumidity       = findViewById(R.id.tv_air_humidity_value);
        mBarchartTemperature = findViewById(R.id.barchart_temperature);
        mBarchartHumidity = findViewById(R.id.barchart_humidity);
        mTemperatureDateAndTime = findViewById(R.id.tv_barchart_temperature);
        mHumidityDateAndTime = findViewById(R.id.tv_barchart_humidty);
        mTemperatureList = new ArrayList<>();
        mHumidityList = new ArrayList<>();
        mAuthService = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserProfilePicture();
        RemoteServerApi rsa = ServiceBuilder.getInstance().buildService(RemoteServerApi.class);
        Call<List<Atmosphere>> atmosphereCall = rsa.getAtmospheres();
        atmosphereCall.enqueue(new Callback<List<Atmosphere>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Atmosphere>> call, Response<List<Atmosphere>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AirActivity.this, "Error code air", Toast.LENGTH_SHORT).show();
                } else {
                    List<Atmosphere> currentAtmospheres = response.body();

                    if (currentAtmospheres == null) {
                        Toast.makeText(AirActivity.this, "Request body null - air", Toast.LENGTH_SHORT).show();
                    } else {
                        decryptAirDataAndUpdateUI(currentAtmospheres);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Atmosphere>> call, Throwable t) {
                Toast.makeText(AirActivity.this, "Something is wrong - air", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decryptAirDataAndUpdateUI(List<Atmosphere> atmospheres) {
        String key = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv = "C196C6DB0B0EDC093E4C5F513C75B75A";

        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBarray, 0, keyBarray.length, "AES");

        Cipher cipher = null;
        DataUnit maxConcentrationDataUnit = new DataUnit(0, "", "");
        byte[] tempBytes = new byte[64];
        byte[] humBytes = new byte[64];
        for (Atmosphere atmosphere : atmospheres) {
            try {
                cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                tempBytes = cipher.doFinal(Base64.getDecoder().decode(atmosphere.getTemperature()));
                humBytes = cipher.doFinal(Base64.getDecoder().decode(atmosphere.getHumidity()));
            } catch (NoSuchAlgorithmException |
                    NoSuchPaddingException |
                    InvalidAlgorithmParameterException |
                    InvalidKeyException |
                    BadPaddingException |
                    IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            String tempData = new String(tempBytes);
            String humData = new String(humBytes);

            StringTokenizer tempTokenizer = new StringTokenizer(tempData, "_");
            StringTokenizer humTokenizer = new StringTokenizer(humData, "_");
            mTemperatureList.add(new DataUnit(Integer.parseInt(tempTokenizer.nextToken()), tempTokenizer.nextToken(), tempTokenizer.nextToken()));
            mHumidityList.add(new DataUnit(Integer.parseInt(humTokenizer.nextToken()), humTokenizer.nextToken(), humTokenizer.nextToken()));

            setUI();
        }
    }

    private void setUI() {
        String temperatureToDisplay = mTemperatureList.get(0).getDataUnitValue() + " °C";
        mTemperature.setText(temperatureToDisplay);

        String humidityToDisplay = mHumidityList.get(0).getDataUnitValue() + " %";
        mHumidity.setText(humidityToDisplay);


        ArrayList<BarEntry> temperatureEntries = new ArrayList<>();
        for (DataUnit dataUnit : mTemperatureList) {
            temperatureEntries.add(new BarEntry(mTemperatureList.indexOf(dataUnit), dataUnit.getDataUnitValue()));
        }

        ArrayList<BarEntry> humidityEntries = new ArrayList<>();
        for (DataUnit dataUnit : mHumidityList) {
            humidityEntries.add(new BarEntry(mHumidityList.indexOf(dataUnit), dataUnit.getDataUnitValue()));
        }

        BarDataSet barDataSetTemperature = new BarDataSet(temperatureEntries, "Temperature");
        barDataSetTemperature.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSetTemperature.setValueTextColor(Color.BLACK);
        barDataSetTemperature.setValueTextSize(8f);

        BarDataSet barDataSetHumidity = new BarDataSet(humidityEntries, "Humidity");
        barDataSetHumidity.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSetHumidity.setValueTextColor(Color.BLACK);
        barDataSetHumidity.setValueTextSize(8f);


        BarData barDataTemperature = new BarData(barDataSetTemperature);
        mBarchartTemperature.setFitBars(true);
        mBarchartTemperature.setData(barDataTemperature);
        mBarchartTemperature.getDescription().setText(" ");
        mBarchartTemperature.animateY(2000);

        mBarchartTemperature.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String date = mTemperatureList.get(((int)e.getX())).getDataUnitDate();
                String time = mTemperatureList.get(((int)e.getX())).getDataUnitTime();
                String toBeShown = "Date: " + date +  "  " + "Time: " + time;
                mTemperatureDateAndTime.setText(toBeShown);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        BarData barDataHumidity = new BarData(barDataSetHumidity);
        mBarchartHumidity.setFitBars(true);
        mBarchartHumidity.setData(barDataHumidity);
        mBarchartHumidity.getDescription().setText(" ");
        mBarchartHumidity.animateY(2000);

        mBarchartHumidity.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String date = mHumidityList.get(((int)e.getX())).getDataUnitDate();
                String time = mHumidityList.get(((int)e.getX())).getDataUnitTime();
                String toBeShown = "Date: " + date +  "  " + "Time: " + time;
                mHumidityDateAndTime.setText(toBeShown);
            }

            @Override
            public void onNothingSelected() {

            }
        });
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

        mDashboard.setOnClickListener(view -> {
            Intent dashboardIntent = new Intent(AirActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        });

        mUserProfile.setOnClickListener(view -> {
            Intent userProfileIntent = new Intent(AirActivity.this, UserActivity.class);
            startActivity(userProfileIntent);
        });

    }

    @Override
    public void onBackPressed() {
        Intent dashboardIntent = new Intent(AirActivity.this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
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
}