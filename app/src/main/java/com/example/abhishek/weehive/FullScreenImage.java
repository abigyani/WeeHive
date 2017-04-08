package com.example.abhishek.weehive;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullScreenImage extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private boolean visible = true;
    private PhotoViewAttacher photoViewAttacher;
    private String post_key;
    private  RelativeLayout rl;
    private LinearLayout ll;
    private TextView tv_name,tv_title,tv_hearts,tv_time,tv_views;
    private ImageView im;
    private ImageButton ib;
    private FirebaseAuth mAuth;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        Intent intent = getIntent();
        post_key = intent.getStringExtra("post_key");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Images").child(post_key);
        mAuth = FirebaseAuth.getInstance();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Images").child(post_key);

        rl = (RelativeLayout) findViewById(R.id.activity_full_screen_image);
        ll = (LinearLayout) findViewById(R.id.full_image_ll);
        tv_title = (TextView) findViewById(R.id.view_title);
        tv_name = (TextView) findViewById(R.id.view_name);
        tv_hearts = (TextView) findViewById(R.id.view_hearts);
        tv_time = (TextView) findViewById(R.id.view_time);
        tv_views = (TextView) findViewById(R.id.view_views);
        im = (ImageView) findViewById(R.id.full_image);
        ib = (ImageButton) findViewById(R.id.click_heart);

        //setValues();

        photoViewAttacher = new PhotoViewAttacher(im);

        mDatabase.keepSynced(true);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                tv_title.setText(dataSnapshot.child("Title").getValue().toString());
                tv_name.setText(dataSnapshot.child("Name").getValue().toString());
                tv_hearts.setText(dataSnapshot.child("Like_count").getValue().toString());
                tv_time.setText(dataSnapshot.child("Time").getValue().toString());
                tv_views.setText(dataSnapshot.child("View_count").getValue().toString());

                //Image
                Picasso.with(getBaseContext()).load(dataSnapshot.child("Image").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(im, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getBaseContext()).load(dataSnapshot.child("Image").getValue().toString()).into(im);
                    }
                });
                photoViewAttacher.update();

                //heart
                if(dataSnapshot.child("Like").hasChild(mAuth.getCurrentUser().getUid())){
                    flag = Integer.parseInt(dataSnapshot.child("Like").child(mAuth.getCurrentUser().getUid()).getValue().toString());
                    switch (flag){
                        case 0 : ib.setImageResource(R.drawable.white_heart);
                            break;

                        case 1 : ib.setImageResource(R.drawable.red_heart);
                            break;

                        case 2 : ib.setImageResource(R.drawable.double_heart);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visible) {
                    tv_name.setVisibility(View.INVISIBLE);
                    tv_title.setVisibility(View.INVISIBLE);
                    tv_time.setVisibility(View.INVISIBLE);
                    ll.setVisibility(View.INVISIBLE);
                    visible=false;
                } else {
                    tv_name.setVisibility(View.VISIBLE);
                    tv_title.setVisibility(View.VISIBLE);
                    tv_time.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.VISIBLE);
                    visible=true;
                }
            }
        });

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.child("UID").getValue().equals(mAuth.getCurrentUser().getUid())){
                            if( flag == 0 ){
                                ib.setImageResource(R.drawable.red_heart);
                                flag++;
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int val = Integer.parseInt(dataSnapshot.child("Like_count").getValue().toString());
                                        val++;
                                        mDatabase.child("Like_count").setValue(""+val);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else if (flag == 1) {
                                ib.setImageResource(R.drawable.double_heart);
                                flag++;
                            } else if (flag == 2) {
                                ib.setImageResource(R.drawable.white_heart);
                                flag=0;
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int val = Integer.parseInt(dataSnapshot.child("Like_count").getValue().toString());
                                        val--;
                                        mDatabase.child("Like_count").setValue(""+val);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            mDatabase.child("Like").child(mAuth.getCurrentUser().getUid()).setValue(""+flag);
                            updateLikeCount();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void updateLikeCount() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String count = dataSnapshot.child("Like_count").getValue().toString();
                tv_hearts.setText(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
