package net.mpoisv.kakaoschedule;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import net.mpoisv.kakaoschedule.net.mpoisv.kakaoschedule.db.DataBaseHelper;

import java.util.Locale;
import java.util.TimeZone;

public class KakaoTalkListener extends NotificationListenerService {

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();

        return super.onStartCommand(intent, flags, START_REDELIVER_INTENT);
    }*/

    @Override
    public void onCreate() {
        super.onCreate();

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        Locale.setDefault(Locale.KOREA);

        ResponseKakao.dataBaseHelper = new DataBaseHelper(getApplicationContext());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        if(KakaoInfo.getKakaoPackageName().contains(sbn.getPackageName())) {
            Bundle extras = sbn.getNotification().extras;
            if(extras.getString(Notification.EXTRA_TITLE) == null ||
                    extras.getString(Notification.EXTRA_TEXT) == null) return;

            KakaoData kakaoData = new KakaoData();

            kakaoData.sender = extras.getString(Notification.EXTRA_TITLE);
            kakaoData.message = extras.getString(Notification.EXTRA_TEXT);
            kakaoData.room = extras.getString(Notification.EXTRA_SUB_TEXT);

            if(sbn.getNotification().actions == null) return;

            for(Notification.Action action : sbn.getNotification().actions) {
                if(action.getRemoteInputs() != null && action.getRemoteInputs().length > 0 && (action.title.toString().toLowerCase().contains("reply") || action.title.toString().toLowerCase().contains("답장"))) {
                    ResponseKakao.response(kakaoData.room, kakaoData.message, kakaoData.sender, kakaoData.room != null, new Replier(action, getApplicationContext()));

                    break;
                }
            }
        }
    }

    /*void startForegroundService() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("kakaoschedule", "카카오톡 일정 봇", notificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(getApplicationContext(), "kakaoschedule");
        }else
            builder = new NotificationCompat.Builder(getApplicationContext());

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle("카카오톡 일정 봇이 실행중입니다.")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        startForeground(1234, builder.build());
    }*/

    class KakaoData {
        public String room;
        public String sender;
        public String message;
    }
}
