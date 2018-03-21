package com.example.r.mychat;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profil extends AppCompatActivity {

    private ImageView Profileimg;
    private TextView Profilename,PStatus,  Friendscount;
    private DatabaseReference databaseReference,notificationref;
    private Button button,decline;
    private ProgressDialog progressDialog;
    private String mCurrentFriStat;
    private DatabaseReference mfrireqDatabase;
    private DatabaseReference mfriendDatabase;
    private DatabaseReference mRootDatabase;
    private FirebaseUser mCurUSer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        final String uid=getIntent().getStringExtra("uid");
        mfrireqDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mfriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mRootDatabase=FirebaseDatabase.getInstance().getReference();
        notificationref=FirebaseDatabase.getInstance().getReference().child("Notification");
        mCurUSer= FirebaseAuth.getInstance().getCurrentUser();
        progressDialog=new ProgressDialog(Profil.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        button=(Button) findViewById(R.id.button5);
        decline=(Button) findViewById(R.id.button6);
        Profileimg=(ImageView) findViewById(R.id.imageView);
        Profilename=(TextView) findViewById(R.id.textView2);
        PStatus=(TextView) findViewById(R.id.textView3);
      //  Friendscount=(TextView) findViewById(R.id.textView4);



        //finding friends

        mfrireqDatabase.child(mCurUSer.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)){
                    String req_ty=dataSnapshot.child(uid).child("request_type").getValue().toString();

                    if(req_ty.equals("received")){
                        mCurrentFriStat="req_received";
                        button.setText("Accept req");
                        decline.setEnabled(true);
                        decline.setVisibility(View.VISIBLE);

                    }else if(req_ty.equals("sent")){
                        mCurrentFriStat="req_sent";
                        button.setText("Cancel request");
                        decline.setEnabled(false);
                        decline.setVisibility(View.INVISIBLE);

                    }else{
                        mfriendDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(uid)){
                                    mCurrentFriStat="1";
                                    button.setText("UnFriend");
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCurrentFriStat="0";//not friend
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                if(mCurrentFriStat.equals("0")){
                    DatabaseReference newnotiref=mRootDatabase.child("Notification").child(uid).push();
                    String newnotiid=newnotiref.getKey();
                    HashMap<String, String> notidata = new HashMap<String, String>();
                    notidata.put("From", mCurUSer.getUid());
                    notidata.put("type", "request");
                    Map requestt=new HashMap<String,String>();
                    requestt.put("Friend_req/"+mCurUSer.getUid()+"/"+uid+"/request_type","sent");
                    requestt.put("Friend_req/"+uid+"/"+mCurUSer.getUid()+"/request_type","received");
                    requestt.put("Notification/"+uid+"/"+newnotiid,notidata);
                    mRootDatabase.updateChildren(requestt, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError!=null){
                                Toast.makeText(Profil.this, "failed",Toast.LENGTH_SHORT).show();
                            }
                            mCurrentFriStat="req_sent";
                            button.setText("Cancel req");
                            button.setEnabled(true);
                            decline.setEnabled(false);
                            decline.setVisibility(View.INVISIBLE);
                        }
                    });

                }

                if(mCurrentFriStat.equals("req_sent")) {
                    mfrireqDatabase.child(mCurUSer.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mfrireqDatabase.child(uid).child(mCurUSer.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mCurrentFriStat = "0";
                                    button.setEnabled(true);
                                    button.setText("Send Friend req");
                                }
                            });
                        }
                    });

                }

                    //req reci

                if(mCurrentFriStat.equals("req_received")) {
                    String date = DateFormat.getDateTimeInstance().format(new Date());
                    Map Friendmap = new HashMap<String, String>();
                    Friendmap.put("Friends/" + mCurUSer.getUid() + "/" + uid + "/date", date);
                    Friendmap.put("Friends/" + uid + "/" + mCurUSer.getUid() + "/date", date);
                    Friendmap.put("Friend_req/" + mCurUSer.getUid() + "/" + uid, null);
                    Friendmap.put("Friend_req/" + uid + "/" + mCurUSer.getUid(), null);

                    mRootDatabase.updateChildren(Friendmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null){
                                mCurrentFriStat="1";//friends
                                button.setEnabled(true);
                                button.setText("UnFriend");
                                decline.setEnabled(false);
                                decline.setVisibility(View.INVISIBLE);
                            }else {
                                Toast.makeText(Profil.this, "failed",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                //UnFriend

                if(mCurrentFriStat=="1"){
                    Map UnfriendMap=new HashMap<String,String>();
                    UnfriendMap.put("Friends/" + mCurUSer.getUid() + "/" + uid, null);
                    UnfriendMap.put("Friends/" + uid + "/" + mCurUSer.getUid(), null);
                    mRootDatabase.updateChildren(UnfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null){

                                mCurrentFriStat="0";//friends

                                button.setText("Send Friend Req");
                                decline.setEnabled(false);
                                decline.setVisibility(View.INVISIBLE);
                            }else {
                                Toast.makeText(Profil.this, "failed",Toast.LENGTH_SHORT).show();
                            }
                            button.setEnabled(true);
                        }
                    });
                }
            }
        });
      databaseReference= FirebaseDatabase.getInstance().getReference().child("userrdata").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dname=dataSnapshot.child("Name").getValue().toString();
                String statuss=dataSnapshot.child("Status").getValue().toString();
                String dthumbimg=dataSnapshot.child("thumb_img").getValue().toString();

                Profilename.setText(dname);
                PStatus.setText(statuss);

               Picasso.with(Profil.this).load(dthumbimg).into(Profileimg);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
