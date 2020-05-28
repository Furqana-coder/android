package com.sgbit.androidremoteaccess;

import android.content.Context;

import java.lang.*;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import java.*;
import java.util.regex.*;
import java.util.Scanner;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.*;
import com.sgbit.androidremoteaccess.util.SessionManager;

import java.util.Random;

public class ActivityForgotPassword extends AppCompatActivity {

    int mobnum;
    String content;
    String NewPassword;
    String ReEnterPassword;
    String generatedOTP;
    EditText etMobileNumber;
    Button btnSendOTP;
    LinearLayout llOTP,llMobileNumber,llResetLabel,llResetPassword,llForgotPassword;
    EditText etEnterOTP;
    Button btnVerify;
    EditText etNewPassword;
    EditText etReEnterPassword;
    Button btnSubmit;
    final Context context = this;

    User user;

    public static boolean isValid(String content) {

        Pattern p = Pattern.compile("[7-9][0-9]{9}");
        Matcher m = p.matcher(content);
        return m.matches();
        //return (m.find() && m.group().equals(content));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        llOTP = (LinearLayout) findViewById(R.id.llOTP);
        llMobileNumber = (LinearLayout) findViewById(R.id.llMobileNumber);
        //llOTP.setVisibility(View.VISIBLE);
        llResetLabel = (LinearLayout) findViewById(R.id.llResetLabel);
        llResetPassword = (LinearLayout) findViewById(R.id.llResetPassword);
        etMobileNumber = (EditText) findViewById(R.id.etMobileNumber);
        btnSendOTP = (Button) findViewById(R.id.btnSendOTP);
        etEnterOTP = (EditText)findViewById(R.id.etEnterOTP);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        etNewPassword  = (EditText)findViewById(R.id.etNewPassword);
                etReEnterPassword = (EditText)findViewById(R.id.etReEnterPassword);
        btnSubmit =(Button) findViewById(R.id.btnSubmit);

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = etMobileNumber.getText().toString(); //gets you the contents of edit text(string)
                // mobnum = Integer.parseInt(content);//convert string to int


                if (isValid(content)) {
                    new AsyncTask<Void, Void, String>() {

                        SweetAlertDialog pDialog;

                        @Override
                        protected void onPreExecute() {
                            pDialog =Utility.createSweetAlertDialog(ActivityForgotPassword.this);
                            if(pDialog!=null) {
                                pDialog.show();
                            }
                        }
                        @Override
                        protected String doInBackground(Void... voids) {
                            return new HttpManager().getData(getString(R.string.baseUrl) + "UserService/getUserDetails/" + content);
                        }




                        @Override
                        protected void onPostExecute(String s) {

                            System.out.println("Result =" + s);

                            if (TextUtils.isEmpty(s)) {
                                Toast.makeText(getApplicationContext(), "Invalid number ", Toast.LENGTH_SHORT).show();
                            } else {

                                Gson gson = new Gson();
                                user = gson.fromJson(s,User.class);
                                emailOTP(s);
                                /*
                                generatedOTP = Utility.generateOTP();
                                System.out.println("The generatedOTP is :" + generatedOTP);
                                new SMS().sendSms("Your OTP to reset password is :"+generatedOTP,user.getMobileNumber());
                                llMobileNumber.setVisibility(View.GONE);
                                llOTP.setVisibility(View.VISIBLE);
                                 */
                            }
                            pDialog.dismiss();
                        }
                    }.execute();

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid mobile number format", Toast.LENGTH_SHORT).show();
                    System.out.println("Invalid Number");
                }
            }
        });


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                String content = etEnterOTP.getText().toString(); //gets you the contents of edit text(string)
                int val = Integer.parseInt(content);//converting string to int
                System.out.println(content);

                if (generatedOTP.equals(content)) {

                    System.out.println("verified");
                    llOTP.setVisibility(View.GONE);
                    //llForgotPassword.setVisibility(View.GONE);
                    llResetLabel.setVisibility(View.VISIBLE);
                    llResetPassword.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getApplicationContext(), "OTP not verified", Toast.LENGTH_SHORT).show();
                    System.out.println("OTP incorrect");
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                NewPassword = etNewPassword.getText().toString();
                ReEnterPassword= etReEnterPassword.getText().toString();
                if(NewPassword.equals(ReEnterPassword)){
                    user.setPassword(NewPassword);
                    new AsyncTask<Void, Void, String>() {

                        SweetAlertDialog pDialog;
                        @Override
                        protected String doInBackground(Void... voids) {
                            Gson gson = new Gson();
                            String userJson = gson.toJson(user);
                            return new HttpManager().postData(getString(R.string.baseUrl) + "UserService/updateUser",userJson);
                        }
                        @Override
                        protected void onPreExecute() {
                            pDialog =Utility.createSweetAlertDialog(ActivityForgotPassword.this);
                            pDialog.show();
                        }


                        @Override
                        protected void onPostExecute(String s) {
                            System.out.println("Result =" + s);

                            if (TextUtils.isEmpty(s)) {
                                Toast.makeText(getApplicationContext(), "did not update ", Toast.LENGTH_SHORT).show();
                            } else {
                                SessionManager sessionManager = new SessionManager(getApplicationContext());
                                sessionManager.putString("loggedInUser", s);

                                Intent intent = new Intent(context, ActivityLogin.class);
                                startActivity(intent);
                                finish();
                            }
                            pDialog.dismiss();
                        }
                    }.execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Passwords dont match! please try again!", Toast.LENGTH_SHORT).show();
                    System.out.println("Passwords dont match!");


                }
            }
        });
    }

    private void emailOTP(final String userJson){

        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... voids) {
                return new HttpManager().postData(getString(R.string.baseUrl)+"UserService/sendSMS",userJson);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(!TextUtils.isEmpty(s)){
                    generatedOTP = s;
                    System.out.println("The generatedOTP is :" + generatedOTP);
                    //new SMS().sendSms("Your OTP to reset password is :"+generatedOTP,user.getMobileNumber());
                    llMobileNumber.setVisibility(View.GONE);
                    llOTP.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

}


