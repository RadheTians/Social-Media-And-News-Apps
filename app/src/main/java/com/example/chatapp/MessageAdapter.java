package com.example.chatapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    public Context context;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        mAuth=FirebaseAuth.getInstance();
        context = parent.getContext();
        return new MessageViewHolder(v);

    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }



    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {


        String mCurrent_user=mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(i);
        viewHolder.messageText.setText(c.getMessage());
        String from_user=c.getFrom();
        String message_type = c.getType();
        try {
            long millisecond = c.getTime();
            String dateString= GetTimeAgo.getTimeAgo(millisecond,context);
            viewHolder.dispalyTime.setText(dateString);
        } catch (Exception e) {

            //Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }



        /**mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thambimage").getValue().toString();

                viewHolder.displayName.setText(name);

                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage()).placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );



         if(from_user.equals(mCurrent_user)){
             //viewHolder.messageText.setBackgroundColor(Color.WHITE);
             //viewHolder.messageText.setTextColor(Color.BLACK);
             //viewHolder.relativeLayout.setBackgroundColor(Color.BLACK);
             params.setMargins(150, 10, 15, 10);
             viewHolder.relativeLayout.setLayoutParams(params);

         }
         else {
             //viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
             //viewHolder.messageText.setTextColor(Color.BLACK);
             //viewHolder.relativeLayout.setBackgroundColor(Color.RED);
             params.setMargins(15, 10, 120, 10);
             viewHolder.relativeLayout.setLayoutParams(params);
         }



    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public ImageView messageImage;
        public TextView dispalyTime;
        public RelativeLayout relativeLayout;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            dispalyTime = (TextView) view.findViewById(R.id.time_text_layout);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.message_layout);

        }
    }

}