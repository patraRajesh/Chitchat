package com.example.r.mychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.support.constraint.R.id.parent;

public class Message extends AppCompatActivity {


    EditText edtmsg;
    ImageView sendbtn;

    private DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<User, ViewHolder> firebaseRecyclerAdapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("position");
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(Message.this));

        edtmsg=(EditText) findViewById(R.id.edtmsg);
        sendbtn=(ImageView) findViewById(R.id.sendButton);



        StringBuilder strBuilder = new StringBuilder("https://mychat-8e4d9.firebaseio.com/Group/");
        strBuilder.append(id);
        String gp = strBuilder.toString();

       FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReferenceFromUrl(gp);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, ViewHolder>(User.class,
                android.R.layout.two_line_list_item,
                ViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, User model, final int position) {

               viewHolder.txtView.setText(model.getName() + "\n" + model.getText()
                        + "\n" + DateFormat.format("dd-MM (HH:mm)",
                        model.getMessageTime()));//changed date format

            }
        };


        recyclerView.setAdapter(firebaseRecyclerAdapter);
        databaseReference=database.getReferenceFromUrl("https://mychat-8e4d9.firebaseio.com/Group");


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                email = email.replace("@", "");
                email = email.replace(".", "");
                User friendlyMessage = new User(edtmsg.getText().toString(), email);
                databaseReference.child(id).push().setValue(friendlyMessage);

            }
        });


}



    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtView;
        View view;

        public ViewHolder(View itemView) {

            super(itemView);

            txtView = (TextView) itemView.findViewById(android.R.id.text1);

            view = itemView;


        }

    }

}
