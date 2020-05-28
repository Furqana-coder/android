package com.sgbit.androidremoteaccess.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.R;
import com.sgbit.androidremoteaccess.model.Contact;
import com.sgbit.androidremoteaccess.model.SMS;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;

public class SMSService extends Service {
    public SMSService() {
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
        Toast.makeText(this, " MyService Created ", Toast.LENGTH_LONG).show();
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String userJson = sessionManager.getString("loggedInUser");
        gson = Utility.getGson();
        loggedInUser = gson.fromJson(userJson,User.class);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();
        getAllSMS();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void getAllSMS(){
        TelephonyProvider telephonyProvider = new TelephonyProvider(getApplicationContext());
        List<Sms> smses = telephonyProvider.getSms(TelephonyProvider.Filter.ALL).getList();
        List<SMS> smsList = new ArrayList<>();
        System.out.println("getSMS");
        for (int i=0;i<smses.size();i++){
            SMS sms = new SMS();
            System.out.println(smses.get(i).body+" "+smses.get(i).address+" "+smses.get(i).receivedDate+" "+smses.get(i).sentDate);
            sms.setAddress(smses.get(i).address);
            sms.setBody(smses.get(i).body);
            sms.setReceivedDate(new Date(smses.get(i).receivedDate));
            sms.setSentDate(new Date(smses.get(i).sentDate));
            sms.setUserId(loggedInUser.getMobileNumber());
            smsList.add(sms);
        }

        if(!smsList.isEmpty()){
            final String smsListJson = gson.toJson(smsList,new TypeToken<List<SMS>>() {
            }.getType());

            new AsyncTask<Void,Void,String>(){

                @Override
                protected String doInBackground(Void... voids) {
                    return new HttpManager().postData(getString(R.string.baseUrl)+"SMSService/addSMSList",smsListJson);
                }
            }.execute();
        }
    }
}
