package com.example.r.mychat;

import android.app.Application;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by r on 1/10/2018.
 */

public class myChat extends Application {
    private DatabaseReference userdb;
    private FirebaseAuth mauth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setSingletonInstance(built);


        mauth=FirebaseAuth.getInstance();
        if((mauth.getCurrentUser()==null)){
            Toast.makeText(myChat.this,"failed",Toast.LENGTH_SHORT).show();
        }else{
        userdb=FirebaseDatabase.getInstance().getReference().child("userrdata").child(mauth.getCurrentUser().getUid());
        userdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    userdb.child("online").onDisconnect().setValue(false);
                    userdb.child("lastseen").onDisconnect().setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
}