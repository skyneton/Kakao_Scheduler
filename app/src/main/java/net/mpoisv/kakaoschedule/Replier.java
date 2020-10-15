package net.mpoisv.kakaoschedule;

import android.app.Notification;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Replier {
    private Notification.Action session;
    private Context context;

    public Replier(Notification.Action action, Context context) {
        this.session = action;
        this.context = context;
    }

    public void reply(String message) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        for(RemoteInput resultKey : this.session.getRemoteInputs())
            bundle.putCharSequence(resultKey.getResultKey(), message);

        RemoteInput.addResultsToIntent(this.session.getRemoteInputs(), intent, bundle);

        try{
            this.session.actionIntent.send(context, 0, intent);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void reply(String room, String message) {
        reply(room + KakaoInfo.COMPRESS + "\n" + message);
    }
}
