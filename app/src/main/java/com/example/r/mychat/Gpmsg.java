package com.example.r.mychat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Gpmsg extends AppCompatActivity {


    EditText edtmsg;
    ImageView sendbtn;

    private DatabaseReference databaseReference,databaseReference5;
    FirebaseRecyclerAdapter<User, Message.ViewHolder> firebaseRecyclerAdapter;
    private RecyclerView recyclerView;
    String gp;
    String gpname;
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
    private Toolbar mtoolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpmsg);



        mtoolbar = (Toolbar) findViewById(R.id.chatbar);
        setSupportActionBar(mtoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

      Intent intent = getIntent();
       gpname = intent.getStringExtra("gpname");

        getSupportActionBar().setTitle(gpname);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerVie);
    //    recyclerView.setLayoutManager(new LinearLayoutManager(Gpmsg.this));
        edtmsg=(EditText) findViewById(R.id.edtmsg);
        sendbtn=(ImageView) findViewById(R.id.sendButton);


        StringBuilder strBuilder = new StringBuilder("https://mychat-8e4d9.firebaseio.com/Group/");
        strBuilder.append(gpname);
        gp = strBuilder.toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReferenceFromUrl(gp);
        mRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.message_swipe_layout2);
        mAdapter=new PersonalAdapter(messagelist);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Query messageQuery = databaseReference.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        ImageView btnadd=(ImageView) findViewById(R.id.btnadd);

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

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();


            }
        });


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

                recyclerView.scrollToPosition(messagelist.size() - 1);

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








        databaseReference5=database.getReferenceFromUrl("https://mychat-8e4d9.firebaseio.com/Group");


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                 String email = user.getUid();

                Map messagemap=new HashMap();
                messagemap.put("message",edtmsg.getText().toString());
                messagemap.put("seen",false);
                messagemap.put("type","text");
                messagemap.put("time", ServerValue.TIMESTAMP);
                messagemap.put("from",email);


                edtmsg.setText("");

                databaseReference.child(gpname).push().updateChildren(messagemap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });



            }
        });


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.mainmenugp, menu);
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==11 &&resultCode==RESULT_OK){

            Uri imageuri = data.getData();
            final DatabaseReference usermsgref=databaseReference.child(gpname).push();
            final String push_id=usermsgref.getKey();

            StorageReference filepath = mStorageRef.child("message_img").child(push_id+".jpg");

            filepath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        String email = user.getUid();
                        final String down_url_img=task.getResult().getDownloadUrl().toString();
                        Map messagemap=new HashMap();
                        messagemap.put("message",down_url_img);
                        messagemap.put("seen",false);
                        messagemap.put("type","image");
                        messagemap.put("time",ServerValue.TIMESTAMP);
                        messagemap.put("from",email);


                        usermsgref.updateChildren(messagemap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });

                    }
                }
            });


        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.mlogout) {

            FirebaseAuth.getInstance().signOut();


        }
        if (item.getItemId() == R.id.addmem) {
            Toast.makeText(Gpmsg.this,gp,Toast.LENGTH_SHORT);
            Intent intent = new Intent(Gpmsg.this, AllUsersactivityadd.class);
            intent.putExtra("gpname",gpname);
            startActivity(intent);

        }
        if (item.getItemId() == R.id.btnallusers) {

            Intent intent = new Intent(Gpmsg.this, AllUsersactivity.class);
            startActivity(intent);
        }


        if (item.getItemId() == R.id.menuaccsetting) {
            Intent intent = new Intent(Gpmsg.this, SettingActivity.class);
            startActivity(intent);



        }

        return true;

    }




    private void loadMoreMessages() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReferenceFromUrl(gp);
        Query messageQuery = databaseReference.orderByKey().endAt(mLastKey).limitToLast(10);

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
