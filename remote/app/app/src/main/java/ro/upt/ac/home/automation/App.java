package ro.upt.ac.home.automation;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;

public class App extends Application {
    public static final String CHANNEL_1_ID = "Channel_1_ID";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
        channel1.setLightColor(Color.RED);
        channel1.setDescription("channel 1");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel1);
    }
}
