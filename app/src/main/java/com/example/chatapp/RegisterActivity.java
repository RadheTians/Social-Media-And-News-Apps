package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button regbtn;
    private EditText regname;
    private EditText regemail;
    private EditText regpass;
    private String userName;
    private String userEmail;
    private String userPass;
    private FirebaseAuth mAuth;
    private Toolbar regToolbar;
    private ProgressDialog regPro;

    private DatabaseReference myRef;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regPro=new ProgressDialog(this);
        regname=(EditText) findViewById(R.id.regname);
        regemail=(EditText) findViewById(R.id.regemail);
        regpass=(EditText) findViewById(R.id.regpass);
        regbtn=(Button) findViewById(R.id.regbtn);
        regToolbar=(Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(regToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName=regname.getText().toString();
                userEmail=regemail.getText().toString();
                userPass=regpass.getText().toString();

                if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPass)) {
                   regPro.setTitle("Registering You");
                   regPro.setMessage("Wait for While");
                   regPro.setCanceledOnTouchOutside(false);
                   regPro.show();


                    register_user(userName, userEmail, userPass);
                }
                else {
                    Toast.makeText(RegisterActivity.this,"Check Input Fields",Toast.LENGTH_LONG).show();
                }




            }
        });

    }
    private void register_user(final String Name, String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            String url=user.getUid();
                            String token= FirebaseInstanceId.getInstance().getToken();

                            myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(url);

                            Map< String,String> mapUser=new HashMap<>();
                            mapUser.put("name",Name);
                            mapUser.put("status","default");
                            mapUser.put("image","dummayImage");
                            mapUser.put("thambimage","deafult");
                            mapUser.put("device_token",token);
                            myRef.setValue(mapUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        regPro.dismiss();
                                        Intent Mainintent =new Intent(RegisterActivity.this,MainActivity.class);
                                        Mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(Mainintent);
                                        finish();
                                        Toast.makeText(RegisterActivity.this,"Login Now",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            firebaseFirestore = FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("Users").document(url).set(mapUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                       /** regPro.dismiss();
                                        Intent Mainintent =new Intent(RegisterActivity.this,MainActivity.class);
                                        Mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(Mainintent);
                                        finish();*/
                                        Toast.makeText(RegisterActivity.this,"Login Now with storage",Toast.LENGTH_LONG).show();
                                    }

                                }
                            });




                        }
                        else {
                            regPro.hide();

                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}
