package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mainTool;
    private ViewPager viewPager;
    private AdapterSections section;
    private Context context;
    private TabLayout tableLayout;
    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainTool=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mainTool);
        getSupportActionBar().setTitle("RT Chat");


        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            viewPager=(ViewPager) findViewById(R.id.main_tabPager);
            section = new AdapterSections(getSupportFragmentManager());
            viewPager.setAdapter(section);
            tableLayout = (TabLayout) findViewById(R.id.main_tabs);
            tableLayout.setupWithViewPager(viewPager);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
           sendTostart();
        }
        else{
            mUserRef.child("online").setValue(true);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuth.getCurrentUser() != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendTostart(){
        Intent intent=new Intent(MainActivity.this,StartActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.userLogout){
            FirebaseAuth.getInstance().signOut();
            sendTostart();
        }
        if(item.getItemId()==R.id.display_name){
            Intent settingIntent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingIntent);
        }
        if(item.getItemId()==R.id.diplay_users){
            Intent settingIntent=new Intent(MainActivity.this,UsersActivity.class);
            startActivity(settingIntent);
        }
        if(item.getItemId()==R.id.new_post_find){
            Intent settingIntent=new Intent(MainActivity.this,NewsActivity.class);
            startActivity(settingIntent);
        }
        if(item.getItemId()==R.id.all_post_show){
            Intent settingIntent=new Intent(MainActivity.this,UniqueActivity.class);
            startActivity(settingIntent);
        }
        return true;
    }
    public class AdapterSections extends FragmentPagerAdapter {

        public AdapterSections(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0 :
                    ChatsFragment chatsFragment=new ChatsFragment();
                    return chatsFragment;
                case 1 :
                    FriendsFragment friendsFragment=new FriendsFragment();
                    return friendsFragment;
                case 2 :
                    RequestFragment requestFragment=new RequestFragment();
                    return requestFragment;
                case 3 :
                    Intent intent = new Intent(MainActivity.this,UniqueActivity.class);
                    startActivity(intent);
                    RequestFragment accountFragment=new RequestFragment();
                    return accountFragment;

                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        public CharSequence getPageTitle(int page){
            switch (page){
                case 0:
                    return "CHATS";
                case 1:
                    return  "FRIENDS";
                case 2:
                    return "REQUESTS";
                case 3:
                    return "POSTS";
                default:
                    return null;
            }
        }
    }
}
