package com.example.abhishek.weehive;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.crashlytics.android.answers.Answers;
import com.digits.sdk.android.Digits;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

public class WelcomeScreen extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "jtFoBVv9WNihHVEokJWLWR1Ac";
    private static final String TWITTER_SECRET = "ZGmqZqKOgPtl4V07Qq6RpiZqnQP7rGsaPY6YWcN7UnOMHjFlzK";


    TextView tv1,tv2,tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build(), new Answers());
        setContentView(R.layout.activity_welcome_screen);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        tv1 = (TextView) findViewById(R.id.textWee);
        tv2 = (TextView) findViewById(R.id.textHive);
        tv3 = (TextView) findViewById(R.id.textView);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.slidefromleft);
        Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.fadein);

        tv1.startAnimation(animation);
        tv2.setAnimation(AnimationUtils.loadAnimation(this,R.anim.slidefromright));
        tv3.startAnimation(animation1);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Runnable r = new Runnable() {
                    @Override
                    public void run(){
                        startActivity(new Intent(WelcomeScreen.this,Facebook_Login.class)); //<-- put your code in here.
                        finish();
                        /*DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("1_Notice");
                        mRef.keepSynced(true);
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("Message")){
                                    startActivity(new Intent(WelcomeScreen.this,Facebook_Login.class)); //<-- put your code in here.
                                    finish();
                                } else {
                                    startActivity(new Intent(WelcomeScreen.this,Facebook_Login.class)); //<-- put your code in here.
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }); */
                    }
                };
                Handler h = new Handler();
                h.postDelayed(r, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
