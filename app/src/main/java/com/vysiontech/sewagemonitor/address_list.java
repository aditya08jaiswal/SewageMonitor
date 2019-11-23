package com.vysiontech.sewagemonitor;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class address_list extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private UserActivity user;
    private double wlevel,latitude,longitude;
    private ListView listView;
    private ArrayList<String> arraylist= new ArrayList<>( );
    private int position,counter=0;
    Context context;
    private TextView text1,text2;
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
        position=bundle.getInt("key");

        zone_name=bundle.getString("zone_name");


        return inflater.inflate(R.layout.fragment_address_list,container,false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text1=view.findViewById(R.id.addresstext);
        text2=view.findViewById(R.id.leveltext);

        user=new UserActivity();
        database= FirebaseDatabase.getInstance();
        mRef=database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(zone_name);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                counter=0;
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    user = ds.getValue(UserActivity.class);
                    wlevel = user.getWlevel();
                    latitude = user.getLatitude();
                    longitude = user.getLongitude();

                    if(counter==position){
                        String city = "";
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            Log.d("Mylo", "Complete address:" + addresses.toString());
                            Log.d("Mylo", "address:" + address);
                            //addresscom.setText("Destination Address:\n"+address+"\n"+"Distance= "+(int)smallest+"m");
                            text1.setText(address);
                            if(wlevel==0)
                                text2.setText("At Ground Level");
                            else if(wlevel==1)
                                text2.setText("At Normal Level");
                            else if(wlevel==2)
                                text2.setText("At Informative Level");
                            else
                                text2.setText("At Critical Level");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                    counter++;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

