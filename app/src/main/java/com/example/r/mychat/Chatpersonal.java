package com.example.r.mychat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class Chatpersonal extends AppCompatActivity {

    private String muser, muserid;
    private DatabaseReference mrootRef,mloadmsgref;
    private Toolbar mtoolbar;
    private TextView titlr,lastseentxt;
    private CircleImageView Profileomg;
    private FirebaseAuth mAuth;
    private String mcurrid;
    private ImageView imageView,btnadd;
    private EditText edtmsg;
    private RecyclerView mmsglist;
    private StorageReference mStorageRef;
    private final List<PersonalModel> messagelist=new ArrayList<>();
    private SwipeRefreshLayout mRefreshLayout;

    private LinearLayoutManager linearLayoutManager;
    private PersonalAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatpersonal);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);

        mtoolbar = (Toolbar) findViewById(R.id.chatbar);
        setSupportActionBar(mtoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mrootRef = FirebaseDatabase.getInstance().getReference();
        mloadmsgref = FirebaseDatabase.getInstance().getReference().child("");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        imageView=(ImageView) findViewById(R.id.sendButton);
        edtmsg=(EditText) findViewById(R.id.edtmsg);

        btnadd=(ImageView) findViewById(R.id.btnadd);
        mmsglist=(RecyclerView) findViewById(R.id.msglist);

        muserid = getIntent().getStringExtra("cuserid");
        muser = getIntent().getStringExtra("uname");
        mAuth=FirebaseAuth.getInstance();
        mcurrid=mAuth.getUid();

        // getSupportActionBar().setTitle(muser);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbarview =inflater.inflate(R.layout.personalchatbar,null);
        actionBar.setCustomView(actionbarview);

        titlr=(TextView) findViewById(R.id.textView6);
        lastseentxt=(TextView) findViewById(R.id.textView7);
        Profileomg=(CircleImageView) findViewById(R.id.circlarside);

        titlr.setText(muser);
        mrootRef=FirebaseDatabase.getInstance().getReference();

       mAdapter=new PersonalAdapter(messagelist);
        linearLayoutManager=new LinearLayoutManager(this);
        mmsglist.setHasFixedSize(true);
        mmsglist.setLayoutManager(linearLayoutManager);
       mmsglist.setAdapter(mAdapter);



        

        mrootRef.child("userrdata").child(muserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("thumb_img").getValue().toString();
                if(online.equals("true")){
                    lastseentxt.setText("Online");
                }else{
                    String lastseen = dataSnapshot.child("lastseen").getValue().toString();
                    TimeAgo timeAgo=new TimeAgo();
                    long lasttime =Long.parseLong(lastseen);
                    String lastseeentime=timeAgo.TimeAgo(lasttime,Chatpersonal.this);
                    lastseentxt.setText(lastseeentime);

                }


             Picasso.with(Chatpersonal.this).load(image).placeholder(R.drawable.avatar).into(Profileomg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mrootRef.child("Personalchat").child(mcurrid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.hasChild(muserid))){

                    Map chat=new HashMap();
                    chat.put("seen",false);
                    chat.put("timestamp", ServerValue.TIMESTAMP);



                    Map chatUser=new HashMap();
                    chatUser.put("Personalchat/"+mcurrid+"/"+muserid,chat);
                    chatUser.put("Personalchat/"+muserid+"/"+mcurrid,chat);
                    mrootRef.updateChildren(chatUser, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

imageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        sendmsg();
    }
});




btnadd.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent img=new Intent();
        img.setType(
                "image/"
        );
        img.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(img, "Choose ur dP"), 11);
    }
});

      loadmessage();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();


            }
        });




    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==11 &&resultCode==RESULT_OK){

            Uri imageuri = data.getData();
            final String curr_user_ref="Personalchat/message/"+mcurrid+"/"+muserid;
            final String chat_user_ref="Personalchat/message/"+muserid+"/"+mcurrid;
            DatabaseReference usermsgref=mrootRef.child("Personalchat").child("message").child(mcurrid).child(muserid).push();
            final String push_id=usermsgref.getKey();

            StorageReference filepath = mStorageRef.child("message_img").child(push_id+".jpg");

            filepath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){

                        final String down_url_img=task.getResult().getDownloadUrl().toString();
                        Map messagemap=new HashMap();
                        messagemap.put("message",down_url_img);
                        messagemap.put("seen",false);
                        messagemap.put("type","image");
                        messagemap.put("time",ServerValue.TIMESTAMP);
                        messagemap.put("from",mcurrid);

                        Map messageusermap=new HashMap();
                        messageusermap.put(chat_user_ref+"/"+push_id,messagemap);
                        messageusermap.put(curr_user_ref+"/"+push_id,messagemap);

                        mrootRef.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });


                    }
                }
            });


        }
    }

    private void sendmsg(){

        String msg=edtmsg.getText().toString();
        if(!(TextUtils.isEmpty(msg))){
            String curr_user_ref="Personalchat/message/"+mcurrid+"/"+muserid;
            String chat_user_ref="Personalchat/message/"+muserid+"/"+mcurrid;
            DatabaseReference usermsgref=mrootRef.child("Personalchat").child("message").child(mcurrid).child(muserid).push();

            String push_id=usermsgref.getKey();

            Map messagemap=new HashMap();
            messagemap.put("message",msg);
            messagemap.put("seen",false);
            messagemap.put("type","text");
            messagemap.put("time",ServerValue.TIMESTAMP);
            messagemap.put("from",mcurrid);



            Map messageusermap=new HashMap();
            messageusermap.put(chat_user_ref+"/"+push_id,messagemap);
            messageusermap.put(curr_user_ref+"/"+push_id,messagemap);

            edtmsg.setText("");

            mrootRef.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });


        }
    }

    private void loadmessage(){




        DatabaseReference messageRef = mrootRef.child("Personalchat").child("message").child(mcurrid).child(muserid);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                PersonalModel message = dataSnapshot.getValue(PersonalModel.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }
                messagelist.add(message);
                mAdapter.notifyDataSetChanged();
                mmsglist.scrollToPosition(messagelist.size() - 1);
                mRefreshLayout.setRefreshing(false);

           }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void loadMoreMessages() {

        DatabaseReference messageRef = mrootRef.child("Personalchat").child("message").child(mcurrid).child(muserid);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                PersonalModel message = dataSnapshot.getValue(PersonalModel.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messagelist.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


             //   Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                linearLayoutManager.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}