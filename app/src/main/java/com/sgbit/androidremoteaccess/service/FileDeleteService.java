package com.sgbit.androidremoteaccess.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.R;
import com.sgbit.androidremoteaccess.model.Document;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.io.File;
import java.util.List;

public class FileDeleteService extends Service {
    public FileDeleteService() {
    }
    Gson gson;
    User loggedInUser;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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
        getAllInActiveFiles();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void getAllInActiveFiles(){
        new AsyncTask<Void,Void,String>(){


            @Override
            protected String doInBackground(Void... params) {
                return new HttpManager().getData(getString(R.string.baseUrl)+"DocumentService/getDeletedDocumentsOfUser/"+loggedInUser.getMobileNumber());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s!=null){
                    Gson gson=Utility.getGson();
                    List<Document> documentList = gson.fromJson(s, new TypeToken<List<Document>>() {
                    }.getType());
                    for (int i=0;i<documentList.size();i++) {
                        deleteMyFile(documentList.get(i).getDocumentPath());
                    }
                }
            }
        }.execute();
    }

    private void deleteMyFile(String fileName){
        File fdelete = new File(fileName);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + fileName);
            } else {
                System.out.println("file not Deleted :" + fileName);
            }
        }
        else{
            System.out.println("file not exists :" + fileName);
        }
    }
}
