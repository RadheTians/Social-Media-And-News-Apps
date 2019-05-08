package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UniqueActivity extends AppCompatActivity {


    private BottomNavigationView mainbottomNav;
    private FloatingActionButton addPostBtn;
    private Toolbar userPostTool;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unique);


        mainbottomNav = findViewById(R.id.mainBottomNav);
        addPostBtn=findViewById(R.id.add_post_btn);


        // FRAGMENTS
        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();
        replaceFragment(homeFragment);
        userPostTool=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(userPostTool);
        getSupportActionBar().setTitle("All Users Post");


        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                switch (item.getItemId()) {

                    case R.id.bottom_action_home:

                        replaceFragment(homeFragment);
                        return true;

                    case R.id.bottom_action_account:

                        //replaceFragment(accountFragment);
                        Intent accountIntent= new Intent(UniqueActivity.this,SettingsActivity.class);
                        startActivity(accountIntent);
                        return true;

                    case R.id.bottom_action_notif:

                        replaceFragment(notificationFragment);
                        return true;

                    default:
                        return false;


                }

            }
        });

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newPostIntent = new Intent(UniqueActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);

            }
        });




    }


    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }

}
