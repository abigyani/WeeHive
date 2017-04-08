package com.example.abhishek.weehive;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Message_Page extends AppCompatActivity {

    DatabaseReference mDatabase;
    TextView tv;
    Button b;
    private String ALLOWED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        tv = (TextView) findViewById(R.id.tv_message);
        b = (Button) findViewById(R.id.button2);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("1_Notice");
        mDatabase.keepSynced(true);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Message")) {
                    tv.setText(dataSnapshot.child("Message").getValue().toString());
                    ALLOWED = dataSnapshot.child("Allowed").getValue().toString();
                } else {
                    startActivity(new Intent(Message_Page.this,Facebook_Login.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Message_Page.this, "Clicked", Toast.LENGTH_SHORT).show();
                if (ALLOWED.equals("YES")) {
                    startActivity(new Intent(Message_Page.this,Facebook_Login.class));
                    finish();
                } else {
                    //Toast.makeText(Message_Page.this, ALLOWED, Toast.LENGTH_SHORT).show();
                    mDatabase.child("Link").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String link = dataSnapshot.getValue().toString();
                            Intent viewIntent = new Intent("android.intent.action.VIEW",
                                    Uri.parse(link));
                            startActivity(viewIntent);
                            //finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(Message_Page.this,Facebook_Login.class));
                finish();
                return false;
            }
        });
    }
}
