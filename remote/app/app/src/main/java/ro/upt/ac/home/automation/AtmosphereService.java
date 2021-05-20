package ro.upt.ac.home.automation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
import ro.upt.ac.home.automation.requests.RemoteServerApi;
import ro.upt.ac.home.automation.requests.ServiceBuilder;
import ro.upt.ac.home.automation.requests.model.Atmosphere;

import static ro.upt.ac.home.automation.App.CHANNEL_1_ID;

public class AtmosphereService extends JobService {
    public static final String TAG = "AtmosphereService";
    private boolean jobCancelled   = false;
    private JobParameters currentJobParams;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Atmosphere job started");
        currentJobParams = jobParameters;
        doBackgroundTasks(jobParameters);
        return true;
    }

    private void doBackgroundTasks(JobParameters jobParameters) {
        new Thread( () -> {
            if (jobCancelled) {
                return;
            }
            getAtmosphereDataAndUpdateUI();

        }).start();
    }

    private void getAtmosphereDataAndUpdateUI() {
        RemoteServerApi rsa = ServiceBuilder.getInstance().buildService(RemoteServerApi.class);

        Call<Atmosphere> atmosphereCall = rsa.getAtmosphere();
        atmosphereCall.enqueue(new Callback<Atmosphere>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Atmosphere> call, Response<Atmosphere> response) {
                if (!response.isSuccessful()) {

                } else {
                    Atmosphere currentAtmosphere = response.body();
                    if (currentAtmosphere == null) {

                    } else {
                        if (jobCancelled) {
                            return;
                        }
                        decryptAtmosphereAndCheck(currentAtmosphere);
                    }
                }
            }

            @Override
            public void onFailure(Call<Atmosphere> call, Throwable t) {

            }
        });
    }

    private void decryptAtmosphereAndCheck(Atmosphere atmosphere) {
        String key = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv = "C196C6DB0B0EDC093E4C5F513C75B75A";
        boolean allOk = true;

        if (jobCancelled) {
            return;
        }

        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec(keyBarray, 0, keyBarray.length, "AES");

        Cipher cipher = null;
        byte[] temperatureBytes = new byte[64];
        byte[] humidityBytes = new byte[64];
        byte[] methaneBytes = new byte[64];
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
        String temperatureData = new String(temperatureBytes);
        String humidityData = new String(humidityBytes);
        String methaneData = new String(methaneBytes);

        StringTokenizer temperatureTokenizer = new StringTokenizer(temperatureData, "_");
        StringTokenizer humidityTokenizer = new StringTokenizer(humidityData, "_");
        StringTokenizer methaneTokenizer = new StringTokenizer(methaneData, "_");

        int tempValue = Integer.parseInt(temperatureTokenizer.nextToken());
        int humidityValue = Integer.parseInt(humidityTokenizer.nextToken());
        int methaneConcentrationValue = Integer.parseInt(methaneTokenizer.nextToken());

        if (tempValue > 40) {
            allOk = false;
            sendTemperatureNotification();
        }

        if (humidityValue > 90) {
            allOk = false;
            sendHumidityNotification();
        }

        if (methaneConcentrationValue > 700) {
            allOk = false;
            sendMethaneGasLeakNotification();
        }

        if (allOk) {
            Log.d(TAG, "Job finished with no events");
            jobFinished(currentJobParams, false);
        }
    }

    private void sendMethaneGasLeakNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Intent activityIntent = new Intent(this, DashboardActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Warning")
                .setAutoCancel(true)
                .setContentText("Possible methane gas leak")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setColor(Color.YELLOW)
                .build();

        notificationManager.notify(1, notification);
        jobFinished(currentJobParams, false);
    }

    private void sendHumidityNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Intent activityIntent = new Intent(this, DashboardActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Warning")
                .setContentText("Humidity level over 90%")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setColor(Color.BLUE)
                .build();

        notificationManager.notify(1, notification);
        jobFinished(currentJobParams, false);
    }

    private void sendTemperatureNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Intent activityIntent = new Intent(this, DashboardActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Warning")
                .setContentText("Temperature over 40 degrees celsius")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setColor(Color.RED)
                .build();

        notificationManager.notify(1, notification);
        jobFinished(currentJobParams, false);
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

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job stopped unexpectedly");
        jobCancelled = true;
        return true;
    }
}
