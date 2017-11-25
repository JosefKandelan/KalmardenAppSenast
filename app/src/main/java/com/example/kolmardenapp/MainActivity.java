package com.example.kolmardenapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String GEOFENCE_ID = "myGeofenceID";

    public static final String Tag = "MainActivity";

    GoogleApiClient googleApiClient = null;

    private Button StrLocMon, StrGeo, StpGeo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        StrLocMon = (Button) findViewById(R.id.StrLocMon);
        StrLocMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrLocMon();
            }
        });


        StrGeo = (Button) findViewById(R.id.StrGeo);
        StrGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrGeo();
            }
        });


        StpGeo = (Button) findViewById(R.id.StpGeo);
        StpGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StpGeo();
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        Log.d(Tag, "Connected to GoogleApiCLient");

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                        Log.d(Tag, "Suspended connection to GoogleApiClient");

                    }
                })

                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Log.d(Tag, "Failed to connect to GoogleApiClient" + connectionResult.getErrorMessage());
                    }
                })

                .build();


        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);


    }

    protected void onResume() {

        Log.d(Tag, "onResume called");

        super.onResume();

        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (response != ConnectionResult.SUCCESS) {

            Log.d(Tag, "Google Play Services not available - show dialog to ask user to download it");

            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();

        } else {

            Log.d(Tag, "Google Play Services is available - no action is required");
        }

    }

    protected void onStart() {

        Log.d(Tag, "onStart called");

        super.onStart();

        googleApiClient.reconnect();

    }

    protected void onStop() {

        Log.d(Tag, "onStop called");

        super.onStop();

        googleApiClient.disconnect();

    }

    private void StrLocMon() {

        Log.d(Tag, "StrLocMon called");

        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {

                public void onLocationChanged(Location location) {

                    Log.d(Tag, "Location update lat/long " + location.getLatitude() + " " + location.getLongitude());
                }

            });


        } catch (SecurityException e) {
            Log.d(Tag, String.format("SecurityException - ", e.getMessage()));

        }


    }

    private void StrGeo() {

        Geofence geofence = new Geofence.Builder()

                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(58.5988752, 16.1636552, 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()

                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence).build();

        Intent intent = new Intent(this, GeofenceService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!googleApiClient.isConnected()) {

            Log.d(Tag, "GoogleApiClient is not conectected");

        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(Tag, "Successfully added geofence");
                            } else {
                                Log.d(Tag, "Failed to add geofence" + status.getStatus());

                            }
                        }
                    });
        }


    }

    private void StpGeo() {

        Log.d(Tag, "StpGeo called");
        ArrayList<String> geofenceIds = new ArrayList<String>();
        geofenceIds.add(GEOFENCE_ID);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceIds);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* handle item selection */
        switch (item.getItemId()) {
            case R.id.menu_experiences:
                Intent intentExperience = new Intent(MainActivity.this, ExperiencesActivity.class);
                this.startActivity(intentExperience);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}
