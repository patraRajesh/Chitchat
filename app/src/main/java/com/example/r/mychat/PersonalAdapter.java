package com.example.r.mychat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by r on 1/14/2018.
 */

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.MessageViewHolder> {


    private FirebaseAuth mAuth;
    private List<PersonalModel> mMessageList;
    private DatabaseReference mUserDatabase;

    public PersonalAdapter(List<PersonalModel> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.textView8);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.mess_singlr_name);
            messageImage = (ImageView) view.findViewById(R.id.mess_single_img);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        PersonalModel c = mMessageList.get(i);
        final String from_user = c.getFrom().toString();

        String message_type = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("userrdata").child(from_user);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String name = dataSnapshot.child("Name").getValue().toString();
                viewHolder.displayName.setText(name);
                String image = dataSnapshot.child("thumb_img").getValue().toString();
                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.avatar).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.avatar).into(viewHolder.messageImage);

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
