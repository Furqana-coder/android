package com.sgbit.androidremoteaccess;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sgbit.androidremoteaccess.model.Document;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOtherAccountFiles extends Fragment {


    public FragmentOtherAccountFiles() {
        // Required empty public constructor
    }

    private View view;
    private GridView gvImages,gvAudios,gvVideos,gvDocs,gvPdfs;

    User loggedInUser;
    Gson gson;
    Fragment currentFragment;

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
        view = inflater.inflate(R.layout.fragment_other_account_files, container, false);
       // ((ActivityNew)getActivity()).getSupportActionBar().setTitle("FILES");
        gvImages = view.findViewById(R.id.gvImages);
        gvAudios = view.findViewById(R.id.gvAudios);
        gvVideos = view.findViewById(R.id.gvVideos);
        gvDocs = view.findViewById(R.id.gvDocs);
        gvPdfs = view.findViewById(R.id.gvPdfs);
        currentFragment = this;

        getFilesOfUser();
        return view;
    }

    private void getFilesOfUser(){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                return new HttpManager().getData(getString(R.string.baseUrl)+"DocumentService/getDocumentsOfUser/"+loggedInUser.getMobileNumber());
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
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

    class FileAdaptor extends ArrayAdapter<Document> {

        Context context;
        List<Document> docList;

        class ViewHolder{
            ImageView imgUploadedImage;
            TextView tvImageName;
            LinearLayout llBtns;
            Button btnDelete,btnDownload;
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
                holder.llBtns = row.findViewById(R.id.llBtns);
                holder.llBtns.setVisibility(View.VISIBLE);
                holder.btnDelete = row.findViewById(R.id.btnDelete);
                holder.btnDownload= row.findViewById(R.id.btnDownload);
                row.setTag(holder);
            }
            else{
                holder = (ViewHolder) row.getTag();
            }
            final Document selectedDoc = docList.get(position);
            if(selectedDoc.getDocumentType().equals("text")){
                holder.imgUploadedImage.setImageResource(R.drawable.ic_doc);
            }
            else if(selectedDoc.getDocumentType().equals("pdf")){
                holder.imgUploadedImage.setImageResource(R.drawable.ic_pdf);
            }
            else {
                Glide.with(getContext()).load(getString(R.string.imageUrl) + selectedDoc.getFileName()).into(holder.imgUploadedImage);
            }
            holder.tvImageName.setText("" + selectedDoc.getFileName());


            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedDoc.setStatus("D");
                    new AsyncTask<Void,Void,String>(){
                        @Override
                        protected String doInBackground(Void... params) {

                            String docJson = gson.toJson(selectedDoc);
                            return new HttpManager().postData(getString(R.string.baseUrl)+"DocumentService/updateDocument",docJson);
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            System.out.println("deleted status - "+s);
                            if (s!=null && s.equals("1")){
                                getFragmentManager().beginTransaction().detach(currentFragment).attach(currentFragment).commit();
                            }
                            else{
                                Toast.makeText(getContext(),"File deletion failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                }
            });

            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageUrl = getString(R.string.imageUrl) + selectedDoc.getFileName();
                    new DownloadFile().execute(imageUrl);
                }
            });
            return row;
        }
    }

    private boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(getContext());
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file

                folder = Environment.getExternalStorageDirectory() + File.separator + "AndroidRemoteAccess/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    //Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(getContext(),
                    message, Toast.LENGTH_LONG).show();
        }
    }

}


