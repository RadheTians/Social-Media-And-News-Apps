package com.example.chatapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference takeReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private CircleImageView displayImage;
    private TextView displayName;
    private TextView displayStatus;
    private Button statusbtn;
    private Button imagebtn;
    private StorageReference mStorageRef;
    private ProgressDialog mProgress;
    private static final int ImageSize=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imagebtn=(Button) findViewById(R.id.settings_image_btn);
        statusbtn=(Button)findViewById(R.id.settings_status_btn);
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String url=currentUser.getUid();
        displayImage=(CircleImageView)findViewById(R.id.settings_image);
        displayName=(TextView)findViewById(R.id.settings_name);
        displayStatus=(TextView) findViewById(R.id.settings_status);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        takeReference= FirebaseDatabase.getInstance().getReference().child("Users").child(url);
        firebaseFirestore = FirebaseFirestore.getInstance();

        takeReference.keepSynced(true);

        takeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                final String Image=dataSnapshot.child("image").getValue().toString();
                String Thamb_Image=dataSnapshot.child("thambimage").getValue().toString();
                displayName.setText(name);
                displayStatus.setText(status);
                if(!Image.equals("deafult")) {

                    Picasso.get().load(Image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar).into(displayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(Image).placeholder(R.drawable.default_avatar).into(displayImage);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {




            }
        });
        statusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status=displayStatus.getText().toString();
                Intent statusIntent=new Intent(SettingsActivity.this,StatusActivity.class);
                statusIntent.putExtra("status_value",status);
                startActivity(statusIntent);
            }
        });
        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SettingsActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(SettingsActivity.this);

                    }

                } else {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(SettingsActivity.this);

                }

                /** Log.d("Called","me");
                 Intent imageData=new Intent();
                 imageData.setType("image/*");
                 imageData.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(imageData,"ACHIVE IMAGE"),ImageSize);

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ImageSize && requestCode == RESULT_OK){
            Toast.makeText(SettingsActivity.this,"Picture has picked",Toast.LENGTH_LONG).show();
            Uri imageUrl=data.getData();
            CropImage.activity(imageUrl).setAspectRatio(1,1).start(SettingsActivity.this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(SettingsActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Wait for while");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());
                final String url=currentUser.getUid();
                try {


                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();




                final StorageReference path=mStorageRef.child("Picture_Images").child(url+".png");


                final StorageReference thumb_filepath = mStorageRef.child("Profile_Images").child("thumbs").child(url + ".jpg");

                path.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Uri downloadUrl = uri;
                                final String fileUrl = downloadUrl.toString();


                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String thumb_image=uri.toString();
                                                DocumentReference contact = firebaseFirestore.collection("Users").document(url);
                                                contact.update("image", fileUrl);
                                                contact.update("thambimage", thumb_image);
                                                takeReference.child("thambimage").setValue(thumb_image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mProgress.dismiss();
                                                            Toast.makeText(SettingsActivity.this, "Thamb Picture has uploaded", Toast.LENGTH_LONG).show();

                                                        }
                                                        else {
                                                            mProgress.dismiss();
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(SettingsActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();


                                                        }

                                                    }
                                                });
                                            }
                                        });

                                    }
                                });

                                takeReference.child("image").setValue(fileUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mProgress.dismiss();
                                            Toast.makeText(SettingsActivity.this, "Picture has uploaded", Toast.LENGTH_LONG).show();

                                        }
                                        else {
                                            mProgress.dismiss();
                                            String error = task.getException().getMessage();
                                            Toast.makeText(SettingsActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();


                                        }
                                    }

                                });


                            }
                        });

                    }
                });

                        /**addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Uri download_url = task.getResult();
                            takeReference.child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        mProgress.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Picture has uploaded", Toast.LENGTH_LONG).show();




                                    }
                                }
                            });

                        }
                        else {
                            Toast.makeText(SettingsActivity.this,"Uploading Error",Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        }
                    }
                );*/
                 }catch (Exception e){}


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }




    }

    /**public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/

}
