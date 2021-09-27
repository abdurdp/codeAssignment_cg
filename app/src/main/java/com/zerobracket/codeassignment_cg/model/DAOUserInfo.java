package com.zerobracket.codeassignment_cg.model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DAOUserInfo {
    private DatabaseReference databaseReference;

    public DAOUserInfo(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference();
    }

    public Task<Void> addUser(UserInfo userInfo){
        return databaseReference.push().setValue(userInfo);
    }
    public Query getData(String key){
        if(key==null){
            return databaseReference.orderByKey().limitToFirst(5);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(5);
    }

    public DatabaseReference getImage(UserInfo userInfo){
//       return databaseReference.child(userInfo.getImageUrl());
       return databaseReference.child("image");
    }
}
