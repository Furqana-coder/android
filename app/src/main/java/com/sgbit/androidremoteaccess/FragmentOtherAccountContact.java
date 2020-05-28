package com.sgbit.androidremoteaccess;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.model.CallLog;
import com.sgbit.androidremoteaccess.model.Contact;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOtherAccountContact extends Fragment {


    public FragmentOtherAccountContact() {
        // Required empty public constructor
    }

    private View view;
    User loggedInUser;
    Gson gson;
    ListView lvContact;
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
        view = inflater.inflate(R.layout.fragment_other_account_contact, container, false);
        //f((ActivityNew)getActivity()).getSupportActionBar().setTitle("CONTACTS");
        lvContact = view.findViewById(R.id.listview1);
        getOtherAccountContact();
        return view;
    }

    private void getOtherAccountContact(){

        new AsyncTask<Void,Void,String>(){
            SweetAlertDialog pDialog;
            @Override
            protected void onPreExecute() {
                pDialog =Utility.createSweetAlertDialog(getContext());
                pDialog.show();
                super.onPreExecute();
            }



            @Override
            protected String doInBackground(Void... voids) {
                return new HttpManager().getData(getString(R.string.baseUrl)+"ContactService/getContactsOfUser/"+loggedInUser.getMobileNumber());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.dismiss();
                System.out.println(s);
                if(s!=null) {
                    List<Contact> contacts = gson.fromJson(s, new TypeToken<List<Contact>>() {
                    }.getType());
                    List<String> contactList = new ArrayList<>();
                    for (int i = 0; i < contacts.size(); i++) {
                        String contact = contacts.get(i).getContactName() + " " + contacts.get(i).getMobileNo();
                        contactList.add(contact);
                    }

                    if (contactList.size() > 0) {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.content_item_list, R.id.txtName, contactList);
                        lvContact.setAdapter(arrayAdapter);
                    }
                }
            }
        }.execute();

    }

}
