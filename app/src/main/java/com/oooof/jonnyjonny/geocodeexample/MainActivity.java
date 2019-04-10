package com.oooof.jonnyjonny.geocodeexample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    private final static int ALL_PERMISSIONS_RESULT=101;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST=9000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private long UPDATE_INTERVAL=15000;
    private long FASTEST_INTERVAL=5000;

    TextView lat_lng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lat_lng=findViewById(R.id.lat_lng);
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        createLocationCallback();
    }
    private void createLocationRequest(){
        mLocationRequest=new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

    }
    private void createLocationCallback(){
        mLocationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location:locationResult.getLocations()){
                    if (location!=null){
                        lat_lng.setText(" ");
                        lat_lng.append("Latitude:"+location.getLatitude()+" "+"Longitude:"+location.getLongitude());
                    }
                }
            }
        };
    }
    protected void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,"Enable Permissions",Toast.LENGTH_LONG).show();
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
    }
    public void stopLocationUpdates(){
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()){
            lat_lng.append("Please Install Google Play Services!");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private boolean checkPlayServices(){
        GoogleApiAvailability apiAvailability=GoogleApiAvailability.getInstance();
        int resultCode=apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode!=ConnectionResult.SUCCESS){
            if (apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this,resultCode,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else {
                finish();
                return false;
            }

        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},ALL_PERMISSIONS_RESULT);

        }else {
            //get my  Location
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null){
                        lat_lng.setText(" ");
                        lat_lng.append("Latitude:"+location.getLatitude()+" "+"Longitude:"+location.getLongitude());
                    }


                }
            });
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showMessageOKCancel("Connection Failed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ALL_PERMISSIONS_RESULT:{
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                }else {

                }
                return;
            }
        }

    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",okListener)
                .setNegativeButton("Cancel",null)
                .create().show();

    }
}
