package com.sgbit.androidremoteaccess;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.model.*;
import com.sgbit.androidremoteaccess.util.*;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import com.sgbit.androidremoteaccess.model.User;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentContact extends Fragment {
    View view;
    User loggedInUser;
    Button button;
    ListView listView;
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name, phonenumber;
    public static final int RequestPermissionCode = 1;
    SweetAlertDialog pDialog;
    Gson gson;
    ArrayList<Contact> contactsList = new ArrayList<>();

    public FragmentContact() {
        // Required empty public constructor
    }
    String contactId ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(getContext());
        String userJson = sessionManager.getString("loggedInUser");
        gson =Utility.getGson();
        loggedInUser = gson.fromJson(userJson,User.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_contact, container, false);
        ((ActivityHome)getActivity()).getSupportActionBar().setTitle("Contact");

        listView = view.findViewById(R.id.listview1);

        //button = view.findViewById(R.id.button1);

        StoreContacts = new ArrayList<String>();

        EnableRuntimePermission();


/*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String contactsJson = gson.toJson(contactsList,new TypeToken<List<Document>>() {
            }.getType());
                System.out.println("contactsJson=" + contactsJson);
            Task task = new Task();
            task.execute(contactsJson);
            }
        });
        */
        return view;

    }

    public void GetContactsIntoArrayList() {
        Contact contact = new Contact();

        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            StoreContacts.add(name + " " + ":" + " " + phonenumber);
            contact.setContactName(name);
            contact.setMobileNo(phonenumber);

            contact.setContactId(loggedInUser.getMobileNumber());
            //Gson gson = new Gson();

            //String userJson = gson.toJson(contact);
            //System.out.println("userJson=" + userJson);
            //Task task = new Task();

            //task.execute(userJson);
            contactsList.add(contact);
        }

        arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.content_item_list, R.id.txtName, StoreContacts);
        listView.setAdapter(arrayAdapter);
        cursor.close();

    }

    class Task extends AsyncTask<String, Void, String> {

        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog =Utility.createSweetAlertDialog(getContext());
            if(pDialog!=null) {
                pDialog.show();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            String contactsJson = strings[0];


            return new HttpManager().postData(getString(R.string.baseUrl)+"ContactService/addContactList",contactsJson);

        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("Result - " + result);
            pDialog.dismiss();

        }
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED) {
            GetContactsIntoArrayList();
            Toast.makeText(getContext(), "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getContext(), "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
                    GetContactsIntoArrayList();

                } else {

                    Toast.makeText(getContext(), "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


}


