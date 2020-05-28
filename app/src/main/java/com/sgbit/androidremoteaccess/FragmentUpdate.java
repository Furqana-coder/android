package com.sgbit.androidremoteaccess;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import org.apache.http.conn.ConnectTimeoutException;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentUpdate.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentUpdate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUpdate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String password;
    private String ReEnterPassword;
    Button btnUpdate;
    EditText etPassword, etReEnterPassword;
    Button btnSubmit;
    User loggedInUser;
    SweetAlertDialog pDialog;
    private OnFragmentInteractionListener mListener;

    public FragmentUpdate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentUpdate.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUpdate newInstance(String param1, String param2) {
        FragmentUpdate fragment = new FragmentUpdate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getContext());
        String userJson = sessionManager.getString("loggedInUser");
        System.out.println("userJson - "+userJson);

        Gson gson = new Gson();
        loggedInUser = gson.fromJson(userJson,User.class);
        System.out.println("loggedInUser - "+loggedInUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_update, container, false);
        //((ActivityNew)getActivity()).getSupportActionBar().setTitle("UPDATE");
        btnSubmit = view.findViewById(R.id.btnSubmit);
        etPassword = view.findViewById(R.id.etPassword);
        etReEnterPassword = view.findViewById(R.id.etReEnterPassword);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                password = etPassword.getText().toString();
                ReEnterPassword = etReEnterPassword.getText().toString();
                if (password.equals(ReEnterPassword)) {
                    loggedInUser.setPassword(password);
                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected void onPreExecute() {
                            pDialog = Utility.createSweetAlertDialog(getContext());
                            pDialog.show();
                        }

                        @Override
                        protected String doInBackground(Void... voids) {


                            Gson gson = new Gson();
                            String userJson = gson.toJson(loggedInUser);
                            return new HttpManager().postData(getString(R.string.baseUrl) + "UserService/updateUser", userJson);
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            System.out.println("Result =" + s);

                            if (TextUtils.isEmpty(s)) {
                                Toast.makeText(getContext(), "did not update ", Toast.LENGTH_SHORT).show();
                            } else {
                                SessionManager sessionManager = new SessionManager(getContext());
                                sessionManager.putString("loggedInUser", s);

                                Intent intent = new Intent(getContext(), ActivityLogin.class);
                                startActivity(intent);
                                //finish();
                            }
                            pDialog.dismiss();

                        }
                    }.execute();
                } else {
                    Toast.makeText(getContext(), "Passwords dont match! please try again!", Toast.LENGTH_SHORT).show();
                    System.out.println("Passwords dont match!");


                }
            }

        });
        return view;

    }



    // TODO: Rename method, update argument and hook method into UI event

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

}

