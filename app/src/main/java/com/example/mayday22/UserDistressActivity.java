package com.example.mayday22;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class UserDistressActivity extends FragmentActivity implements OnMapReadyCallback {
    LocationManager locationManager;
    LocationListener locationListener;
    Location userLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap googleMap2;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_distress);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("lastId");
        final String password = intent.getStringExtra("lastPassword");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id).child(password);

        method();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void method() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat, lng;
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    LatLng userLatLong = new LatLng(lat, lng);
                    mDatabase.child("latitude").setValue(lat);
                    mDatabase.child("longitude").setValue(lng);
                    Geocoder geo = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geo.getFromLocation(lat, lng, 1);
                        String str;
                        str = addressList.get(0).getLocality();
                        googleMap2.addMarker(new MarkerOptions().position(userLatLong).title(str));
                        googleMap2.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, 15.0f));
                        locationManager.removeUpdates(this);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }
    private void fetchLastLocation() {

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    userLocation = location;
                    Toast.makeText(getApplicationContext(),userLocation.getLatitude()+""+userLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(UserDistressActivity.this);
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap2 = googleMap;

  /*      final LatLng userLatLong = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userLatLong).title("אתה נמצא כאן");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(userLatLong));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, 5));
        googleMap.addMarker(markerOptions);
        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);*/

       /* locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation=location;


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };*/
    }

}
