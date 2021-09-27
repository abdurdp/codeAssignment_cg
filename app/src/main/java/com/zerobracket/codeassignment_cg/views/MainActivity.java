package com.zerobracket.codeassignment_cg.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.zerobracket.codeassignment_cg.R;
import com.zerobracket.codeassignment_cg.adapter.UserAdapter;
import com.zerobracket.codeassignment_cg.databinding.ActivityMainBinding;
import com.zerobracket.codeassignment_cg.databinding.UserDetailsLayoutBinding;
import com.zerobracket.codeassignment_cg.databinding.UserItemLayoutBinding;
import com.zerobracket.codeassignment_cg.interfaces.UserInfoClicked;
import com.zerobracket.codeassignment_cg.model.DAOUserInfo;
import com.zerobracket.codeassignment_cg.model.UserInfo;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private Context context;
    private DAOUserInfo doaUserInfo;
    private List<UserInfo> userInfoList = new ArrayList<>();

    private androidx.appcompat.app.AlertDialog alertDialog;
    private UserAdapter userAdapter;
    private boolean isLoading=false;
    private String key=null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = getApplicationContext();
        doaUserInfo = new DAOUserInfo();
        setListeners();
        setAdapter();
         progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        getData();
        subscribe();

    }

    private void subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("UserAdded")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
////                        String msg = getString(R.string.msg_subscribed);
//                        if (!task.isSuccessful()) {
//                            msg = getString(R.string.msg_subscribe_failed);
//                        }
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getData() {
//        binding.swipeRefresh.setRefreshing(true);
        doaUserInfo.getData(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for( DataSnapshot dataSnapshot: snapshot.getChildren()){
                    UserInfo userInfo;
                    userInfo = dataSnapshot.getValue(UserInfo.class);
                    userInfoList.add(userInfo);
                    key = dataSnapshot.getKey();
                }
                userAdapter.setItems(userInfoList);
                userAdapter.notifyDataSetChanged();

                isLoading=false;
                progressDialog.dismiss();

//                binding.swipeRefresh.setRefreshing(false);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false;

                progressDialog.dismiss();
//                binding.swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void setListeners() {
        binding.fabPost.setOnClickListener(view -> startActivity(new Intent(context,PostActivity.class)));
    }

    private void setAdapter() {
         userAdapter = new UserAdapter(userInfoList, userInfo -> {
            dialogIninit(userInfo);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.setAdapter(userAdapter);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                int lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(totalItem<lastVisible+3){
                    if(!isLoading){
                        getData();
                        isLoading=true;
                    }
                }
            }
        });
    }
    public void dialogIninit(UserInfo userInfo) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_Light_Dialog);
        UserDetailsLayoutBinding userItemLayoutBinding = UserDetailsLayoutBinding.inflate(this.getLayoutInflater());
        builder.setView(userItemLayoutBinding.getRoot());
        builder.setCancelable(true);
        userItemLayoutBinding.etUserName.setText(userInfo.getUserName());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleFormat = new SimpleDateFormat("dd MMM yyyy");
        //Display it by setText

        LocalDate birthDate;
        LocalDate currentDate;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Converting obtained Date object to LocalDate object
        Instant instant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = new Date(userInfo.getDob()).toInstant();
            ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
            LocalDate givenDate = zone.toLocalDate();
          int age =  Period.between(givenDate, LocalDate.now()).getYears();
          userItemLayoutBinding.etAge.setText(String.valueOf(age));
        }

        Picasso.get().load(userInfo.getImageUrl()).into(userItemLayoutBinding.imageView);


        userItemLayoutBinding.etDob.setText(simpleFormat.format(new Date(userInfo.getDob())));
        alertDialog = builder.create();
        alertDialog.show();
    }
}