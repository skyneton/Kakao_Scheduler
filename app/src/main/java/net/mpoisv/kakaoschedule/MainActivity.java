package net.mpoisv.kakaoschedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.mpoisv.kakaoschedule.net.mpoisv.kakaoschedule.db.DataBaseHelper;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SYSTEM_NOTIFICATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkNotificationPermission())
            NotificationPermission();
    }

    private boolean checkNotificationPermission() {
        return NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext()).contains(getPackageName());
    }

    private void NotificationPermission() {
        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);

        startActivityForResult(intent, REQUEST_SYSTEM_NOTIFICATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case REQUEST_SYSTEM_NOTIFICATION:
                if(!checkNotificationPermission())
                    finish();
                break;
        }
    }
}
