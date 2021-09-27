package com.zerobracket.codeassignment_cg.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zerobracket.codeassignment_cg.R;
import com.zerobracket.codeassignment_cg.databinding.ActivityMainBinding;
import com.zerobracket.codeassignment_cg.databinding.ActivityPostBinding;
import com.zerobracket.codeassignment_cg.model.DAOUserInfo;
import com.zerobracket.codeassignment_cg.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private ActivityPostBinding binding;
    private Context context;
    private Long selectedDate= null;

    private static final int REQUEST_PICK_GALLERY = 1001;
    private Uri filePath;
    private StorageReference storageReference;
    private String imageName;
    private FirebaseStorage storage;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setListeners();
    }

    private void setData() {

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(binding.etUserName.getText().toString());
        userInfo.setDob(selectedDate);
        userInfo.setImageUrl(imagePath);
        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        DAOUserInfo daoUserInfo = new DAOUserInfo();
        daoUserInfo.addUser(userInfo).addOnSuccessListener(unused -> {
            sendMessage();
            progressDialog.dismiss();

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Data Upload Failed! ->"+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });



    }

    private void sendMessage() {
        RequestQueue mRequestQue = Volley.newRequestQueue(this);

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "UserAdded");
            JSONObject notificationObj = new JSONObject();
//            notificationObj.put("title", "new Order");
//            notificationObj.put("body", "New order from : " + phoneNum.replace("+", " "));
//            //replace notification with data when went send data
            json.put("notification", notificationObj);
            String key = "AAAA6WFxyxY:APA91bEv5zM4dsFL_TSx3UxZ83iJdI-7Xwd3iLzbNo9O48p1dagWyJJ_zqPLmQY8cq0LoYcRRyDVHNGP9ry54XBruAxgfgMLGpvzIdVqJf6tiTMfwaW-o086hg0wQ0datqx5DWFGFFO1";


            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    response -> Log.d("MUR", "onResponse: "),
                    error -> Log.d("MUR", "onError: " + error.networkResponse)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key="+key);
                    return header;
                }
            };


            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {
        binding.btnPost.setOnClickListener(view -> {
           if(binding.etUserName.getText()!=null&&selectedDate!=null){
              if(!binding.etUserName.getText().equals("")){
                  uploadImage();
              }
              else{
                  binding.tilUserName.setError("Please Enter User Name , Date of Birth");
              }
           }
           else{
               binding.tilUserName.setError("Please Enter User Name & Date of Birth");
           }
        });
        binding.etDob.setOnClickListener(view -> openDatePicker());
        binding.tilDob.setOnClickListener(view -> openDatePicker());
        binding.imageView.setOnClickListener(view -> choosePhotoFromGallery());
    }
    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_PICK_GALLERY);
    }

    private void openDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());
        MaterialDatePicker<Long> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());
        picker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener) selection -> {
            // Get the selected DATE RANGE
             selectedDate = (Long) selection;



            //Format the dates in ur desired display mode
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
            //Display it by setText
            binding.etDob.setText(simpleFormat.format(new Date(selectedDate)));
        });
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == REQUEST_PICK_GALLERY
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                binding.imageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();

            // Defining the child of storageReference
            imageName = "images/"+ UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(imageName);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(PostActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imagePath = uri.toString();
                                            setData();
                                        }
                                    });


                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(PostActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
        else{
            Toast.makeText(context, "Please Upload Picture", Toast.LENGTH_SHORT).show();
        }
    }
}