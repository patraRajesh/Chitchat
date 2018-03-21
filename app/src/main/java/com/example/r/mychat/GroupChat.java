package com.example.r.mychat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by r on 1/13/2018.
 */

public class GroupChat extends Fragment {
    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;


    public GroupChat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.recyclerView3);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("userrdata");
        mUsersDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<GroupCModel, GViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<GroupCModel, GViewHolder>(

                GroupCModel.class,
                R.layout.usersingle2,
                GViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final GViewHolder friendsViewHolder, GroupCModel friends, int i) {

                friendsViewHolder.setgpnae(friends.getGpname());
                final String gpnamme=friends.getGpname().toString();
friendsViewHolder.mView.findViewById(R.id.btndelgp).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(getContext()," Left   "+gpnamme,Toast.LENGTH_SHORT).show();

        mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot val:dataSnapshot.getChildren()){
                   final String vall=val.getKey().toString();
                    mFriendsDatabase.child(val.getKey()).child("gpname").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            try{
                            if(dataSnapshot2.getValue().equals(gpnamme)) {
                                mFriendsDatabase.child(vall.toString()).removeValue();
                            }
                            }catch (NullPointerException e ){

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     //   mFriendsDatabase.removeValue();
    }
});
                friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(getContext() , Gpmsg.class);
                        in.putExtra("gpname" ,gpnamme);
                        startActivity(in);

                    }
                });



            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);

    }


    public static class GViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setgpnae(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.uname_single);
            userStatusView.setText(date);

        }
        public  void setuimg(String thumb , Context ctx){

            CircleImageView circleImageView=(CircleImageView) mView.findViewById(R.id.users_single_img);
            Picasso.with(ctx).load("").placeholder(R.drawable.group).into(circleImageView);
        }


    }
}
