package com.vysiontech.sewagemonitor;


import
        android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class home extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private UserActivity user;
    private double wlevel, latitude, longitude;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation, mylocation;
    private Marker currentLoactionMarker;
    public static final int REQUEST_LOCATION_CODE_ = 99;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private String zone_name;

    Context context;

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

        Log.d("zone_name1",zone_name);
        Log.d("uidcurrent",FirebaseAuth.getInstance().getCurrentUser().getUid());

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fr = getFragmentManager();
            FragmentTransaction ft = fr.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


        return inflater.inflate(R.layout.fragment_home, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE_:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (client == null) {
                            getuplocation();
                            buildGoogleApiClient();
                        }
                    }
                    mMap.setMyLocationEnabled(true);
                } else //permission denied
                {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_LONG).show();
                }
                //return;

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        if (currentLoactionMarker != null) {
            currentLoactionMarker.remove();
        }


        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
        monitoring();


    }

    public void monitoring() {

        user = new UserActivity();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(zone_name);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    user = ds.getValue(UserActivity.class);
                    latitude = user.getLatitude();
                    longitude = user.getLongitude();
                    wlevel = user.getWlevel();

                    if (wlevel == 1) {
                        MarkerOptions markerOptions1 = new MarkerOptions();
                        markerOptions1.position(new LatLng(latitude, longitude));
                        markerOptions1.title(ds.getKey());
                        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        markerOptions1.snippet("Normal level");
                        mMap.addMarker(markerOptions1);


                    } else if (wlevel == 2) {
                        MarkerOptions markerOptions2 = new MarkerOptions();
                        markerOptions2.position(new LatLng(latitude, longitude));
                        markerOptions2.title(ds.getKey());
                        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        markerOptions2.snippet("Informative level");
                        mMap.addMarker(markerOptions2);


                    } else if (wlevel == 3) {

                        MarkerOptions markerOptions3 = new MarkerOptions();
                        markerOptions3.position(new LatLng(latitude, longitude));
                        markerOptions3.title(ds.getKey());
                        markerOptions3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        markerOptions3.snippet("Critical level");
                        mMap.addMarker(markerOptions3);


                    } else if (wlevel == 0) {
                        MarkerOptions markerOptions0 = new MarkerOptions();
                        markerOptions0.position(new LatLng(latitude, longitude));
                        markerOptions0.title(ds.getKey());
                        markerOptions0.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        markerOptions0.snippet("Ground level");
                        mMap.addMarker(markerOptions0);
                    }
                    LatLng LatLng = new LatLng(latitude, longitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomBy(9));


                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUplocation();
    }

    private void setUplocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestRuntimePermission();
        } else {
            getuplocation();
        }
    }

    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]
                {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_LOCATION_CODE_);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getuplocation() {
        if (client != null) {
            if (client.isConnected()) {
                int permissionLocation = checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(client);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(client, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(client, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = checkSelfPermission(context,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(client);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getuplocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        getActivity().finish();
                        break;
                }
                break;
        }
    }


}
