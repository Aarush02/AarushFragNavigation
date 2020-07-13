package com.example.aarushfragnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener , View.OnClickListener {
    private TextView textViewProfileName, textViewProfileEmail, textViewEditProfile;
    private ImageView imageViewProfilePicture;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadBottomFragment(new BottomHomeFrag());
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        textViewProfileName = headerView.findViewById(R.id.text_view_name_profile);
        textViewProfileEmail = headerView.findViewById(R.id.text_view_email_profile);
        textViewEditProfile = headerView.findViewById(R.id.text_view_edit_profile);
        imageViewProfilePicture = headerView.findViewById(R.id.image_view_profile);



    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        if (id == R.id.nav_item_wish_list) {
            fragment = new NavWishlistFrag();
        } else if (id == R.id.nav_item_notification_setting) {
            fragment = new NavNotificationSettingFrag();
        } else if (id == R.id.nav_item_privacy_policy) {
            fragment = new NavPrivacyPolicyFrag();
        } else if (id == R.id.nav_item_profile_logout) {

        } else if (id == R.id.action_home){
            fragment = new BottomHomeFrag();
        } else if (id == R.id.action_search){
            fragment = new BottomSearchFrag();
        }  else if (id == R.id.action_more){
            fragment = new BottomNearbyFrag();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }
    private boolean loadBottomFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    private void getProfileData() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        String uid=  currentUser.getUid();

        firebaseDatabase.getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String userName = dataSnapshot.child("Username").getValue(String.class);
                    String userEmail = dataSnapshot.child("Email").getValue(String.class);


                    textViewProfileName.setText(userName);
                    textViewProfileEmail.setText(userEmail);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {

    }

}