package com.sgbit.androidremoteaccess;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityRegistration extends AppCompatActivity {

    EditText etName;
    EditText etPassword, etMobileNumber, etEmailId;
    Button btnSubmit;
    TextView textView2;
    final Context context = this;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etName = (EditText) findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPassword);

        etMobileNumber = (EditText) findViewById(R.id.etMobileNumber);
        etEmailId = (EditText) findViewById(R.id.etEmailId);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        textView2 = (TextView) findViewById(R.id.textView2);
        Test1 test1=new Test1();



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            Test1 test1=new Test1();
            @Override
            public void onClick(View view) {
                Boolean flag=true;
                String name = etName.getText().toString().trim();
                String phone = etMobileNumber.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String emailId = etEmailId.getText().toString().trim();


                System.out.println("Name=" + name);
                System.out.println("Phone=" + phone);
                System.out.println("Password=" + password);
                System.out.println("EmailId=" + emailId);
                if (test1.isValid(emailId)) {
                    System.out.print("Yes");
                }
                else {
                    etEmailId.setError("Invalid Email ID");
                    flag = false;
                }
                if(test1.isValidPhone(phone)){
                    System.out.print("Yes");
                }
                else {
                    etMobileNumber.setError("Invalid Mobile Number");
                    flag = false;
                }

                //Toast.makeText(getApplicationContext(), "Clicked - " + name, Toast.LENGTH_SHORT).show();
                if(flag == true) {
                    User user = new User();
                    user.setName(name);
                    user.setMobileNumber(phone);
                    user.setEmailId(emailId);
                    user.setPassword(password);



                    Gson gson = new Gson();

                    String userJson = gson.toJson(user);

                    System.out.println("userJson=" + userJson);

                    if(flag) {
                        RegistrationTask registrationTask = new RegistrationTask();

                        registrationTask.execute(userJson);
                    }
                }

            }


        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });


    }


    class RegistrationTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            pDialog = Utility.createSweetAlertDialog(ActivityRegistration.this);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String userJson = strings[0];
            HttpManager httpManager = new HttpManager();
            // String result = httpManager.postData("http://192.168.43.210:8080/AndroidRemoteAccess/webapi/UserService/registerUser", userJson);
            return new HttpManager().postData(getString(R.string.baseUrl) + "UserService/registerUser", userJson);

            // return result;
            //return userJson;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            System.out.println("Result - " + result);
            if (TextUtils.isEmpty(result)) {
                Toast.makeText(getApplicationContext(), "Registration is unsuccessful!", Toast.LENGTH_SHORT).show();
            } else if (result.equals("1")) {
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.putString("loggedInUser", result);
                Toast.makeText(getBaseContext(), "Registration is successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ActivityLogin.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "Registration is unsuccessful!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
class Test1{
    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email== null)
            return false;
        return pat.matcher(email).matches();
    }
    public static boolean isValidPhone(String Mobile) {
        String PHONE_PATTERN = "[0-9]{10}";
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher  matcher = pattern.matcher(Mobile);
        return matcher.matches();
    }

    public static boolean isJsonStringEmpty(String jsonString){
        if(jsonString==null||jsonString.equals("")||jsonString.equals("[]")){
            return true;
        }
        return false;
    }
}
