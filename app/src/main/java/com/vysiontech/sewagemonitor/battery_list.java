package com.vysiontech.sewagemonitor;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vysiontech.sewagemonitor.UserActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class battery_list extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private UserActivity user;
    private double battery_percentage;
    private ListView listView;
    private ArrayList<String> arraylist= new ArrayList<>( );
    private String device_id;
    //Toolbar toolbar;
    Context context;
    private String zone_name;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle=getArguments();
        zone_name=bundle.getString("zone_name");

        return inflater.inflate(R.layout.fragment_battery_list,container,false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //toolbar=view.findViewById(R.id.toolbar);
        //toolbar.setTitle("Critical Sewers");



        super.onViewStateRestored(savedInstanceState);
        user=new UserActivity();
        database= FirebaseDatabase.getInstance();
        mRef=database.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(zone_name);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, arraylist);
        listView = view.findViewById(R.id.listview);
        listView.setAdapter(arrayAdapter);


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arraylist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    user=ds.getValue(UserActivity.class);
                    battery_percentage=user.getBattery();
                    device_id=ds.getKey();

                    arraylist.add(device_id+"     "+"Battery Percentage:  "+battery_percentage+"%");
                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putInt("key",position);
                bundle.putString("zone_name",zone_name);
                FragmentTransaction fr=getFragmentManager().beginTransaction();

                address_list address=new address_list();
                address.setArguments(bundle);

                fr.replace(R.id.fragment_container,address);
                fr.commit();

            }
        });
    }
}
