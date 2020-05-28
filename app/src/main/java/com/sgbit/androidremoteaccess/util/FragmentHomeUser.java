                             package com.sgbit.androidremoteaccess.util;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.R;
import com.sgbit.androidremoteaccess.model.User;

                             /**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHomeUser extends Fragment {


                                 public FragmentHomeUser() {
                                     // Required empty public constructor
                                 }

                                 User loggedInUser;
                                 Gson gson;
                                 View view;

                                 @Override
                                 public void onCreate(@Nullable Bundle savedInstanceState) {
                                     super.onCreate(savedInstanceState);
                                     SessionManager sessionManager = new SessionManager(getContext());
                                     String userJson = sessionManager.getString("s");
                                     System.out.println("userJson - " + userJson);
                                     gson = Utility.getGson();
                                     loggedInUser = gson.fromJson(userJson, User.class);
                                     System.out.println("loggedInUser - " + loggedInUser);
                                 }


                                 @Override
                                 public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                          Bundle savedInstanceState) {
                                     // Inflate the layout for this fragment
                                     view = inflater.inflate(R.layout.fragment_home_user, container, false);
                                     // (((ActivityNew)getActivity()).getSupportActionBar().setTitle("HOME");
                                     TextView tvName = view.findViewById(R.id.tvName);


//                                     tvName.setText("Hi " + loggedInUser.getName() + ", Welcome to your account.");
                                     return view;


                                 }
                             }
