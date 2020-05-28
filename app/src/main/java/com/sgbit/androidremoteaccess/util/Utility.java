package com.sgbit.androidremoteaccess.util;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sgbit.androidremoteaccess.R;
import com.sgbit.androidremoteaccess.ActivityForgotPassword;
import com.sgbit.androidremoteaccess.model.User;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

//import cn.pedant.SweetAlert.SweetAlertDialog;
public class Utility {

    //Notification Types
    public static final int IDEA_RATED = 1;
    public static final int SUGGESTION_RATED = 2;
    public static final int SUGGESTION_MADE = 3;
    public static final int COMMENTED = 4;
    public static final int PROMOTED = 5;
    public static final int SUGGESTION_SOUGHT = 6;
    public static final int PAID = 7;

    //
    public static final int TOTAL_SUGGESTIONS_RATING_THRESHOLD = 50;
    public static final String EXPERT_USER = "Expert";
    public static final String PUBLIC_USER = "Public";
    public static String formatDateToString(long datetime){
        String dateString = null;
        Date date=new Date(datetime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        dateString = dateFormat.format(date);
        return dateString;
    }

    public static boolean isEmailValid(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidAadharNumber(String aadharNumber){
        if(aadharNumber.length()!=12){
            return false;
        }
        else{
            try{
                Long.parseLong(aadharNumber);
            }
            catch (NumberFormatException e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        Log.d("password - ",password);
        if(password.length() < 3){
            return false;
        }
        return true;
    }

    public static boolean isValidPhone(String phone) {
        String PHONE_PATTERN = "[0-9]{10}";
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean isJsonStringEmpty(String jsonString){
        if(jsonString==null||jsonString.equals("")||jsonString.equals("[]")){
            return true;
        }
        return false;
    }

    public static String getAddressFromLatLng(double latitude, double longitude, Context context){
        String address = null;
        System.out.println("Lat "+latitude+" Lng "+longitude);
        try {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            if(geocoder.isPresent()) {
                System.out.println("Geocoder is present");
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() != 0) {
                    Address returnedAddress = addresses.get(0);
                    StringBuffer str = new StringBuffer();
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        str.append(returnedAddress.getAddressLine(i)).append(",");
                    }
                    str.append(returnedAddress.getLocality() + ",");
                    str.append(returnedAddress.getSubAdminArea() + ",");
                    str.append(returnedAddress.getAdminArea());
                    // str.append(returnedAddress.getCountryName() + ",");
                    System.out.println("addresses  present");
                    address = str.toString();
                }
                else{
                    System.out.println("addresses NOT present");
                }
            }
            else{
                address = "Not Available";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public double[] getLatLngFromAddress(String Address, Context context){
        double[] latLng = new double[2];

        Geocoder gc = new Geocoder(context, Locale.ENGLISH);
        if(gc.isPresent()){
            List<android.location.Address> list = null;
            try {
                list = gc.getFromLocationName(Address, 1);
                android.location.Address address = list.get(0);
                latLng[0] = address.getLatitude();
                latLng[1] = address.getLongitude();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return latLng;
    }





    public static String generateOTP(){
        String otp = ""+((int)(Math.random()*9000)+1000);
        return otp;
    }

    public static void hideKeyboard(Context context, boolean hide) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if(hide){
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
        else{
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }
    /*
        public static void isActiveNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null && networkInfo.isAvailable()){

            }
            else {
                Toast.makeText(context, "You are NOT connected to internet. Please connect to internet to work with Logistica App", Toast.LENGTH_LONG).show();
            }
        }
    */
    public static String generateTransactionId(String mobileNumber){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddhhmm");
        String dateStr = dateFormat.format(date);
        String transactionIdString = "L"+mobileNumber+dateStr+generateOTP();
        return transactionIdString;
    }

    public static String generateReferalCode(String mobileNumber){
        String transactionIdString = "L"+mobileNumber+generateOTP();
        return transactionIdString;
    }

    public static ProgressDialog createProgressDialog(Context context){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        return dialog;
    }

    public static Gson getGson(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
        return gson;
    }

    public static User getLoggedInUser(Context context){
        SessionManager sessionManager = new SessionManager(context);
        String userJson = sessionManager.getString("loggedInUser");
        Gson gson = getGson();
        User loggedInUser = gson.fromJson(userJson,User.class);
        return loggedInUser;
    }

    public static boolean isValidLicenceNumber(String dlNumber){
        if(TextUtils.isEmpty(dlNumber)){
            return false;
        }
        return true;
    }

    public static boolean isValidVehicleNumber(String vehicleNumber){
        if(TextUtils.isEmpty(vehicleNumber)){
            return false;
        }
        return true;
    }



    public static SweetAlertDialog createSweetAlertDialog(Context context){
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        return pDialog;
    }


}
