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
import com.vysiontech.sewagemonitor.R;
import com.vysiontech.sewagemonitor.UserActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ground extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private UserActivity user;
    private double wlevel,latitude,longitude;
    private ListView listView;
    private String zone_name;
    private ArrayList<String> arraylist= new ArrayList<>( );

    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ground,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle=getArguments();
        zone_name=bundle.getString("zone_name");


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
                    wlevel=user.getWlevel();
                    latitude=user.getLatitude();
                    longitude=user.getLongitude();


                    if(wlevel==0)
                    {
                        String city="";
                        Geocoder geocoder=new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> addresses=geocoder.getFromLocation(latitude,longitude,1);
                            String address=addresses.get(0).getAddressLine(0);
                            city=addresses.get(0).getLocality();
                            Log.d("Mylo","Complete address:"+ addresses.toString());
                            Log.d("Mylo","address:"+ address);
                            //addresscom.setText("Destination Address:\n"+address+"\n"+"Distance= "+(int)smallest+"m");

                            arraylist.add(address);
                            arrayAdapter.notifyDataSetChanged();



                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
