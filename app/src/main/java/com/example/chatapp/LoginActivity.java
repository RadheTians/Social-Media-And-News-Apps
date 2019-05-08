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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Button loginbtn;
    private EditText loginEmail;
    private EditText loginpass;
    private String userEmail;
    private String userPass;
    private Toolbar loginToolbar;
    private ProgressDialog loginPro;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        loginEmail=(EditText) findViewById(R.id.loginemail);
        loginpass=(EditText) findViewById(R.id.loginpass);
        loginbtn=(Button) findViewById(R.id.loginbtn);
        loginToolbar=(Toolbar) findViewById(R.id.login_toolbar);
        loginPro=new ProgressDialog(this);
        setSupportActionBar(loginToolbar);
        getSupportActionBar().setTitle("Login Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail=loginEmail.getText().toString();
                userPass=loginpass.getText().toString();
                if(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPass)){
                    loginPro.setTitle("login You");
                    loginPro.setMessage("Wait for While");
                    loginPro.setCanceledOnTouchOutside(false);
                    loginPro.show();

                    userLogin(userEmail,userPass);
                }
            }
        });
    }


    private void userLogin(String email,String password){

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginPro.dismiss();

                    String current_user=mAuth.getCurrentUser().getUid();
                    String token= FirebaseInstanceId.getInstance().getToken();
                    mDatabase.child(current_user).child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent Mainintent = new Intent(LoginActivity.this, MainActivity.class);
                            Mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Mainintent);
                            finish();
                            Toast.makeText(LoginActivity.this, "Login Now", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    loginPro.hide();


                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();                }
            }
        });
    }
}
