package com.sgbit.androidremoteaccess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityLogin extends AppCompatActivity {

    EditText etPassword,etMobileNumber;
    TextView txtRegister;
    TextView txtForget;
    Button btnLogIn, btnRegister;
    final Context context = this;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etMobileNumber =(EditText) findViewById(R.id.etMobileNumber);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogIn = (Button) findViewById(R.id.btnLogIn);


        /*
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityRegistration.class);
                startActivity(intent);
            }


        });*/
        txtForget = (TextView) findViewById(R.id.txtForget);
        txtForget.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityForgotPassword.class);
                startActivity(intent);
            }

        });
        txtRegister =(TextView) findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityRegistration.class);
                startActivity(intent);
            }

        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                final String mobileNumber = etMobileNumber.getText().toString();
                final String password = etPassword.getText().toString();
                System.out.println("Password=" + password);
                System.out.println("Mobile number=" +mobileNumber);


                new AsyncTask<Void,Void,String>(){
                    @Override
                    protected void onPreExecute() {
                        pDialog = Utility.createSweetAlertDialog(ActivityLogin.this);
                        pDialog.show();
                    }

                    @Override
                    protected String doInBackground(Void... voids) {
                        String url = getString(R.string.baseUrl)+"UserService/validateUser/"+mobileNumber+"/"+password;
                        return new HttpManager().getData(url);
                        //String url = getString(R.string.baseUrl)+"UserService/validateUser/"+mobileNumber+"/"+password;

                    }

                    @Override
                    protected void onPostExecute(String s) {
                        System.out.println("Result =" + s);

                        if(TextUtils.isEmpty(s)){
                            Toast.makeText(getApplicationContext(),"Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.putString("loggedInUser", s);

                           Intent intent = new Intent(context, ActivityHome.class);
                            startActivity(intent);
                            finish();
                        }
                        pDialog.dismiss();
                    }
                }.execute();




            }


        });

       /* */
    }
}
