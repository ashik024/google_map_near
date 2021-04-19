package com.example.globe_map2;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;

    GoogleApiClient googleApiClient;
    Location lastlocation;
    LocationRequest locationRequest;

    SupportMapFragment mapFragment;

    Marker mCurrLocationMarker;

    Marker marker;

    Button button;

    RelativeLayout relativeLayout;
    TextView close;


    ArrayList<LatLng> arrayListTmp= new ArrayList<LatLng>();
    ArrayList<LatLng> arrayList= new ArrayList<LatLng>();
    LatLng Depo1 = new LatLng(23.83151100010753, 90.41675226718466);
    LatLng Depo2 = new LatLng(23.829724827167425, 90.42042152905809);
    LatLng Depo3 = new LatLng(23.83230593729829, 90.41639821560038);
    LatLng Depo4 = new LatLng(23.8289396884234, 90.41646258861572);
    LatLng Depo5 = new LatLng(23.828929874159027, 90.42139785312384);
    LatLng Depo6 = new LatLng(23.7925, 90.4078);
    LatLng Depo7 = new LatLng(23.8282, 90.3890);
    ArrayList<String> title= new ArrayList<String>();

    double depo_lat;
    double depo_lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

         button= findViewById(R.id.find_depo);

        relativeLayout= findViewById(R.id.relative);
        close= findViewById(R.id.close);
        mapFragment.getMapAsync(this);
        arrayList.add(Depo1);
        arrayList.add(Depo2);
        arrayList.add(Depo3);
        arrayList.add(Depo4);
        arrayList.add(Depo5);
        arrayList.add(Depo6);
        arrayList.add(Depo7);


        title.add("Depo1");
        title.add("Depo2");
        title.add("Depo3");
        title.add("Depo4");
        title.add("Depo5");
        title.add("Ashik Sir");
        title.add("Shohag Sir");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mMap.clear();
                Location userLocation= new Location("");
                userLocation.setLatitude(lastlocation.getLatitude());
                userLocation.setLongitude(lastlocation.getLongitude());
                for(int i=0;i<arrayList.size();i++){
                    Location depoLocation= new Location("");
                    depoLocation.setLatitude(arrayList.get(i).latitude);
                    depoLocation.setLongitude(arrayList.get(i).longitude);
                    CalculationByDistance(userLocation,depoLocation);



                }
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            }
        });

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("Mynotifications","Mynotifications", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }




    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setAllMarkers();





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildgoogleapi();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildgoogleapi();
            mMap.setMyLocationEnabled(true);
        }

    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastlocation= location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");

        mCurrLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }

    final int Location_Request_Code=1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildgoogleapi();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MapsActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }


    }

//    private void finddistance() {
//
//        Location userLocation= new Location("");
//        userLocation.setLatitude(lastlocation.getLatitude());
//        userLocation.setLongitude(lastlocation.getLongitude());
//        Log.i("user", String.valueOf(lastlocation.getLatitude()));
//        Log.i("user", String.valueOf(lastlocation.getLongitude()));
//
//
//        Location depoLocation= new Location("");
//        depoLocation.setLatitude(Depo6.latitude);
//        depoLocation.setLongitude(Depo6.longitude);
//
//        Log.i("depo", String.valueOf(Depo6.latitude));
//        Log.i("depo", String.valueOf(Depo6.longitude));
//
//       // CalculationByDistance(userLocation,depoLocation);
//
//    }

    protected synchronized void buildgoogleapi() {
        googleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();


    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    public void setAllMarkers(){


        for (int i=0; i<arrayList.size();i++){

            for (int j =0;j<title.size();j++){

                marker=  mMap.addMarker(new MarkerOptions().position(arrayList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.officebuilding)).title(String.valueOf(title.get(i))));



            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                relativeLayout.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        relativeLayout.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.VISIBLE);
                    }
                });
                return false;

            }
        });
    }
//    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_account_balance_24);
//        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }

    public double CalculationByDistance(Location StartP, Location EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.getLatitude();
        double lat2 = EndP.getLatitude();
        double lon1 = StartP.getLongitude();
        double lon2 = EndP.getLongitude();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.e("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        if(kmInDec<=2){
            arrayListTmp.add(new LatLng(lat2,lon2));

            for (int i=0; i<arrayListTmp.size();i++){

                for (int j =0;j<title.size();j++){

                    marker=  mMap.addMarker(new MarkerOptions().position(arrayListTmp.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.officebuilding)).title(String.valueOf(title.get(i))));
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayListTmp.get(i)));
            }
        }


        return Radius * c;
    }
}