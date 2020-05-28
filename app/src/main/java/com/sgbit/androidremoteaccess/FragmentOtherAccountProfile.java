package com.sgbit.androidremoteaccess;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.SessionManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOtherAccountProfile extends Fragment {
    User loggedInUser;
    TextView tvMobileNumber;
    TextView tvpassword;
    TextView tvemail;
    TextView tvname;
    Button btnUpdate;
    View view;


    public FragmentOtherAccountProfile() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getContext());
        String userJson = sessionManager.getString("anotherUser");
        System.out.println("userJson - "+userJson);

        Gson gson = new Gson();
        loggedInUser = gson.fromJson(userJson,User.class);
        System.out.println("loggedInUser - "+loggedInUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.ragment_other_account_profile, container, false);
        //((ActivityNew)getActivity()).getSupportActionBar().setTitle("PROFILE");
        tvMobileNumber = view.findViewById(R.id.tvMobileNumber);
        tvMobileNumber.setText(loggedInUser.getMobileNumber());
        tvpassword = view.findViewById(R.id.tvpassword);
        tvpassword.setText(loggedInUser.getPassword());
        tvemail = view.findViewById(R.id.tvemail);
        tvemail.setText(loggedInUser.getEmailId());
        tvname = view.findViewById(R.id.tvname);
        tvname.setText(loggedInUser.getName());

        return view;
    }

}
