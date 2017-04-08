package com.example.abhishek.weehive;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_AddPost extends Fragment {


    private EditText title,desc;
    private ImageButton ib;
    private Button btn;
    private Uri imageUri=null;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private DatabaseReference refImages;


    private static final int GALLERY_REQUEST = 1;

    public Fragment_AddPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Add Post");
        View view = inflater.inflate(R.layout.fragment_addpost,container,false);
        title = (EditText) view.findViewById(R.id.editText);
        //desc = (EditText) view.findViewById(R.id.editText2);
        ib = (ImageButton) view.findViewById(R.id.imageButton);
        btn = (Button) view.findViewById(R.id.button);
        progressDialog = new ProgressDialog(getContext());
        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post").child(mAuth.getCurrentUser().getUid());
        refImages = FirebaseDatabase.getInstance().getReference().child("Images");

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

        return view;
    }

    private void startPosting() {
        final String title_val = title.getText().toString().trim();
        progressDialog.setMessage("Posting ...");
        progressDialog.show();

        if (!TextUtils.isEmpty(title_val) && imageUri != null) {
            StorageReference filepath = storageReference.child("Post_Images").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    Date d = new Date();
                    CharSequence s  = DateFormat.format("MMMM d, yyyy, hh:mm a", d.getTime());

                    DatabaseReference newPost = databaseReference.push();
                    newPost.child("Title").setValue(title_val);
                    //newPost.child("Name").setValue(desc_val);
                    newPost.child("Image").setValue(downloadUri.toString());
                    newPost.child("Time").setValue(s.toString());

                    //Images DB
                    final DatabaseReference ref=refImages.push();
                    ref.child("UID").setValue(mAuth.getCurrentUser().getUid().toString());
                    ref.child("Title").setValue(title_val);
                    ref.child("Time").setValue(s.toString());
                    ref.child("Like_count").setValue("0");
                    ref.child("View_count").setValue("0");
                    ref.child("Image").setValue(downloadUri.toString());

                    //Getting User name
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ref.child("Name").setValue(dataSnapshot.child("Name").getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    progressDialog.dismiss();
                    Fragment_Feed feed = new Fragment_Feed();
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction().replace(R.id.content_main,feed).commit();

                    Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            ib.setImageURI(imageUri);
        }
    }
}
