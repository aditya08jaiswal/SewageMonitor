package com.vysiontech.sewagemonitor;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class MapsActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mRef;
    double latitude, longitude;
    private double wlevel;
    private UserActivity user;
    private int critical = 0, informative = 0, normal = 0,ground=0;
    private TextView string1, string2, string3,string4;
    private int flag = 0;
    private DrawerLayout drawer;
    private Toolbar mtoolbar;
    private String zone_name;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mtoolbar = findViewById(R.id.map_appbar);

        mtoolbar.setTitle("Home");
        drawer = findViewById(R.id.drawer_layout);
        zone_name=getIntent().getStringExtra("zone_name");
        bundle = new Bundle();
        bundle.putString("zone_name",zone_name);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                mRef = FirebaseDatabase.getInstance().getReference().child("token").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mRef.child("Id").setValue(newToken);
            }
        });

        string1 = findViewById(R.id.textView3);
        string2 = findViewById(R.id.textView4);
        string3 = findViewById(R.id.textView5);
        string4 = findViewById(R.id.textView6);


        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,mtoolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            home Home=new home();
            Home.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,Home).commit();
            navigationView.setCheckedItem(R.id.nav_Home);
        }
        monitor();
    }

    public void monitor() {
        user = new UserActivity();

        mRef = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(zone_name);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                normal = 0;
                informative = 0;
                critical = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    user = ds.getValue(UserActivity.class);
                    latitude = user.getLatitude();
                    longitude = user.getLongitude();
                    wlevel = user.getWlevel();

                    if (wlevel == 1)
                        normal++;

                    else if (wlevel == 2)
                        informative++;

                    else if (wlevel == 3) {
                        critical++;
                    }
                    else {
                        ground++;
                    }


                }
                string1.setText("Sewers in normal condition: " + normal);
                string2.setText("Sewers in informative condition: " + informative);
                string3.setText("Sewers in critical condition: " + critical);
                string4.setText("Sewers in ground condition: " + ground);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (flag == 1) {
            Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
            startActivity(intent);
            MapsActivity.this.finish();
            flag = 0;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                mtoolbar.setTitle("Home");
                home Home=new home();
                Home.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Home).commit();
                flag = 2;
                break;
            case R.id.nav_critical:
                Toast.makeText(this, "Critical level list", Toast.LENGTH_SHORT).show();
                mtoolbar.setTitle("Critical Sewers");
                critical Critical=new critical();
                Critical.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Critical).commit();
                flag = 1;
                break;
            case R.id.nav_informative:
                Toast.makeText(this, "Informative level list", Toast.LENGTH_SHORT).show();
                mtoolbar.setTitle("Informative Sewers");
                informative Informative=new informative();
                Informative.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Informative).commit();
                flag = 1;
                break;
            case R.id.nav_normal:
                Toast.makeText(this, "Normal level list", Toast.LENGTH_SHORT).show();
                mtoolbar.setTitle("Normal Sewers");
                normal Normal=new normal();
                Normal.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Normal).commit();
                flag = 1;
                break;
            case R.id.nav_statistics:
                Toast.makeText(this, "Statistics", Toast.LENGTH_SHORT).show();
                mtoolbar.setTitle("Statistics");
                statistics Statistics=new statistics();
                Statistics.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Statistics).commit();
                flag = 1;
                break;
            case R.id.nav_ground:
                Toast.makeText(this, "Ground", Toast.LENGTH_SHORT).show();
                mtoolbar.setTitle("Ground Level Sewers");
                ground Ground=new ground();
                Ground.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Ground).commit();
                flag = 1;
                break;
            case R.id.nav_status:
                Toast.makeText(this, "Battery Status", Toast.LENGTH_SHORT).show();
                mtoolbar.setTitle("Battery Status");
                battery_list Battery_list=new battery_list();
                Battery_list.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Battery_list).commit();
                flag = 1;
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

