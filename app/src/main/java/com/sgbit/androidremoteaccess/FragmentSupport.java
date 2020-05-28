package com.sgbit.androidremoteaccess;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSupport extends Fragment {

    Button btnAdminNumber;
    Button btnAdminEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_support, container, false);
       // ((ActivityNew)getActivity()).getSupportActionBar().setTitle("SUPPORT");
        btnAdminNumber = view.findViewById(R.id.btnAdminNumber);
        btnAdminEmail = view.findViewById(R.id.btnAdminEmail);

       /* mEditInit = (etAdminNumber) view.findViewById(R.id.etAdminNumber);
        mEditInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
            }

        });*/
        btnAdminNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:7894561235"));
                startActivity(intent);
            }



        });

        btnAdminEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND,Uri.parse("mailto:" + "admin@gmail.com"));
                emailIntent.setType("text/plain");
               // emailIntent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "admin@gmail.com"));
              // emailIntent.setRecipient(emailIntent.RecipientType.TO, new InternetAddress("admin@gmail.com"));
                startActivity(emailIntent);
            }

            // return view;

        });
        return view;
    }
}

