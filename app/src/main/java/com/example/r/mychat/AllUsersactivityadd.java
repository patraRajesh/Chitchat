package com.example.r.mychat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersactivityadd extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseReference databaseReference,databaseReference4;
    private DatabaseReference myref;
    private DatabaseReference myref2;
    private EditText editText;
private FirebaseRecyclerAdapter<Userlast,UserviewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_usersactivity);

        Intent intent = getIntent();
        final String gp = intent.getStringExtra("gpname").toString();

    final TextView ttxv=(TextView) findViewById(R.id.textView9);

        toolbar=(Toolbar) findViewById(R.id.chatbbar);
        recyclerView=(RecyclerView) findViewById(R.id.recyone);
       setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference= FirebaseDatabase.getInstance().getReference().child("userrdata");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myref = database.getReferenceFromUrl("https://mychat-8e4d9.firebaseio.com/Group");
        myref2 = database.getReferenceFromUrl("https://mychat-8e4d9.firebaseio.com/");


        editText=(EditText) findViewById(R.id.editText5);
        editText.setVisibility(View.INVISIBLE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged( Editable s) {

            Query fireq=databaseReference.orderByChild("Name").startAt(s.toString()).endAt(s.toString()+"\uf8ff");
                        firebaseRecyclerAdapter   =new FirebaseRecyclerAdapter<Userlast,
                        UserviewHolder>(Userlast.class,R.layout.usersingle,UserviewHolder.class,fireq) {
                    @Override
                    protected void populateViewHolder(UserviewHolder viewHolder, Userlast model, int position) {


                            viewHolder.setname(model.getName());
                            viewHolder.setstat(model.getStatus());
                            viewHolder.setuimg(model.getThumb_img(), getApplicationContext());


                        final String uid=getRef(position).getKey();
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String email = user.getUid();

                                databaseReference4= FirebaseDatabase.getInstance().getReference().child("userrdata").child(uid);
                                databaseReference4.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String dname=dataSnapshot.child("Name").getValue().toString();
                                        ttxv.setText("User Added "+dname);
                                        Map messagemap=new HashMap();
                                        messagemap.put("message","Added "+dname);
                                        messagemap.put("seen",false);
                                        messagemap.put("type","text");
                                        messagemap.put("time", ServerValue.TIMESTAMP);
                                        messagemap.put("from",email);
                                        myref2.child("GroupChat").child(uid).push().child("gpname").setValue(gp);
                                        myref.child(gp).push().updateChildren(messagemap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });

                    }
                };

                recyclerView.setAdapter(firebaseRecyclerAdapter);

            }
        });












        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Userlast,
                UserviewHolder>(Userlast.class,R.layout.usersingle,UserviewHolder.class,databaseReference) {
            @Override
                   protected void populateViewHolder(UserviewHolder viewHolder, Userlast model, int position) {

                viewHolder.setname(model.getName());
                viewHolder.setstat(model.getStatus());
                viewHolder.setuimg(model.getThumb_img(),getApplicationContext());


                final String uid=getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String email = user.getUid();

                        databaseReference= FirebaseDatabase.getInstance().getReference().child("userrdata").child(uid);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String dname=dataSnapshot.child("Name").getValue().toString();

                                ttxv.setText("User Added "+dname);

                        Map messagemap=new HashMap();
                        messagemap.put("message","Added "+dname);
                        messagemap.put("seen",false);
                        messagemap.put("type","text");
                        messagemap.put("time", ServerValue.TIMESTAMP);
                        messagemap.put("from",email);


                        myref2.child("GroupChat").child(uid).push().child("gpname").setValue(gp);



                        myref.child(gp).push().updateChildren(messagemap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {




                            }
                        });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public static class UserviewHolder extends RecyclerView.ViewHolder{

        View mView;
        public UserviewHolder(View itemView) {
            super(itemView);

            mView =itemView;
        }

        public void setname(String name){

            TextView txtname=(TextView) mView.findViewById(R.id.uname_single);
            txtname.setText(name);

        }

        public void setstat(String stat){

            TextView txtstat=(TextView) mView.findViewById(R.id.Status_single);
            txtstat.setText(stat);

        }
        public  void setuimg(String thumb , Context ctx){

            CircleImageView circleImageView=(CircleImageView) mView.findViewById(R.id.users_single_img);
            Picasso.with(ctx).load(thumb).placeholder(R.drawable.avatar).into(circleImageView);
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.mainmenuallu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.mlogout) {

            FirebaseAuth.getInstance().signOut();

        }
        if (item.getItemId() == R.id.btnallusers) {

            Intent intent = new Intent(AllUsersactivityadd.this, AllUsersactivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.app_bar_search) {

            editText.setVisibility(View.VISIBLE);


        }


        if (item.getItemId() == R.id.menuaccsetting) {
            Intent intent = new Intent(AllUsersactivityadd.this, SettingActivity.class);
            //intent.putExtra("position", chatWith);
            startActivity(intent);



        }

        return true;

    }
}