package com.example.abhishek.weehive;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.digits.sdk.android.SessionListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.Date;

import io.fabric.sdk.android.Fabric;

// Exclusive Digits Login

public class LoginActivity extends AppCompatActivity {

    private static final String TWITTER_KEY = "Q4xfW3OJGwoSVXOzNd5FKmj9f";
    private static final String TWITTER_SECRET = "vI7OcYwaxGwVn8Hdf0zy9WxLySRdzXWymXT0kcPmf8noD0jKzp";
    SessionListener sessionListener;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY,TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());

        setContentView(R.layout.activity_login);

        if (Digits.getActiveSession() != null) {
            //startActivity(new Intent(this,MainActivity.class));
            Intent intent = new Intent(this, MainActivity.class);
            String number = Digits.getActiveSession().getPhoneNumber().toString();
            intent.putExtra("Mobile",number);
            startActivity(intent);
            finish();
        }

        //String session = Digits.getActiveSession().toString();
        //Toast.makeText(this, session, Toast.LENGTH_LONG).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        DigitsAuthButton digitsAuthButton = (DigitsAuthButton) findViewById(R.id.digitBtn);
        digitsAuthButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                Toast.makeText(LoginActivity.this, "Authentication Successful for" + phoneNumber, Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //String activeSession = Digits.getActiveSession().toString();
                intent.putExtra("Mobile",phoneNumber);
                startActivity(intent);
                finish();
            }

            @Override
            public void failure(DigitsException error) {
                Toast.makeText(LoginActivity.this, "Login Using Mobile Number Failed", Toast.LENGTH_SHORT).show();
            }
        });

        sessionListener = new SessionListener() {
            @Override
            public void changed(DigitsSession newSession) {
                Toast.makeText(LoginActivity.this, "Session phone is changed: " + newSession
                        .getPhoneNumber(), Toast.LENGTH_SHORT).show();
            }
        };
        Digits.addSessionListener(sessionListener);
    }

    @Override
    protected void onStop() {
        Digits.removeSessionListener(sessionListener);
        super.onStop();
    }
}
