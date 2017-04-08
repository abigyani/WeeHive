package com.example.abhishek.weehive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class People_Detail extends AppCompatActivity {

    DatabaseReference mDatabase;
    String id,uid;
    Query query;
    FirebaseAuth people_auth;
    RecyclerView rc;
    DatabaseReference mData;
    int val,flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people__detail);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        uid = getIntent().getStringExtra("uid");

        people_auth = FirebaseAuth.getInstance();
        query = FirebaseDatabase.getInstance().getReference().child("Images").orderByChild("UID").equalTo(uid);
        query.keepSynced(true);

        //Toast.makeText(this, uid , Toast.LENGTH_LONG).show();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabase.keepSynced(true);
        final TextView people_name = (TextView) findViewById(R.id.people_name);
        final TextView people_email = (TextView) findViewById(R.id.people_email);
        final TextView people_time = (TextView) findViewById(R.id.people_time);
        final ProfilePictureView dp = (ProfilePictureView) findViewById(R.id.people_pic);
        dp.setPresetSize(ProfilePictureView.NORMAL);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                people_name.setText(dataSnapshot.child("Name").getValue().toString());
                people_email.setText(dataSnapshot.child("Email").getValue().toString());
                dp.setProfileId(dataSnapshot.child("Id").getValue().toString());
                if(dataSnapshot.child("Last").getValue().equals("ONLINE")) {
                    people_time.setTextColor(Color.GREEN);
                    people_time.setText("ONLINE");
                } else {
                    people_time.setText("Last Seen : "+dataSnapshot.child("Last").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rc = (RecyclerView) findViewById(R.id.people_recyclerView);
        rc.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        rc.setLayoutManager(lm);

        mData = FirebaseDatabase.getInstance().getReference().child("Images");
        mData.keepSynced(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final FirebaseRecyclerAdapter<Post,Fragment_My_Feed.PostViewHolder> people_adapter = new FirebaseRecyclerAdapter<Post, Fragment_My_Feed.PostViewHolder>(
                Post.class,
                R.layout.postrow,
                Fragment_My_Feed.PostViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final Fragment_My_Feed.PostViewHolder viewHolder, Post model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setName(model.getName());
                viewHolder.setImage(getBaseContext(),model.getImage());
                viewHolder.setLikeCount(model.getLike_count());
                viewHolder.setViewCount(model.getView_count());

                viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), "Image Clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(People_Detail.this, FullScreenImage.class);
                        intent.putExtra("post_key",post_key);
                        startActivity(intent);
                    }
                });

                viewHolder.like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mData.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child("UID").getValue().equals(people_auth.getCurrentUser().getUid())){
                                    if(dataSnapshot.child("Like").hasChild(people_auth.getCurrentUser().getUid())){
                                        flag = Integer.parseInt(dataSnapshot.child("Like").child(people_auth.getCurrentUser().getUid()).getValue().toString());
                                        if( flag == 0 ){
                                            viewHolder.like_btn.setImageResource(R.drawable.red_heart);
                                            flag++;
                                            mData.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    val = Integer.parseInt(dataSnapshot.child(post_key).child("Like_count").getValue().toString());
                                                    val++;
                                                    mData.child(post_key).child("Like_count").setValue(""+val);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        } else if (flag == 1) {
                                            viewHolder.like_btn.setImageResource(R.drawable.double_heart);
                                            flag++;
                                        } else if (flag == 2) {
                                            viewHolder.like_btn.setImageResource(R.drawable.white_heart);
                                            flag=0;
                                            mData.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    val = Integer.parseInt(dataSnapshot.child(post_key).child("Like_count").getValue().toString());
                                                    val--;
                                                    mData.child(post_key).child("Like_count").setValue(""+val);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                        mData.child(post_key).child("Like").child(people_auth.getCurrentUser().getUid()).setValue(""+flag);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //Toast.makeText(getContext(), ""+flag, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        rc.setAdapter(people_adapter);
    }
}
