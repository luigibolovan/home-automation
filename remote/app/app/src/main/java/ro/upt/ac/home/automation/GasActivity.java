package ro.upt.ac.home.automation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
import ro.upt.ac.home.automation.requests.model.Controls;

public class GasActivity extends AppCompatActivity {
    private ImageView mDashboard;
    private ImageView mUserProfile;
    private TextView mMethaneConcentration;
    private FirebaseAuth mAuthService;
    private List<DataUnit> mListOfGasMeasurements;
    private BarChart mGasBarchart;
    private TextView mSelectedEntryDateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mUserProfile    = findViewById(R.id.iv_gas_user_photo);
        mDashboard      = findViewById(R.id.iv_gas_app_logo);
        mMethaneConcentration = findViewById(R.id.tv_methane_gas_concentration_value);
        mListOfGasMeasurements = new ArrayList<>();
        mAuthService = FirebaseAuth.getInstance();
        mGasBarchart = findViewById(R.id.barchart_gas);
        mSelectedEntryDateAndTime = findViewById(R.id.tv_methane_gas_selected_date_and_time);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserProfilePicture();

        RemoteServerApi rsa = ServiceBuilder.getInstance().buildService(RemoteServerApi.class);
        Call<List<Atmosphere>> atmosphereCall = rsa.getAllAtmospheres();
        atmosphereCall.enqueue(new Callback<List<Atmosphere>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Atmosphere>> call, Response<List<Atmosphere>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(GasActivity.this, "Error code gas", Toast.LENGTH_SHORT).show();
                } else {
                    List<Atmosphere> currentAtmospheres = response.body();

                    if (currentAtmospheres == null) {
                        Toast.makeText(GasActivity.this, "Request body null - gas", Toast.LENGTH_SHORT).show();
                    } else {
                        decryptGasAndUpdateUI(currentAtmospheres);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Atmosphere>> call, Throwable t) {
                Toast.makeText(GasActivity.this, "Something is wrong - gas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decryptGasAndUpdateUI(List<Atmosphere> atmospheres) {
        String key = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv = "C196C6DB0B0EDC093E4C5F513C75B75A";

        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBarray, 0, keyBarray.length, "AES");

        Cipher cipher = null;
        DataUnit maxConcentrationDataUnit = new DataUnit(0, "", "");
        byte[] methaneBytes = new byte[64];
        for (Atmosphere atmosphere: atmospheres) {
            try {
                cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                methaneBytes = cipher.doFinal(Base64.getDecoder().decode(atmosphere.getMethaneGasConcentration()));
            } catch (NoSuchAlgorithmException |
                    NoSuchPaddingException |
                    InvalidAlgorithmParameterException |
                    InvalidKeyException |
                    BadPaddingException |
                    IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            String methaneData = new String(methaneBytes);

            StringTokenizer methaneTokenizer = new StringTokenizer(methaneData, "_");
            int methaneConcentration = Integer.parseInt(methaneTokenizer.nextToken());
            if (atmospheres.indexOf(atmosphere) < atmospheres.size() - 9){
                if (maxConcentrationDataUnit.getDataUnitValue() < methaneConcentration) {
                    String methaneConcentrationDate = methaneTokenizer.nextToken();
                    String methaneConcentrationTime = methaneTokenizer.nextToken();
                    maxConcentrationDataUnit = new DataUnit(methaneConcentration, methaneConcentrationDate, methaneConcentrationTime);
                }
            } else {
                String methaneConcentrationDate = methaneTokenizer.nextToken();
                String methaneConcentrationTime = methaneTokenizer.nextToken();
                mListOfGasMeasurements.add(new DataUnit(methaneConcentration, methaneConcentrationDate, methaneConcentrationTime));
            }
        }
        if (maxConcentrationDataUnit.getDataUnitValue() > 0) {
            mListOfGasMeasurements.add(0, maxConcentrationDataUnit);
        }

        setUI();
    }

    private void setUI() {
        String textToDisplay = String.valueOf(mListOfGasMeasurements.get(mListOfGasMeasurements.size() - 1).getDataUnitValue()) + " ppm";
        mMethaneConcentration.setText(textToDisplay);

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (DataUnit dataUnit : mListOfGasMeasurements) {
            entries.add(new BarEntry(mListOfGasMeasurements.indexOf(dataUnit), dataUnit.getDataUnitValue()));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Gas concentration table");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(8f);

        BarData barData = new BarData(barDataSet);
        mGasBarchart.setFitBars(true);
        mGasBarchart.setData(barData);
        mGasBarchart.getDescription().setText(" ");
        mGasBarchart.animateY(2000);

        mGasBarchart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String date = mListOfGasMeasurements.get(((int)e.getX())).getDataUnitDate();
                String time = mListOfGasMeasurements.get(((int)e.getX())).getDataUnitTime();
                String toBeShown = "Date: " + date +  "  " + "Time: " + time;
                mSelectedEntryDateAndTime.setText(toBeShown);
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
            finish();
        });

        mUserProfile.setOnClickListener(view -> {
            Intent userProfileIntent = new Intent(GasActivity.this, UserActivity.class);
            startActivity(userProfileIntent);
        });

    }

    @Override
    public void onBackPressed() {
        Intent dashboardIntent = new Intent(GasActivity.this, DashboardActivity.class);
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