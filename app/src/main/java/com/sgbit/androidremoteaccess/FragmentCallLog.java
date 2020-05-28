package com.sgbit.androidremoteaccess;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;


/**
 * A simple {@link Fragment} subclass.
 */


public class FragmentCallLog extends Fragment {

    ListView lvCallLogs;
    View view;
    public FragmentCallLog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_call_log, container, false);
        ((ActivityHome)getActivity()).getSupportActionBar().setTitle("Call logs");
        lvCallLogs = view.findViewById(R.id.lvCallLogs);
        getAllCallLogs();
        return view;
    }

    private void getAllCallLogs(){
        CallsProvider callsProvider = new CallsProvider(getContext());
        List<Call> callList = callsProvider.getCalls().getList();
        List<String> callLogList = new ArrayList<>();
        for (int i=0;i<callList.size();i++){
            System.out.println(callList.get(i).name +"  "+callList.get(i).number+" "+new Date(callList.get(i).callDate));
            callLogList.add(callList.get(i).name +" - "+callList.get(i).number+" - "+new Date(callList.get(i).callDate));
        }
        if(callLogList.size()>0){
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.content_item_list, R.id.txtName,callLogList);
            lvCallLogs.setAdapter(arrayAdapter);
        }
    }
}
