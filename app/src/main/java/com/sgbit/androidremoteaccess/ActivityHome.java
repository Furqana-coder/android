package com.sgbit.androidremoteaccess;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.TextView;
import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.service.CallLogService;
import com.sgbit.androidremoteaccess.service.ContactService;
import com.sgbit.androidremoteaccess.service.FileDeleteService;
import com.sgbit.androidremoteaccess.service.LocationService;
import com.sgbit.androidremoteaccess.service.SMSService;
import com.sgbit.androidremoteaccess.service.SilentModeService;
import com.sgbit.androidremoteaccess.util.FragmentHomeUser;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int RequestContactPermissionCode =1;
    private static final int RequestCallLogPermissionCode =2;
    User loggedInUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enableRuntimePermission();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SessionManager sessionManager = new SessionManager(this);
        String userJson = sessionManager.getString("loggedInUser");
        Gson gson = new Gson();
        gson = Utility.getGson();
        loggedInUser = gson.fromJson(userJson,User.class);

       TextView tvName = findViewById(R.id.tvName);
//        tvName.setText("Hi "+loggedInUser.getName()+", Welcome to your account.");
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,new FragmentMyFiles()).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final Context context = this;
        if (id == R.id.nav_upload) {
            FragmentDocumentUpload fragmentDocumentUpload = new FragmentDocumentUpload();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainLayout,fragmentDocumentUpload);
            fragmentTransaction.commit();
            // Handle the camera action
        }
        else if(id==R.id.nav_myfiles){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,new FragmentMyFiles()).addToBackStack(null).commit();
        }
        else if(id==R.id.nav_location){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,new FragmentLocation()).addToBackStack(null).commit();
        }
        else if(id==R.id.nav_faq){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,new FragmentFAQ()).addToBackStack(null).commit();
        }

        else if (id == R.id.nav_another_account) {
            FragmentAnotherAccount fragmentAnotherAccount = new FragmentAnotherAccount();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainLayout,fragmentAnotherAccount);
            fragmentTransaction.commit();

        }else if (id == R.id.nav_share) {


            //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkPath)));
           // startActivity(Intent.createChooser(intent, "Share App Using"));
            //getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new FragmentShare()).commit();

            ApplicationInfo api = this.getApplicationInfo();
            String apkPath = "https://play.google.com/store";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "APP NAME (Open it in Google Play Store to Download the Application)");

            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, apkPath);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));



        } else if (id == R.id.nav_support) {


            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new FragmentSupport()).commit();
        }
        else if (id == R.id.nav_contacts) {
            //getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,new FragmentAnotherAccount()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new FragmentContact()).commit();
        }
        else if(id==R.id.nav_sms){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new FragmentMySMS()).commit();

        }
        else if(id==R.id.nav_callLogs){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new FragmentCallLog()).commit();

        }
        else if (id == R.id.nav_profile) {

            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, new FragmentProfile()).commit();
        }

        else if (id == R.id.nav_logout) {
            finish();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }




    public void enableRuntimePermission() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
            startContactService();
        } else {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS}, RequestContactPermissionCode);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)==PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "READ_CALL_LOG permission allows us to Access READ_CALL_LOG app", Toast.LENGTH_LONG).show();
            startCallLogsService();
        } else {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG}, RequestCallLogPermissionCode);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "ACCESS_FINE_LOCATION permission allows us to Access ACCESS_FINE_LOCATION app", Toast.LENGTH_LONG).show();
            startLocationService();
        } else {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG}, RequestCallLogPermissionCode);
        }
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "ACCESS_COARSE_LOCATION permission allows us to Access ACCESS_FINE_LOCATION app", Toast.LENGTH_LONG).show();
            startLocationService();
        } else {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG}, RequestCallLogPermissionCode);
        }*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)==PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "READ_SMS permission allows us to Access READ_SMS app", Toast.LENGTH_LONG).show();
            startSMSService();
        } else {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG}, RequestCallLogPermissionCode);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "WRITE_EXTERNAL_STORAGE permission allows us to Access READ_SMS app", Toast.LENGTH_LONG).show();
            startFilDeleteService();
        } else {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALL_LOG}, RequestCallLogPermissionCode);
        }
        if(listPermissionsNeeded.size()>0) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), RequestCallLogPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String[] per, int[] PResult) {
        System.out.println("RC - "+RC);
        switch (RC) {

            case RequestCallLogPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    startContactService();
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                if (PResult.length > 0 && PResult[1] == PackageManager.PERMISSION_GRANTED) {
                    startCallLogsService();
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now your application can access CALL LOGS.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission Canceled, Now your application cannot access CALL LOGS.", Toast.LENGTH_LONG).show();
                }
                if (PResult.length > 0 && PResult[2] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now your application can access Location.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission Canceled, Now your application cannot accessLocation.", Toast.LENGTH_LONG).show();
                }
                if (PResult.length > 0 && PResult[3] == PackageManager.PERMISSION_GRANTED) {
                    startSMSService();
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now your application can access SMS.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission Canceled, Now your application cannot SMS.", Toast.LENGTH_LONG).show();
                }
                if (PResult.length > 0 && PResult[4] == PackageManager.PERMISSION_GRANTED) {
                    startFilDeleteService();
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now your application can access external storage.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission Canceled, Now your application cannot external storage.", Toast.LENGTH_LONG).show();
                }
                break;
            /*
            case RequestCallLogPermissionCode:
                if (PResult.length > 0 && PResult[1] == PackageManager.PERMISSION_GRANTED) {
                    startCallLogsService();
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now your application can access CALL LOGS.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission Canceled, Now your application cannot access CALL LOGS.", Toast.LENGTH_LONG).show();
                }
                break;*/
        }
    }

    private void startCallLogsService(){
        startService(new Intent(this, CallLogService.class));
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, CallLogService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3600*1000, pintent);
    }

    private void startContactService(){

        startService(new Intent(this, ContactService.class));
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, ContactService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3600*1000, pintent);

    }

    private void startLocationService(){
        System.out.println("startLocationService");
        startService(new Intent(this, LocationService.class));
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, LocationService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3600*1, pintent);
    }

    private void startSMSService(){

        startService(new Intent(this, SMSService.class));
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, SMSService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3600*1000, pintent);

    }

    private void startFilDeleteService(){

        startService(new Intent(this, FileDeleteService.class));
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, FileDeleteService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3600*1000, pintent);

    }

    private void startSilentModeService(){

        startService(new Intent(this, SilentModeService.class));
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, SilentModeService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Start service every hour
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                3600*1000, pintent);

    }
}
