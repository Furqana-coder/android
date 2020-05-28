package com.sgbit.androidremoteaccess;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOtherAccountHome extends Fragment {


    User loggedInUser;
    Gson gson;
    public FragmentOtherAccountHome() {
        // Required empty public constructor
    }
    View view;
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
        view = inflater.inflate(R.layout.fragment_other_account_home, container, false);
       // (((ActivityNew)getActivity()).getSupportActionBar().setTitle("HOME");
        TextView tvName = view.findViewById(R.id.tvName);


        tvName.setText("Hi "+loggedInUser.getName()+", Welcome to your account.");


        Button btnMode = view.findViewById(R.id.btnMode);
        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loggedInUser.getMode().equals("G")){
                    Toast.makeText(getContext(),"Already phone is in non - silent mode",Toast.LENGTH_SHORT).show();
                }
                else{
                    loggedInUser.setMode("G");
                    final String userJson = gson.toJson(loggedInUser);
                    new AsyncTask<Void,Void,String>(){
                        @Override
                        protected String doInBackground(Void... params) {
                            return new HttpManager().postData(getString(R.string.baseUrl)+"UserService/updateUser",userJson);
                        }

                        @Override
                        protected void onPostExecute(String res) {
                            super.onPostExecute(res);
                            System.out.println(res);
                            if (res!=null&& res.equals("1")){
                                Toast.makeText(getContext(),"General mode activated ",Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute();
                }
            }
        });
        return view;
    }

}
