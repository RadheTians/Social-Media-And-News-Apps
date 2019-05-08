package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {



    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;

    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("FRIEND_REQUEST");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("FRIENDS");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();



        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn=(Button) findViewById(R.id.profile_decline_btn);
        mCurrent_state = "NOT_FRIENDS";

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Wait for while");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("RECEIVED")){
                                mCurrent_state="REQUEST_RECEIVED";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);
                            }
                            else if (req_type.equals("SENT")){
                                mCurrent_state="REQUEST_SENT";
                                mProfileSendReqBtn.setText("Cencel Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                            mProgressDialog.dismiss();
                        }
                        else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)) {
                                        mCurrent_state = "FRIENDS";
                                        mProfileSendReqBtn.setText("UnFriend");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                    }
                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProgressDialog.dismiss();

                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });
    mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mProfileSendReqBtn.setEnabled(false);



            if(mCurrent_state.equals("NOT_FRIENDS")){


                DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                String newNotificationId = newNotificationref.getKey();

                HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("From", mCurrent_user.getUid());
                notificationData.put("Type", "REQUEST");

                Map requestMap = new HashMap();
                requestMap.put("FRIEND_REQUEST/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "SENT");
                requestMap.put("FRIEND_REQUEST/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "RECEIVED");
                requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError != null){

                            Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                        } else {

                            mCurrent_state = "REQUEST_SENT";
                            mProfileSendReqBtn.setText("Cancel Friend Request");

                        }

                        mProfileSendReqBtn.setEnabled(true);


                    }
                });

            }


            /** if(mCurrent_state.equals("NOT_FRIENDS")){
                 mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("REQUEST_TYPE").setValue("SENT").addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).child("REQUEST_TYPE").setValue("RECEIVED").addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {

                                     if(task.isSuccessful()){

                                         HashMap<String,String> not_data=new HashMap<>();
                                         not_data.put("From",mCurrent_user.getUid());
                                         not_data.put("Type","Request");

                                         mNotificationDatabase.child(user_id).push().setValue(not_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                             @Override
                                             public void onSuccess(Void aVoid) {
                                                 mCurrent_state="FRIENDS";
                                                 mProfileSendReqBtn.setText("Cencel Request");

                                                 mDeclineBtn.setVisibility(View.INVISIBLE);
                                                 mDeclineBtn.setEnabled(false);
                                                 Toast.makeText(ProfileActivity.this,"Request sent",Toast.LENGTH_LONG).show();

                                             }
                                         });

                                     }

                                 }
                             });



                         }
                         else {
                             Toast.makeText(ProfileActivity.this,"Error Try Again",Toast.LENGTH_LONG).show();
                         }
                         mProfileSendReqBtn.setEnabled(true);
                     }
                 });
             }*/

            if(mCurrent_state.equals("REQUEST_SENT")){
                mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state="NOT_FRIENDS";
                                mProfileSendReqBtn.setText("Send Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }
                        });



                    }
                });

            }
            if(mCurrent_state.equals("REQUEST_RECEIVED")){

                Toast.makeText(ProfileActivity.this,"I am calling",Toast.LENGTH_LONG);

                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                Map friendsMap = new HashMap();
                friendsMap.put("FRIENDS/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                friendsMap.put("FRIENDS/" + user_id + "/"  + mCurrent_user.getUid() + "/date", currentDate);


                friendsMap.put("FRIEND_REQUEST/" + mCurrent_user.getUid() + "/" + user_id, null);
                friendsMap.put("FRIEND_REQUEST/" + user_id + "/" + mCurrent_user.getUid(), null);


                mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        if(databaseError == null){

                            mProfileSendReqBtn.setEnabled(true);
                            mCurrent_state = "FRIENDS";
                            mProfileSendReqBtn.setText("Unfriend");

                            mDeclineBtn.setVisibility(View.INVISIBLE);
                            mDeclineBtn.setEnabled(false);

                        } else {

                            String error = databaseError.getMessage();

                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                        }

                    }
                });

            }
            /**if(mCurrent_state.equals("REQUEST_RECEIVED")){
                final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
                mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProfileSendReqBtn.setEnabled(true);
                                                mCurrent_state="FRIENDS";
                                                mProfileSendReqBtn.setText("UnFriend");

                                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                                mDeclineBtn.setEnabled(false);

                                            }
                                        });



                                    }
                                });

                            }
                        });
                    }
                });

            }*/



            if(mCurrent_state.equals("FRIENDS")){

                Map unfriendMap = new HashMap();
                unfriendMap.put("FRIENDS/" + mCurrent_user.getUid() + "/" + user_id, null);
                unfriendMap.put("FRIENDS/" + user_id + "/" + mCurrent_user.getUid(), null);

                mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        if(databaseError == null){

                            mCurrent_state = "NOT_FRIENDS";
                            mProfileSendReqBtn.setText("Send Friend Request");

                            mDeclineBtn.setVisibility(View.INVISIBLE);
                            mDeclineBtn.setEnabled(false);

                        } else {

                            String error = databaseError.getMessage();

                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                        }

                        mProfileSendReqBtn.setEnabled(true);

                    }
                });

            }

        }
    });



    }
}
