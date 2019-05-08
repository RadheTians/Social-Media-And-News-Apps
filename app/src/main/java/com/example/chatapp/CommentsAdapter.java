package com.example.chatapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    public Context context;
    public List<CommentPost> comment_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    public CommentsAdapter(List<CommentPost> comment_list){
        this.comment_list=comment_list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commemt_single_layout, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String currentUserId=firebaseAuth.getCurrentUser().getUid();
        String text_name = comment_list.get(position).getName();
        holder.setName(text_name);
        String text_comment = comment_list.get(position).getComment();
        holder.setComment(text_comment);
        try {
            long millisecond = comment_list.get(position).getTimestamp().getTime();
            String dateString= GetTimeAgo.getTimeAgo(millisecond,context);
            //String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            //Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView nameTextView;
        private TextView commentTextView;
        private TextView timeTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setName(String name) {

            nameTextView = mView.findViewById(R.id.comment_name_layout);
            nameTextView.setText(name);

        }
        public void setComment(String comment) {

            commentTextView = mView.findViewById(R.id.comment_text_layout);
            commentTextView.setText(comment);

        }

        public void setTime(String date) {

            timeTextView = mView.findViewById(R.id.comment_time_layout);
            timeTextView.setText(date);

        }

    }
}
