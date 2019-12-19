package com.vysiontech.sewagemonitor;

import android.content.Context;
import android.content.Intent;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.app.AppCompatActivity;
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
import com.vysiontech.sewagemonitor.Helper.LocaleHelper;

import io.paperdb.Paper;


public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"hi"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mtoolbar = findViewById(R.id.map_appbar);
        setSupportActionBar(mtoolbar);
        string1 = findViewById(R.id.textView3);
        string2 = findViewById(R.id.textView4);
        string3 = findViewById(R.id.textView5);
        string4 = findViewById(R.id.textView6);
        zone_name=getIntent().getStringExtra("zone_name");
        bundle = new Bundle();
        bundle.putString("zone_name",zone_name);


        Paper.init(this);

        String language=Paper.book().read("language");
        if(language==null)
            Paper.book().write("language","hi");

        UpdateView((String)Paper.book().read("language"));
        UpdateTitle((String)Paper.book().read("language"),"title_home");

        drawer = findViewById(R.id.drawer_layout);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                mRef = FirebaseDatabase.getInstance().getReference().child("token").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mRef.child(zone_name).child("Id").setValue(newToken);
            }
        });



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
            UpdateTitle((String)Paper.book().read("language"),"title_home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,Home).commit();
            navigationView.setCheckedItem(R.id.nav_Home);
        }else {
            if(savedInstanceState.equals(new statistics()))
            UpdateTitle((String)Paper.book().read("language"),"title_statistics");
            else if(savedInstanceState.equals(new critical()))
                UpdateTitle((String)Paper.book().read("language"),"title_critical");
            else if(savedInstanceState.equals(new normal()))
                UpdateTitle((String)Paper.book().read("language"),"title_normal");
            else if(savedInstanceState.equals(new informative()))
                UpdateTitle((String)Paper.book().read("language"),"title_informative");
            else if(savedInstanceState.equals(new ground()))
                UpdateTitle((String)Paper.book().read("language"),"title_ground");
            else if(savedInstanceState.equals(new home()))
                UpdateTitle((String)Paper.book().read("language"),"title_home");
            else if(savedInstanceState.equals(new battery_list()))
                UpdateTitle((String)Paper.book().read("language"),"title_battery");

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
                ground=0;
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
                UpdateView((String)Paper.book().read("language"));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void UpdateView(String language) {

        Context context=LocaleHelper.setLocale(this,language);
        Resources resources=context.getResources();


        string1.setText(resources.getString(R.string.normal)+"  " + normal);
        string2.setText(resources.getString(R.string.informative)+"  " + informative);
        string3.setText(resources.getString(R.string.critical)+"  " + critical);
        string4.setText(resources.getString(R.string.ground)+"  " + ground);

    }
    private void UpdateTitle(String language,String key) {
        Context context=LocaleHelper.setLocale(this,language);
        Resources resources=context.getResources();
        switch (key){
            case "title_home":
                getSupportActionBar().setTitle(resources.getString(R.string.title_home));
                break;
            case "title_normal":
                mtoolbar.setTitle(resources.getString(R.string.title_normal));
                break;
            case "title_informative":
                mtoolbar.setTitle(resources.getString(R.string.title_informative));
                break;
            case "title_critical":
                mtoolbar.setTitle(resources.getString(R.string.title_critical));
                break;
            case "title_ground":
                mtoolbar.setTitle(resources.getString(R.string.title_ground));
                break;
            case "title_statistics":
                mtoolbar.setTitle(resources.getString(R.string.title_statistics));
                break;
            case "title_battery":
                mtoolbar.setTitle(resources.getString(R.string.title_battery));
                break;
             default:
                 mtoolbar.setTitle(resources.getString(R.string.title_home));
                 break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.language_en){
            recreate();
            Paper.book().write("language","en");
            UpdateView((String)Paper.book().read("language"));
            CharSequence s=mtoolbar.getTitle();
            String title=s.toString().trim();

            switch (title){
                case "होम":
                    UpdateTitle((String)Paper.book().read("language"),"title_home");
                    break;
                case "सामान्य सीवर":
                    UpdateTitle((String)Paper.book().read("language"),"title_normal");
                    break;
                case "जानकारीपूर्ण सीवर":
                    UpdateTitle((String)Paper.book().read("language"),"title_informative");
                    break;
                case "क्रिटिकल सीवर":
                    UpdateTitle((String)Paper.book().read("language"),"title_critical");
                    break;
                case "ग्राउंड सीवर":
                    UpdateTitle((String)Paper.book().read("language"),"title_ground");
                    break;
                case "आंकड़े":
                    UpdateTitle((String)Paper.book().read("language"),"title_statistics");
                    break;
                case "बैटरी की स्थिति":
                    UpdateTitle((String)Paper.book().read("language"),"title_battery");
                    break;

            }


        }
        if(item.getItemId()==R.id.language_hi){
            recreate();
            Paper.book().write("language","hi");
            UpdateView((String)Paper.book().read("language"));

            CharSequence s=mtoolbar.getTitle();
            String title=s.toString().trim();
            switch (title){
                case "Home":
                    UpdateTitle((String)Paper.book().read("language"),"title_home");
                    break;
                case "Normal Sewers":
                    UpdateTitle((String)Paper.book().read("language"),"title_normal");
                    break;
                case "Informative Sewers":
                    UpdateTitle((String)Paper.book().read("language"),"title_informative");
                    break;
                case "Critical Sewers":
                    UpdateTitle((String)Paper.book().read("language"),"title_critical");
                    break;
                case "Ground Sewers":
                    UpdateTitle((String)Paper.book().read("language"),"title_ground");
                    break;
                case "Statistics":
                    UpdateTitle((String)Paper.book().read("language"),"title_statistics");
                    break;
                case "Battery Status":
                    UpdateTitle((String)Paper.book().read("language"),"title_battery");
                    break;
            }


        }
        return true;
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
                UpdateTitle((String)Paper.book().read("language"),"title_home");
                home Home=new home();
                Home.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Home).commit();
                //flag = 2;
                break;
            case R.id.nav_critical:
                Toast.makeText(this, "Critical level list", Toast.LENGTH_SHORT).show();
                UpdateTitle((String)Paper.book().read("language"),"title_critical");
                critical Critical=new critical();
                Critical.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Critical).commit();
                //flag = 1;
                break;
            case R.id.nav_informative:
                Toast.makeText(this, "Informative level list", Toast.LENGTH_SHORT).show();
                UpdateTitle((String)Paper.book().read("language"),"title_informative");
                informative Informative=new informative();
                Informative.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Informative).commit();
                //flag = 1;
                break;
            case R.id.nav_normal:
                Toast.makeText(this, "Normal level list", Toast.LENGTH_SHORT).show();
                UpdateTitle((String)Paper.book().read("language"),"title_normal");
                normal Normal=new normal();
                Normal.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Normal).commit();
                //flag = 1;
                break;
            case R.id.nav_statistics:
                Toast.makeText(this, "Statistics", Toast.LENGTH_SHORT).show();
                UpdateTitle((String)Paper.book().read("language"),"title_statistics");
                statistics Statistics=new statistics();
                Statistics.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Statistics).commit();
                //flag = 1;
                break;
            case R.id.nav_ground:
                Toast.makeText(this, "Ground", Toast.LENGTH_SHORT).show();
                UpdateTitle((String)Paper.book().read("language"),"title_ground");
                ground Ground=new ground();
                Ground.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Ground).commit();
                //flag = 1;
                break;
            case R.id.nav_status:
                Toast.makeText(this, "Battery Status", Toast.LENGTH_SHORT).show();
                UpdateTitle((String)Paper.book().read("language"),"title_battery");
                battery_list Battery_list=new battery_list();
                Battery_list.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Battery_list).commit();
                //flag = 1;
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

