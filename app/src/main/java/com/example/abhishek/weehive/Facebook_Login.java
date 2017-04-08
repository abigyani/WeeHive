package com.example.abhishek.weehive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Facebook_Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mListener;
    private CallbackManager mCallbackManager;
    private LoginButton mLoginButton;
    private ProgressDialog mProgress;
    String email,gender,name,id;
    private DatabaseReference mDatabase;
    private CharSequence s;
    private String curr_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook__login);

        mLoginButton = (LoginButton) findViewById(R.id.facebook_login_btn);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.keepSynced(true);

        mProgress = new ProgressDialog(this);

        Date d = new Date();
        s  = DateFormat.format("EEEE, MMMM d, yyyy, hh:mm:ss a", d.getTime());
        curr_time = s.toString();

         mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //mDatabase.child(mAuth.getCurrentUser().getUid()).child("Id").setValue(id);
                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("Last").setValue("ONLINE");
                    startActivity(new Intent(Facebook_Login.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(Facebook_Login.this, "Landed Correctly", Toast.LENGTH_SHORT).show();
                }
            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton.setReadPermissions("email","public_profile");
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(Facebook_Login.this, "Signed In as\n"+mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                mProgress.setMessage("Signing In ...");
                mProgress.show();
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    //Toast.makeText(Facebook_Login.this, "User Added", Toast.LENGTH_SHORT).show();
                                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("Name").setValue(name);
                                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("Email").setValue(email);
                                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("Gender").setValue(gender);
                                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("Id").setValue(id);
                                    mDatabase.child(mAuth.getCurrentUser().getUid()).child("Last").setValue("ONLINE");
                                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                                                startActivity(new Intent(Facebook_Login.this,MainActivity.class));
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else
                                    Toast.makeText(Facebook_Login.this, "Error", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                                finish();
                            }
                        });

                //Getting user info
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                setProfileToView(object);
                            }
                        }
                );
                Bundle param = new Bundle();
                param.putString("fields","id,name,email,gender,birthday");
                request.setParameters(param);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Facebook_Login.this, "Error Ocurred", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }
        });
    }

    private void setProfileToView(JSONObject object) {
        try {
            email = object.getString("email");
            gender = object.getString("gender");
            name = object.getString("name");
            id = object.getString("id");
        } catch (JSONException e) {
            Toast.makeText(this, "Catch", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

     @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mListener);
    }
}
