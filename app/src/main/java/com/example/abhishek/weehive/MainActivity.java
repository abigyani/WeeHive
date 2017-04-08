package com.example.abhishek.weehive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int back_pressed_counter=0;
    public FloatingActionButton fab;
    private TextView tv_user_name;
    private TextView tv_user_email;
    private ProfilePictureView iv_user_image;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String curr_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Answers());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //tv_user_name.setText("Abhishek");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_AddPost fragment_addPost = new Fragment_AddPost();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.content_main,fragment_addPost).commit();
                fab.setVisibility(View.GONE);
                back_pressed_counter=0;
                //setTitle("Add Post");
            }
        });

        Fragment_Feed feed = new Fragment_Feed();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content_main, feed).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        tv_user_name = (TextView) header.findViewById(R.id.user_name);
        tv_user_email = (TextView) header.findViewById(R.id.user_email);
        iv_user_image = (ProfilePictureView) header.findViewById(R.id.user_image);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tv_user_name.setText(dataSnapshot.child("Name").getValue().toString());
                tv_user_email.setText(dataSnapshot.child("Email").getValue().toString());
                iv_user_image.setPresetSize(ProfilePictureView.NORMAL);
                iv_user_image.setProfileId(dataSnapshot.child("Id").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*private void onKeyMetic() {
        //Sign Up Monitor
        Answers.getInstance().logSignUp(new SignUpEvent()
                .putMethod("Digits")
                .putSuccess(true));
        //Log In Monitor
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod("Digits")
                .putCustomAttribute("Login Number",Digits.getActiveSession().getPhoneNumber().toString())
                .putSuccess(true));
        Answers.getInstance().logCustom(new CustomEvent("Phone Number")
                .putCustomAttribute("Number",Digits.getActiveSession().getPhoneNumber().toString()));
        Answers.getInstance().logCustom(new CustomEvent("Second Wala")
                .putCustomAttribute(Digits.getActiveSession().getPhoneNumber().toString(),"Secondwala number"));
    } */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            back_pressed_counter=0;
        } else {
            //super.onBackPressed();
            if(back_pressed_counter==0){
                //Fragment_Feed feed = new Fragment_Feed();
                //FragmentManager manager = getSupportFragmentManager();
                //manager.beginTransaction().replace(R.id.content_main, feed).commit();
                back_pressed_counter++;
                fab.setVisibility(View.VISIBLE);
            }
            else if(back_pressed_counter==1) {
                Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
                back_pressed_counter++;
            }
            else if(back_pressed_counter==2) {
                back_pressed_counter=0;
                finish();
                //super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Fragment_Setting setting = new Fragment_Setting();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, setting).commit();
            back_pressed_counter=0;
            fab.setVisibility(View.GONE);
        } else if (id == R.id.action_signOut){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(this,Facebook_Login.class));
            finish();
        } else if (id == R.id.exit){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.feed) {
            // Handle the camera action
            Fragment_Feed feed = new Fragment_Feed();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, feed).commit();
            back_pressed_counter=0;
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.my_feed) {
            Fragment_My_Feed myfeed = new Fragment_My_Feed();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, myfeed).commit();
            back_pressed_counter=0;
            fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.settings) {
            Fragment_Setting setting = new Fragment_Setting();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, setting).commit();
            back_pressed_counter=0;
            fab.setVisibility(View.GONE);
        } else if (id == R.id.sign_out) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(this,Facebook_Login.class));
            finish();
        } else if(id == R.id.exit){
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Date d = new Date();
        CharSequence s  = DateFormat.format("MMMM d, hh:mm a", d.getTime());
        curr_time = s.toString();
        mDatabase.child("Last").setValue(curr_time);
    }
}
