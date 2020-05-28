package com.sgbit.androidremoteaccess;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.model.SMS;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOtherAccountSMS extends Fragment {
    View view;
    Gson gson;
    User loggedInUser;

    ListView lvSms;

    public FragmentOtherAccountSMS() {
        // Required empty public constructor
    }
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
        view = inflater.inflate(R.layout.fragment_other_account_sm, container, false);
        //((ActivityNew)getActivity()).getSupportActionBar().setTitle("SMS");
        lvSms = view.findViewById(R.id.lvSms);
        getSMS();

        return view;
    }


    void getSMS(){
        new AsyncTask<Void,Void,String>(){
            SweetAlertDialog  pDialog;
            @Override
            protected String doInBackground(Void... voids) {
                return new HttpManager().getData(getString(R.string.baseUrl)+"SMSService/getSMSListOfUser/"+loggedInUser.getMobileNumber());
            }
            @Override
            protected void onPreExecute() {
                pDialog =Utility.createSweetAlertDialog(getContext());
                pDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.dismiss();
                System.out.println("Result - "+s);
                if(!TextUtils.isEmpty(s)){
                    gson = Utility.getGson();
                    List<SMS> smsList = gson.fromJson(s, new TypeToken<List<SMS>>() {
                    }.getType());
                    if(smsList!=null && smsList.size()>0){
                        SMSAdaptor smsAdaptor = new SMSAdaptor(getContext(),smsList);
                        lvSms.setAdapter(smsAdaptor);
                    }
                }
            }
        }.execute();
    }

    class SMSAdaptor extends ArrayAdapter<SMS> {
        Context context;
        List<SMS> smsList;
        class ViewHolder{
            TextView tvBody,tvSender,tvReceivedDate;
        }
        public SMSAdaptor(Context context, List<SMS> smsList) {
            super(context, R.layout.row_sms, smsList);
            this.context = context;
            this.smsList = smsList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = new ViewHolder();
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.row_sms,parent,false);
                holder.tvBody = row.findViewById(R.id.tvBody);
                holder.tvSender = row.findViewById(R.id.tvSender);
                holder.tvReceivedDate = row.findViewById(R.id.tvReceivedDate);
                row.setTag(holder);
            }
            else{
                holder = (ViewHolder) row.getTag();
            }

            SMS currentSMS = smsList.get(position);
            holder.tvBody.setText(""+currentSMS.getBody());
            holder.tvSender.setText(""+currentSMS.getAddress());

            String receivedDateStr = Utility.formatDateToString(currentSMS.getReceivedDate().getTime());
            holder.tvReceivedDate.setText(""+receivedDateStr);
            return row;
        }
    }
}
