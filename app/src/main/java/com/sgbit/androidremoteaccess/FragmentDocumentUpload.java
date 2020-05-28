package com.sgbit.androidremoteaccess;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sgbit.androidremoteaccess.model.Document;
import com.sgbit.androidremoteaccess.model.User;
import com.sgbit.androidremoteaccess.util.HttpManager;
import com.sgbit.androidremoteaccess.util.ImageFilePath;
import com.sgbit.androidremoteaccess.util.SessionManager;
import com.sgbit.androidremoteaccess.util.Utility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDocumentUpload extends Fragment {
    Spinner spin;
    User loggedInUser;
    TextView txtfileType, txtChFile, txtUrl, etMobileNumber, tvBrowseError, tvUploadError;
    Button btnBrowse, btnUpload;
    String mobileNumber;
    String password;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    SweetAlertDialog pDialog;


    String file = "";
    Intent intent;
    private Gson gson;
    String FilePath;
    private static final int PICKFILE_RESULT_CODE = 1;

    public FragmentDocumentUpload() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getContext());
        String userJson = sessionManager.getString("loggedInUser");
        System.out.println("userJson - " + userJson);

        Gson gson = new Gson();
        loggedInUser = gson.fromJson(userJson, User.class);
        System.out.println("loggedInUser - " + loggedInUser);
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_document_upload, container, false);
        ((ActivityHome)getActivity()).getSupportActionBar().setTitle("Document upload");
        btnBrowse = view.findViewById(R.id.btnBrowse);
        tvBrowseError = view.findViewById(R.id.tvBrowseError);
        btnUpload = view.findViewById(R.id.btnUpload);
        txtUrl = view.findViewById(R.id.txtUrl);
        txtChFile = view.findViewById(R.id.txtChFile);
        txtfileType = view.findViewById(R.id.txtfileType);
        tvUploadError = view.findViewById(R.id.tvUploadError);
        etMobileNumber = view.findViewById(R.id.etMobileNumber);
        checkPermissionREAD_EXTERNAL_STORAGE(getContext());
        spin = view.findViewById(R.id.spin);
        final String[] fileType = {"text", "pdf", "image", "audio", "video"};
        ArrayAdapter<String> fileAdap = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, fileType);
        fileAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(fileAdap);
        SessionManager sessionManager = new SessionManager(getContext());
        String userJson = sessionManager.getString("loggedInUser");
        System.out.println("userJson - " + userJson);

        Gson gson = new Gson();
        loggedInUser = gson.fromJson(userJson, User.class);
        System.out.println("loggedInUser - " + loggedInUser);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext(), "Position - " + i, Toast.LENGTH_LONG).show();
                file = fileType[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                String fileType = file + "/*";

                if(file.equals("text")){
                 //   fileType="text/plain|application/msword";
                    String[] mimeTypes =
                            {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                    "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                    "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                    "text/plain"};
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                        if (mimeTypes.length > 0) {
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                        }
                    } else {
                        String mimeTypesStr = "";
                        for (String mimeType : mimeTypes) {
                            mimeTypesStr += mimeType + "|";
                        }
                        intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
                    }
                }
                else if(file.equals("pdf")){
                    String[] mimeTypes =
                            {"application/pdf"};
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                        if (mimeTypes.length > 0) {
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                        }
                    } else {
                        String mimeTypesStr = "";
                        for (String mimeType : mimeTypes) {
                            mimeTypesStr += mimeType + "|";
                        }
                        intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
                    };
                }
                else {
                    System.out.println("fileType - " + fileType);
                    intent.setType(fileType);
                }
                startActivityForResult(intent, PICKFILE_RESULT_CODE);

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Boolean error = false;

                if (TextUtils.isEmpty(txtUrl.getText().toString())) {
                    tvUploadError.setVisibility(View.VISIBLE);
                    error = true;
                }
                if (!error) {
                    new UploadFileToServer().execute();
                }

            }
        });
        return view;
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    //String FilePath = data.getData().getPath();
                    Uri uri = data.getData();
                    FilePath = ImageFilePath.getPath(getContext(), uri);
                    txtUrl.setText(FilePath);
                    tvUploadError.setVisibility(View.GONE);
                    // uploadFile(FilePath);

                }
                break;


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(getContext(), "Access Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    private class UploadFileToServer extends AsyncTask<String, String, String> {

        File sourceFile;
        int totalSize = 0;
        String FILE_UPLOAD_URL = getString(R.string.baseUrl) + "DocumentService/uploadFile";


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            // donut_progress.setProgress(0);
            //uploader_area.setVisibility(View.GONE); // Making the uploader area screen invisible
            //progress_area.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            String filePath = txtUrl.getText().toString();
            System.out.println("filePath - " + filePath);
            System.out.println("FILE_UPLOAD_URL - " + FILE_UPLOAD_URL);
            sourceFile = new File(filePath);
            System.out.println("sourceFile - " + sourceFile);
            totalSize = (int) sourceFile.length();
            System.out.println("totalSize - " + totalSize);
            pDialog = Utility.createSweetAlertDialog(getContext());
            pDialog.show();

        }

        @Override
        protected void onProgressUpdate(String... progress) {
            // Log.d("PROG", progress[0]);
            //donut_progress.setProgress(Integer.parseInt(progress[0])); //Updating progress
        }

        @Override
        protected String doInBackground(String... args) {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = sourceFile.getName();
            StringBuilder builder = null;
            try {
                connection = (HttpURLConnection) new URL(FILE_UPLOAD_URL).openConnection();
                connection.setRequestMethod("POST");
                String boundary = "---------------------------boundary";
                String tail = "\r\n--" + boundary + "--\r\n";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);

                String metadataPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                        + "" + "\r\n";

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"file\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";

                long fileLength = sourceFile.length() + tail.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                String stringData = metadataPart + fileHeader;

                long requestLength = stringData.length() + fileLength;
                connection.setRequestProperty("Content-length", "" + requestLength);
                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(stringData);
                out.flush();

                int progress = 0;
                int bytesRead = 0;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead; // Here progress is total uploaded bytes

                    publishProgress("" + (int) ((progress * 100) / totalSize)); // sending progress percent to publishProgress
                }

                // Write closing boundary and close stream
                out.writeBytes(tail);
                out.flush();
                out.close();

                /* Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
*/
                BufferedReader reader;
                int responseCode = connection.getResponseCode();
                System.out.println("responseCode- " + responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    StringBuilder stringBuilder = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }
                    return stringBuilder.toString();
                } else {
                    return null;
                }

            } catch (Exception e) {
                // Exception
                System.out.println("Exception ");
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // Log.e("Response", "Response from server: " + result);
            System.out.println("Response from server: " + result);
            pDialog.dismiss();
            if (TextUtils.isEmpty(result)) {
                //Show error - login failed
                //showProgress(false);
                Log.d("Upload Status", "Upload failed");
                tvUploadError.setVisibility(View.VISIBLE);
                ;
            } else {
                Log.d("Upload Status", "Upload success");
                tvUploadError.setVisibility(View.GONE);
                // shareUploads(result);

                Document document = new Document();
                document.setUploaderId(loggedInUser.getMobileNumber());
                System.out.print("sourceFile:" + sourceFile.getName());
                document.setDocumentPath(FilePath);
                document.setFileName(sourceFile.getName());
                document.setStatus("A");
                document.setDocumentType(file);
                Gson gson = new Gson();

                String docJson = gson.toJson(document);
                new RegistrationTask().execute(docJson);
            }

        }

    }

    /*
                @Override
                public void onActivityResult(int requestCode,int resultCode, Intent data) {
                    // TODO Auto-generated method stub

                    switch (requestCode) {

                        case 7:
                            switch (requestCode) {
                                case PICKFILE_RESULT_CODE:
                                    if (resultCode == RESULT_OK) {
                                        //String FilePath = data.getData().getPath();
                                        Uri uri = data.getData();
                                        String FilePath = ImageFilePath.getPath(getContext(), uri);
                                        txtUrl.setText(FilePath);
                                        tvBrowseError.setVisibility(View.GONE);
                                        //uploadFile(FilePath);

                                    }
                                    break;


                                if (resultCode == RESULT_OK) {

                                    String Pathname = data.getData().getPath();
                                    txtUrl.setText(Pathname);

                                    Toast.makeText(getContext(), Pathname, Toast.LENGTH_LONG).show();

                                }
                                break;

                            }
                    }
                }

         */
       /* btnUpload.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {



            new AsyncTask<Void,Void,String>(){

                @Override
                protected String doInBackground(Void... voids) {


                    return new HttpManager().getData(getString(R.string.baseUrl)+"DocumentService/uploadDocument/");
                }

                @Override
                protected void onPostExecute(String s) {
                    System.out.println("Result =" + s);

                    if(TextUtils.isEmpty(s)){
                        Toast.makeText(getContext(),"Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        SessionManager sessionManager = new SessionManager(getContext());
                        sessionManager.putString("loggedInUser", s);

                        Intent intent = new Intent(getContext(), ActivityHome.class);
                        startActivity(intent);
                       // finish();
                    }
                }
            }.execute();




        }


    });
    return view;*/
    class RegistrationTask extends AsyncTask<String, Void, String> {
        SweetAlertDialog pDialog;
        @Override
        protected void onPreExecute() {
            pDialog =Utility.createSweetAlertDialog(getContext());
            pDialog.show();}

        @Override
        protected String doInBackground(String... strings) {
            String docJson = strings[0];
            HttpManager httpManager = new HttpManager();
            // String result = httpManager.postData("http://192.168.43.210:8080/AndroidRemoteAccess/webapi/UserService/registerUser", userJson);
            return new HttpManager().postData(getString(R.string.baseUrl) + "DocumentService/uploadDocument", docJson);

            // return result;
            //return userJson;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            System.out.println("Result - " + result);
            if (TextUtils.isEmpty(result)) {
                Toast.makeText(getContext(), "Upload is unsuccessful!", Toast.LENGTH_SHORT).show();
            } else {
                getFragmentManager().beginTransaction().replace(R.id.mainLayout,new FragmentMyFiles()).addToBackStack(null).commit();
            }
        }
    }


}






