package com.example.r.mychat;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersactivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    private EditText editText;
     int pos=1;
private FirebaseRecyclerAdapter<Userlast, UserviewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_usersactivity);

        editText=(EditText) findViewById(R.id.editText5);
        editText.setVisibility(View.INVISIBLE);

       toolbar=(Toolbar) findViewById(R.id.chatbbar);
        recyclerView=(RecyclerView) findViewById(R.id.recyone);
       setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All users");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference= FirebaseDatabase.getInstance().getReference().child("userrdata");

         firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Userlast,
                UserviewHolder>(Userlast.class, R.layout.usersingleall, UserviewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(UserviewHolder viewHolder, Userlast model, int position) {
                try{
                    viewHolder.setname(model.getName());
                    viewHolder.setstat(model.getStatus());
                    viewHolder.setuimg(model.getThumb_img(), getApplicationContext());
                }catch (NullPointerException e){

                }

                final String uid = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(AllUsersactivity.this, Profil.class);
                        in.putExtra("uid", uid);
                        startActivity(in);


                    }
                });

            }
        };



        editText.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(final Editable s) {
        Query fireq=databaseReference.orderByChild("Name").startAt(s.toString()).endAt(s.toString()+"\uf8ff");
        firebaseRecyclerAdapter  = new FirebaseRecyclerAdapter<Userlast,
            UserviewHolder>(Userlast.class, R.layout.usersingleall, UserviewHolder.class, fireq) {
        @Override
        protected void populateViewHolder(UserviewHolder viewHolder, Userlast model, int position) {
      try{
          viewHolder.setname(model.getName());
          viewHolder.setstat(model.getStatus());
          viewHolder.setuimg(model.getThumb_img(), getApplicationContext());
          pos=position;


            }catch (NullPointerException e){

      }

            final String uid = getRef(position).getKey();

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(AllUsersactivity.this, Profil.class);
                    in.putExtra("uid", uid);
                    startActivity(in);


                }
            });

        }
    };
    recyclerView.setAdapter(firebaseRecyclerAdapter);


    }
});



        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();


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

            Intent intent = new Intent(AllUsersactivity.this, AllUsersactivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.app_bar_search) {

            editText.setVisibility(View.VISIBLE);


        }


        if (item.getItemId() == R.id.menuaccsetting) {
            Intent intent = new Intent(AllUsersactivity.this, SettingActivity.class);
            //intent.putExtra("position", chatWith);
            startActivity(intent);



        }

        return true;

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
}
