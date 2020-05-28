package com.sgbit.androidremoteaccess.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

public class SilentModeService extends Service {
    public SilentModeService() {
    }
    Gson gson;
    User loggedInUser;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, " MyService Created ", Toast.LENGTH_LONG).show();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String userJson = sessionManager.getString("loggedInUser");
        gson = Utility.getGson();
        loggedInUser = gson.fromJson(userJson,User.class);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();
        if(loggedInUser.getMode().equals("G")) {
            final AudioManager mobilemode = (AudioManager) getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
            mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            int currentStreamRing = mobilemode.getStreamVolume(AudioManager.STREAM_RING);

            mobilemode.setStreamVolume(AudioManager.STREAM_RING, currentStreamRing, AudioManager.FLAG_PLAY_SOUND);

            //Toast.makeText(getApplicationContext(),"General mode activated ",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
