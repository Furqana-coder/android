package com.sgbit.androidremoteaccess;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.model.CallLog;
import com.sgbit.androidremoteaccess.model.Document;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;


public class FragmentOtherAccountCallLog extends Fragment {


    public FragmentOtherAccountCallLog() {
        // Required empty public constructor
    }
    private View view;
    User loggedInUser;
    ListView lvCallLogs;
    Gson gson ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getContext());
        String userJson = sessionManager.getString("anotherUser");
        System.out.println("userJson - "+userJson);

         gson = Utility.getGson();
        loggedInUser = gson.fromJson(userJson,User.class);
        System.out.println("loggedInUser - "+loggedInUser);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_other_account_call_log, container, false);
        //((ActivityNew)getActivity()).getSupportActionBar().setTitle("CALL LOGS");
        lvCallLogs = view.findViewById(R.id.lvCallLogs);
        getAllCallLogs();
        return view;
    }

    private void getAllCallLogs(){

        new AsyncTask<Void,Void,String>(){

            SweetAlertDialog pDialog;
            @Override
            protected void onPreExecute() {
                pDialog =Utility.createSweetAlertDialog(getContext());
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return new HttpManager().getData(getString(R.string.baseUrl)+"CallLogsService/getCallLogsOfUser/"+loggedInUser.getMobileNumber());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.dismiss();
                System.out.println(s);
                if(s!=null) {
                    List<CallLog> callLogs = gson.fromJson(s, new TypeToken<List<CallLog>>() {
                    }.getType());
                    List<String> callLogList = new ArrayList<>();
                    for (int i = 0; i < callLogs.size(); i++) {
                        String log = callLogs.get(i).getCallerName() + " " + callLogs.get(i).getCallerNumber();
                        callLogList.add(log);
                    }

                    if (callLogList.size() > 0) {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.content_item_list, R.id.txtName, callLogList);
                        lvCallLogs.setAdapter(arrayAdapter);
                    }
                }
            }
        }.execute();

    }

}
