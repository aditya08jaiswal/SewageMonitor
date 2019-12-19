package com.vysiontech.sewagemonitor;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vysiontech.sewagemonitor.Helper.LocaleHelper;

import java.util.ArrayList;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ListView mListView;
    private ArrayList<String> arrayList=new ArrayList<>();
    private DatabaseReference mDbRef;
    private FirebaseUser mAuthUser;
    private Toolbar mToolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"hi"));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=findViewById(R.id.main_appbar);
        setSupportActionBar(mToolbar);


        Paper.init(this);

        String language=Paper.book().read("language");
        if(language==null)
            Paper.book().write("language","hi");
        UpdateTitle((String)Paper.book().read("language"),"title_selectZone");


        mListView=findViewById(R.id.zone_list);
        mAuth=FirebaseAuth.getInstance();

        mAuthUser=mAuth.getCurrentUser();
       if(mAuthUser!=null){
        String uid=mAuthUser.getUid();
        Log.d("current uid",uid);


        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,arrayList);
        mListView.setAdapter(arrayAdapter);


        mDbRef= FirebaseDatabase.getInstance().getReference().child(uid);
        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String update_zone_name="";

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    String zone_name=ds.getKey();
                    switch (zone_name){
                        case "Ranikhet":
                            if(Paper.book().read("language").equals("hi")){
                                update_zone_name=UpdateListView((String)Paper.book().read("language"),"Ranikhet");
                            }
                            else
                                update_zone_name=UpdateListView((String)Paper.book().read("language"),"Ranikhet");
                            break;
                        case "Sardarpura":
                            if(Paper.book().read("language").equals("hi")){
                                update_zone_name=UpdateListView((String)Paper.book().read("language"),"Sardarpura");
                            }
                            else
                                update_zone_name=UpdateListView((String)Paper.book().read("language"),"Sardarpura");
                            break;
                    }
                    arrayList.add(update_zone_name);
                    arrayAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

             mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     int counter=0;
                   for(DataSnapshot ds:dataSnapshot.getChildren()){
                       if(position==counter)
                       {
                           String zone_name=ds.getKey();
                           Intent toFragment=new Intent(MainActivity.this,MapsActivity.class);
                           toFragment.putExtra("zone_name",zone_name);
                           Log.d("zone_name",zone_name);
                           startActivity(toFragment);
                           finish();
                           break;

                       }
                       else {
                           counter++;
                       }
                   }

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
            }
        });

       }

    }

    private String UpdateListView(String language, String key) {
        String updated_zone_name="";
        Context context=LocaleHelper.setLocale(this,language);
        Resources resources=context.getResources();
        switch(key){
            case "Ranikhet":
                updated_zone_name=resources.getString(R.string.Ranikhet);
                break;
            case "Sardarpura":
                updated_zone_name=resources.getString(R.string.Sardarpura);
                break;
        }

        return updated_zone_name;
    }

    private void UpdateTitle(String language,String key) {
        Context context=LocaleHelper.setLocale(this,language);
        Resources resources=context.getResources();
        switch(key){
            case "title_selectZone":
            getSupportActionBar().setTitle(resources.getString(R.string.title_selectZone));
            break;
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null)
        {
            startActivity(new Intent(MainActivity.this,ChooseCityActivity.class));
            finish();
        }

    }


}
