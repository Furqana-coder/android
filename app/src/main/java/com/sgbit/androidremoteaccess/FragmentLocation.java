package com.sgbit.androidremoteaccess;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.Location;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLocation extends Fragment {
    public FragmentLocation() {
        // Required empty public constructor
    }
    private View view;
    User loggedInUser;
    TextView tvLatitude,tvLongitude;
    Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getContext());
        String userJson = sessionManager.getString("loggedInUser");
        System.out.println("userJson - "+userJson);

        gson = Utility.getGson();
        loggedInUser = gson.fromJson(userJson,User.class);
        System.out.println("loggedInUser - "+loggedInUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_location, container, false);
        ((ActivityHome)getActivity()).getSupportActionBar().setTitle("Location");
        tvLatitude = view.findViewById(R.id.tvLatitude);
        tvLongitude = view.findViewById(R.id.tvLongitude);
        getOtherAccountLocation();
        return view;
    }

    private void getOtherAccountLocation(){

        new AsyncTask<Void,Void,String>(){

            SweetAlertDialog pDialog;
            @Override
            protected void onPreExecute() {
                pDialog =Utility.createSweetAlertDialog(getContext());
                pDialog.show();}

            @Override
            protected String doInBackground(Void... voids) {
                return new HttpManager().getData(getString(R.string.baseUrl)+"LocationServices/getlocationByMobileNo/"+loggedInUser.getMobileNumber());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.dismiss();
                System.out.println(s);
                if(s!=null){
                    Location location = gson.fromJson(s,Location.class);
                    System.out.println(location.getLatitude()+" "+location.getLongitude());
                    tvLatitude.setText(""+location.getLatitude());
                    tvLongitude.setText(""+location.getLongitude());
                }

            }
        }.execute();

    }

}
