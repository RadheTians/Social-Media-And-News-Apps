package com.example.chatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView comment_list_view;
    private List<CommentPost> comment_list;
    private CommentsAdapter commentAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;


    private Toolbar commentTool;
    private EditText comment_Edit_Text;
    private ImageView comment_Image_btn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private  String blogPostId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        commentTool=(Toolbar) findViewById(R.id.comment_app_bar);
        setSupportActionBar(commentTool);
        getSupportActionBar().setTitle("Post Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        comment_list = new ArrayList<>();
        comment_list_view = findViewById(R.id.comments_list);
        commentAdapter = new CommentsAdapter(comment_list);

        comment_list_view.setLayoutManager(new LinearLayoutManager(this));
        comment_list_view.setAdapter(commentAdapter);

        comment_Edit_Text=(EditText)findViewById(R.id.comment_message_view);
        comment_Image_btn=(ImageView)findViewById(R.id.comment_send_btn);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        blogPostId = getIntent().getStringExtra("blog_post_id");

        try {

            if (firebaseAuth.getCurrentUser() != null) {

                comment_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                        if (reachedBottom) {
                            loadMorePost();

                        }

                    }
                });
                Query firstQuery = firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);

                firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            if (isFirstPageFirstLoad) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                                comment_list.clear();

                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {


                                    CommentPost blogPost = doc.getDocument().toObject(CommentPost.class);


                                    if (isFirstPageFirstLoad) {

                                        comment_list.add(blogPost);

                                    } else {

                                        comment_list.add(0, blogPost);

                                    }

                                    commentAdapter.notifyDataSetChanged();
                                }


                            }
                            isFirstPageFirstLoad = false;
                        }


                    }
                });
                comment_Image_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String comment_Text = comment_Edit_Text.getText().toString();
                        comment_Edit_Text.setText("");
                        firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    String userName = task.getResult().getString("name");
                                    String userImage = task.getResult().getString("image");
                                    Map<String, Object> commentsMap = new HashMap<>();
                                    commentsMap.put("comment", comment_Text);
                                    commentsMap.put("name", userName);
                                    commentsMap.put("timestamp", FieldValue.serverTimestamp());
                                    firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").document(random()).set(commentsMap);

                                } else {

                                    //Firebase Exception

                                }

                            }
                        });


                    }
                });
            }
        }catch (Exception e){}

    }
    public void loadMorePost(){

        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Posts/" + blogPostId + "/Comments")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(5);

            nextQuery.addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                CommentPost blogPost = doc.getDocument().toObject(CommentPost.class);
                                comment_list.add(blogPost);

                                commentAdapter.notifyDataSetChanged();
                            }

                        }
                    }

                }
            });

        }

    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
