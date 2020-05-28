package com.sgbit.androidremoteaccess;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.model.Document;
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
public class FragmentMyFiles extends Fragment {
    public FragmentMyFiles() {
        // Required empty public constructor
    }
    private GridView gvImages,gvAudios,gvVideos,gvDocs,gvPdfs;
    private View view;
    User loggedInUser;

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
        view = inflater.inflate(R.layout.fragment_my_files, container, false);
        ((ActivityHome)getActivity()).getSupportActionBar().setTitle("MyFiles");
        gvImages = view.findViewById(R.id.gvImages);
        gvAudios = view.findViewById(R.id.gvAudios);
        gvVideos = view.findViewById(R.id.gvVideos);
        gvPdfs= view.findViewById(R.id.gvPdfs);
        gvDocs = view.findViewById(R.id.gvDocs);
        getFilesOfUser();
        return view;
    }

    private void getFilesOfUser(){
        new AsyncTask<Void,Void,String>(){
            SweetAlertDialog pDialog;

            @Override
            protected void onPreExecute() {
                pDialog =Utility.createSweetAlertDialog(getContext());
                if(pDialog!=null) {
                    pDialog.show();
                }
            }
            @Override
            protected String doInBackground(Void... params) {
                return new HttpManager().getData(getString(R.string.baseUrl)+"DocumentService/getDocumentsOfUser/"+loggedInUser.getMobileNumber());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(pDialog!=null) {
                    pDialog.dismiss();
                }
                System.out.println(s);
                if(!TextUtils.isEmpty(s)){
                    Gson gson = Utility.getGson();
                    List<Document> documentList = gson.fromJson(s, new TypeToken<List<Document>>() {
                    }.getType());

                    List<Document> imgList = new ArrayList<Document>();
                    for (int i=0;i<documentList.size();i++){
                        if(documentList.get(i).getDocumentType().equals("image")){
                            imgList.add(documentList.get(i));
                        }
                    }

                    if(imgList.size()>0){
                        FileAdaptor fileAdaptor = new FileAdaptor(getContext(),imgList);
                        gvImages.setAdapter(fileAdaptor);
                    }

                    List<Document> videoList = new ArrayList<Document>();
                    for (int i=0;i<documentList.size();i++){
                        if(documentList.get(i).getDocumentType().equals("video")){
                            videoList.add(documentList.get(i));
                        }
                    }
                    if(videoList.size()>0){
                        FileAdaptor fileAdaptor = new FileAdaptor(getContext(),videoList);
                        gvVideos.setAdapter(fileAdaptor);
                    }

                    List<Document> audioList = new ArrayList<Document>();
                    for (int i=0;i<documentList.size();i++){
                        if(documentList.get(i).getDocumentType().equals("audio")){
                            audioList.add(documentList.get(i));
                        }
                    }
                    if(audioList.size()>0){
                        FileAdaptor fileAdaptor = new FileAdaptor(getContext(),audioList);
                        gvAudios.setAdapter(fileAdaptor);
                    }
                    List<Document> docList = new ArrayList<Document>();
                    for (int i=0;i<documentList.size();i++){
                        if(documentList.get(i).getDocumentType().equals("text")){
                            docList.add(documentList.get(i));
                        }
                    }
                    if(docList.size()>0){
                        FileAdaptor fileAdaptor = new FileAdaptor(getContext(),docList);
                        gvDocs.setAdapter(fileAdaptor);
                    }
                    List<Document> pdfList = new ArrayList<Document>();
                    for (int i=0;i<documentList.size();i++){
                        if(documentList.get(i).getDocumentType().equals("pdf")){
                            pdfList.add(documentList.get(i));
                        }
                    }
                    if(pdfList.size()>0){
                        FileAdaptor fileAdaptor = new FileAdaptor(getContext(),pdfList);
                        gvPdfs.setAdapter(fileAdaptor);
                    }
                }
            }
        }.execute();
    }

    class FileAdaptor extends ArrayAdapter<Document>{

        Context context;
        List<Document> docList;

        class ViewHolder{
            ImageView  imgUploadedImage;
            TextView tvImageName;
        }
        public FileAdaptor(@NonNull Context context, List<Document> docList) {
            super(context, R.layout.row_image, docList);
            this.context=context;
            this.docList = docList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = new ViewHolder();
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.row_image,parent,false);
                holder.imgUploadedImage = row.findViewById(R.id.imgUploadedImage);
                holder.tvImageName = row.findViewById(R.id.tvImageName);
                row.setTag(holder);
            }
            else{
                holder = (ViewHolder) row.getTag();
            }
            Document selectedDoc = docList.get(position);
            if(selectedDoc.getDocumentType().equals("text")){
                holder.imgUploadedImage.setImageResource(R.drawable.ic_doc);
            }
            else if(selectedDoc.getDocumentType().equals("pdf")){
                holder.imgUploadedImage.setImageResource(R.drawable.ic_pdf);
            }
            else if(selectedDoc.getDocumentType().equals("audio")){
                holder.imgUploadedImage.setImageResource(R.drawable.ic_audio);
            }
            else {
                Glide.with(getContext()).load(getString(R.string.imageUrl) + selectedDoc.getFileName()).into(holder.imgUploadedImage);
            }
            holder.tvImageName.setText("" + selectedDoc.getFileName());

            return row;
        }
    }
}
