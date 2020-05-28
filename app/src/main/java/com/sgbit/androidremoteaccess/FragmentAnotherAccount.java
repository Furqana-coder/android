package com.sgbit.androidremoteaccess;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAnotherAccount extends Fragment {

    EditText etPassword, etMobileNumber;
    Button btnLogIn;
    TextView  txtForgotPassword;
    View view;
    SweetAlertDialog pDialog;
    public FragmentAnotherAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_another_account, container, false);
        ((ActivityHome)getActivity()).getSupportActionBar().setTitle("Another Account");
        etMobileNumber = view.findViewById(R.id.etMobileNumber);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogIn = view.findViewById(R.id.btnLogIn);

        txtForgotPassword = view.findViewById(R.id.txtForgotPassword);

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityForgotPassword.class);
               startActivity(intent);

            }


        });


        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mobileNo = etMobileNumber.getText().toString();
                final String password = etPassword.getText().toString();
                System.out.println("Password=" + password);
                System.out.println("EmailId=" + mobileNo);

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        pDialog = Utility.createSweetAlertDialog(getContext());
                        pDialog.show();
                    }


                    @Override
                    protected String doInBackground(Void... voids) {
                        return new HttpManager().getData(getString(R.string.baseUrl) + "UserService/validateUser/" + mobileNo + "/" + password);
                    }

                    @Override
                    protected void onPostExecute(String ss) {
                        System.out.println("Result =" + ss);

                        if (TextUtils.isEmpty(ss)) {
                            Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                        } else {
                            SessionManager sessionManager = new SessionManager(getContext());
                            sessionManager.putString("anotherUser", ss);

                            Intent intent = new Intent(getActivity(),ActivityNew.class);
                            startActivity(intent);
                           //finish();
                        }
                        pDialog.dismiss();
                    }
                }.execute();

            }

        });
        return view;

    }
}

