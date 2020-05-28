package com.sgbit.androidremoteaccess.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.R;
import com.sgbit.androidremoteaccess.model.CallLog;
import com.sgbit.androidremoteaccess.model.Document;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;
import me.everything.providers.android.telephony.Conversation;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;

public class CallLogService extends Service {
    public CallLogService() {

    }
    Gson gson;
    User loggedInUser;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
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
        getCallLogs();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    void getCallLogs(){
        /*TelephonyProvider telephonyProvider = new TelephonyProvider(getApplicationContext());
        List<Sms> smses = telephonyProvider.getSms(TelephonyProvider.Filter.INBOX).getList();

        for (int i=0;i<smses.size();i++){
            System.out.println(smses.get(i).body);
        }

        List<Conversation> conversations = telephonyProvider.getConversations().getList();
        System.out.println("conversations");
        for (int i=0;i<conversations.size();i++){
            System.out.println(conversations.get(i));
        }
        */
        CallsProvider callsProvider = new CallsProvider(getApplicationContext());
        List<Call> callList = callsProvider.getCalls().getList();

        final List<CallLog> callLogList = new ArrayList<>();
        for (int i=0;i<callList.size();i++){
            System.out.println(callList.get(i).name +"  "+callList.get(i).number+" "+new Date(callList.get(i).callDate));
            CallLog callLog = new CallLog();
            callLog.setCallDate(new Date(callList.get(i).callDate));
            callLog.setCallerName(callList.get(i).name);
            callLog.setCallerNumber(callList.get(i).number);
            callLog.setUserId(loggedInUser.getMobileNumber());

            callLogList.add(callLog);

        }

        if(callLogList.size()>0){
            new AsyncTask<Void,Void,String>(){

                @Override
                protected String doInBackground(Void... voids) {
                    String callLogListJson = gson.toJson(callLogList,new TypeToken<List<CallLog>>() {
                    }.getType());
                    return new HttpManager().postData(getString(R.string.baseUrl)+"CallLogsService/addCallLogsList",callLogListJson);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    System.out.println(s);
                }
            }.execute();
        }


    }



}
