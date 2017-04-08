package com.example.abhishek.weehive;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.Digits;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_My_Feed extends Fragment {

    RecyclerView postlist;
    View view;
    private DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private Query query;

    public Fragment_My_Feed() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(getContext(), "My", Toast.LENGTH_SHORT).show();
        FirebaseRecyclerAdapter<Post,PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.postrow,
                PostViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, Post model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setName(model.getName());
                viewHolder.setImage(getActivity().getBaseContext(),model.getImage());
                viewHolder.setLikeCount(model.getLike_count());
                viewHolder.setViewCount(model.getView_count());

                viewHolder.option_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(v.getContext(),v);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.postmenu,popup.getMenu());
                        //Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.delete:
                                        //Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                                        databaseReference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getContext(), "Successfuly removed the post.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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
                        //Toast.makeText(getContext(), "Image Clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Fragment_My_Feed.this.getActivity(), FullScreenImage.class);
                        intent.putExtra("post_key",post_key);
                        startActivity(intent);
                    }
                });
            }
        };
        //Toast.makeText(getContext(), titleCheck, Toast.LENGTH_SHORT).show();
        postlist.setAdapter(firebaseRecyclerAdapter);
       /* postlist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean flag=false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == recyclerView.SCROLL_STATE_SETTLING){
                    if(flag) R.layout.content_main
                    else view.findViewById(R.id.fab).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0) flag=true;
                else flag=false;
            }
        }); */

        progressDialog.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("Post").child(mAuth.getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Images");
        query = FirebaseDatabase.getInstance().getReference().child("Images").orderByChild("UID").equalTo(mAuth.getCurrentUser().getUid());
        query.keepSynced(true);

        view = inflater.inflate(R.layout.fragment__my__feed, container, false);
        getActivity().setTitle("My Feed");
        postlist = (RecyclerView) view.findViewById(R.id.recyclerView);
        postlist.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        postlist.setLayoutManager(lm);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        return view;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        View mview;
        ImageButton like_btn;
        ImageButton option_btn;
        ImageView post_image;
        TextView tv_name;

        public PostViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
            option_btn = (ImageButton) mview.findViewById(R.id.menu_button);
            post_image = (ImageView) mview.findViewById(R.id.imageView);
            like_btn = (ImageButton) mview.findViewById(R.id.heart_btn);
            tv_name = (TextView) mview.findViewById(R.id.nameText);
        }

        public void setTitle(String title){
            TextView postTitle = (TextView) mview.findViewById(R.id.titleText);
            postTitle.setText(title);
        }
        

        public void setImage(final Context context, final String image){
            //final ImageView post_image = (ImageView) mview.findViewById(R.id.imageView);
            //Picasso.with(context).load(image).fit().centerCrop().into(post_image);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(post_image);
                }
            });
        }

        public void setName(String name){
            SpannableString content = new SpannableString("By "+name);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tv_name.setText(content);
        }

        public void setLikeBtn(int likeBtn) {
            switch (likeBtn) {
                case 0 : like_btn.setImageResource(R.drawable.white_heart);
                         break;

                case 1 : like_btn.setImageResource(R.drawable.red_heart);
                         break;

                case 2 : like_btn.setImageResource(R.drawable.double_heart);
            }
        }

        public void setLikeCount(String likeCount) {
            //this.likeCount = likeCount;
            TextView like_count = (TextView) mview.findViewById(R.id.like_count);
            like_count.setText(likeCount);
        }

        public void setViewCount(String viewCount){
            TextView view_count = (TextView) mview.findViewById(R.id.views);
            view_count.setText(viewCount);
        }

        //public setButton

    }

}
