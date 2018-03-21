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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
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

public class FriendReq extends Fragment {private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase,mFriendsDatabase2;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;


    public FriendReq() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friendsf, container, false);
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.recyclerView2);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase2 = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
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

        FirebaseRecyclerAdapter<FriendsRe, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<FriendsRe, FriendsViewHolder>(

                FriendsRe.class,
                R.layout.usersingle,
                FriendsViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, FriendsRe friends, int i) {

                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(i).getKey();


                friendsViewHolder.mView.findViewById(R.id.btndel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mFriendsDatabase.child(list_user_id).removeValue();
                            mFriendsDatabase2.child(list_user_id).child(mCurrent_user_id).removeValue();
                        }catch (NullPointerException e){

                        }

                    }
                });

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("Name").getValue().toString();
                        friendsViewHolder.setName(userName);


                        if(dataSnapshot.hasChild("online")) {

                            final String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setOnline(userOnline);

                        }

                        final  String userThumb = dataSnapshot.child("thumb_img").getValue().toString();


                        friendsViewHolder.setuimg(userThumb, getContext());


                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]=new CharSequence[] { "Open Profile","Send msg"            };

                                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

                                builder.setTitle("Select");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which==0){
                                            Intent in = new Intent(getContext() , Profil.class);
                                            in.putExtra("uid" ,list_user_id);
                                            startActivity(in);


                                        }
                                        if (which==1){
                                            Intent in = new Intent(getContext() , Chatpersonal.class);
                                            in.putExtra("cuserid" ,list_user_id);
                                            in.putExtra("uname",userName);
                                            startActivity(in);
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);


    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.Status_single);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.uname_single);
            userNameView.setText(name);

        } public void setOnline(String name){
            if(name.equals(true)) {

                TextView txtv = (TextView) mView.findViewById(R.id.textView5);
                txtv.setText("Online");
            }else {
                TextView txtv = (TextView) mView.findViewById(R.id.textView5);
                txtv.setText("offline");
            }
        }
        public  void setuimg(String thumb , Context ctx){

            CircleImageView circleImageView=(CircleImageView) mView.findViewById(R.id.users_single_img);
            Picasso.with(ctx).load(thumb).placeholder(R.drawable.avatar).into(circleImageView);
        }






    }

}
