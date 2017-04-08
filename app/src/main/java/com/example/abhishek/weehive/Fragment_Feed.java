package com.example.abhishek.weehive;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Feed extends Fragment {


    RecyclerView postlist;
    View view;
    private DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    static int flag;
    int val;
    LinearLayoutManager linearLayoutManager;
    FloatingActionButton float_button;
    Query query;

    public Fragment_Feed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Images");
        databaseReference.keepSynced(true);

        query = FirebaseDatabase.getInstance().getReference().child("Images");
        query.keepSynced(true);

        view = inflater.inflate(R.layout.fragment__my__feed, container, false);

        float_button = (FloatingActionButton) view.findViewById(R.id.feed_float_button);
        float_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                PopupMenu filter_menu = new PopupMenu(v.getContext(),v);
                MenuInflater filter_inflater = filter_menu.getMenuInflater();
                filter_inflater.inflate(R.menu.filter_menu,filter_menu.getMenu());
                filter_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.filter_like : query = databaseReference.orderByChild("Like_count");
                                                    onStart();
                                                    break;

                            case R.id.filter_popularity : //query = databaseReference.orderByChild("Pop")
                                                          //onStart();
                                                    Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
                                                    break;

                            case R.id.filter_view : query = databaseReference.orderByChild("View_count");
                                                    onStart();
                                                    break;

                            case R.id.filter_time : query = databaseReference;
                                                    onStart();
                        }
                        return false;
                    }
                });
                filter_menu.show();
            }
        });

        getActivity().setTitle("Feed");
        postlist = (RecyclerView) view.findViewById(R.id.recyclerView);
        postlist.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postlist.setLayoutManager(linearLayoutManager);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        //Enable fast scrolling
        //VerticalRecyclerViewFastScroller scroll = (VerticalRecyclerViewFastScroller) view.findViewById(R.id.fast_scroller);
        //scroll.setRecyclerView(postlist);
        //postlist.setOnScrollListener(scroll.getOnScrollListener());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Post,Fragment_My_Feed.PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, Fragment_My_Feed.PostViewHolder>(
                Post.class,
                R.layout.postrow,
                Fragment_My_Feed.PostViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final Fragment_My_Feed.PostViewHolder viewHolder, Post model, final int position) {
                //titleCheck = model.getTitle();
                final String post_key = getRef(position).getKey();
                viewHolder.setName(model.getName());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setImage(getActivity().getBaseContext(),model.getImage());
                viewHolder.setLikeCount(model.getLike_count());
                viewHolder.setViewCount(model.getView_count());


                viewHolder.tv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String uid = dataSnapshot.child("UID").getValue().toString();
                                Intent intent = new Intent(getContext(),People_Detail.class);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                viewHolder.like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child("UID").getValue().equals(mAuth.getCurrentUser().getUid())){
                                    if( flag == 0 ){
                                        viewHolder.like_btn.setImageResource(R.drawable.red_heart);
                                        flag++;
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                val = Integer.parseInt(dataSnapshot.child(post_key).child("Like_count").getValue().toString());
                                                val++;
                                                databaseReference.child(post_key).child("Like_count").setValue(""+val);
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
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                val = Integer.parseInt(dataSnapshot.child(post_key).child("Like_count").getValue().toString());
                                                val--;
                                                databaseReference.child(post_key).child("Like_count").setValue(""+val);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                    databaseReference.child(post_key).child("Like").child(mAuth.getCurrentUser().getUid()).setValue(""+flag);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //Toast.makeText(getContext(), ""+flag, Toast.LENGTH_SHORT).show();
                    }
                });

                viewHolder.option_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), ""+flag, Toast.LENGTH_SHORT).show();
                        PopupMenu popup = new PopupMenu(v.getContext(),v);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.postmenu,popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete:
                                        Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();

                                        return true;

                                    case R.id.report:
                                        Toast.makeText(getContext(), "Report", Toast.LENGTH_SHORT).show();
                                        return true;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });

                viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), post_key, Toast.LENGTH_SHORT).show();
                        databaseReference.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child("UID").getValue().equals(mAuth.getCurrentUser().getUid())){
                                    int count = Integer.parseInt(dataSnapshot.child("View_count").getValue().toString());
                                    count++;
                                    databaseReference.child(post_key).child("View_count").setValue(""+count);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(Fragment_Feed.this.getActivity(), FullScreenImage.class);
                        intent.putExtra("post_key",post_key);
                        startActivity(intent);
                    }
                });
                //Toast.makeText(getContext(), "Flag Before Fn Call : "+flag, Toast.LENGTH_SHORT).show();
                setLikeBtn(viewHolder.like_btn,post_key);
            }
        };
        postlist.setAdapter(firebaseRecyclerAdapter);
        progressDialog.dismiss();
    }

    private void setLikeBtn(final ImageButton like_btn,final String post_key) {

        //Toast.makeText(getContext(), "setLikeBtn Called"+flag, Toast.LENGTH_SHORT).show();
        databaseReference.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Like").hasChild(mAuth.getCurrentUser().getUid().toString())) {
                    flag = Integer.parseInt(dataSnapshot.child("Like").child(mAuth.getCurrentUser().getUid().toString()).getValue().toString());
                    //Toast.makeText(getContext(), ""+flag, Toast.LENGTH_SHORT).show();
                    switch (flag) {
                        case 0 : like_btn.setImageResource(R.drawable.white_heart);
                            break;

                        case 1 : like_btn.setImageResource(R.drawable.red_heart);
                            break;

                        case 2 : like_btn.setImageResource(R.drawable.double_heart);
                            break;

                        default:
                            Toast.makeText(getContext(), "Default Called", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
