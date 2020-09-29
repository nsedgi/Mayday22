package com.example.mayday22;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.List;

public class UserDistressActivity extends FragmentActivity implements OnMapReadyCallback {
    LocationManager locationManager;
    GoogleMap googleMap2;
    private DatabaseReference mDatabase, avDatabase, medDatabase;
    private long maxId;
    double lat, lng;
    double closestLat, closestLng, closestSum, closestCounter=19000.0; //set impossible coordinates.
    private String medicId, medicPassword;
    int closestFlag, i;
    TextView waitTitle;
    String medInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_distress);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("lastId");
        final String password = intent.getStringExtra("lastPassword");
        String medInfo = intent.getStringExtra("lastMedInfo");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id).child(password);
        avDatabase = FirebaseDatabase.getInstance().getReference().child("medic locations");
        medDatabase = FirebaseDatabase.getInstance().getReference().child("medics");
        waitTitle = findViewById(R.id.waitTitle);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setLocation();

    }

    private void CalculateClosestMedic() {
//add another database for available medics.
        avDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //set max available medics (medics in available databased are numerated).
                maxId = dataSnapshot.getChildrenCount();
                System.out.println("maxId:" + maxId);
                closestSum=lat+lng;
                double calculate=12000;
                for(i = 1; i<=maxId; ++i){

                    closestLat = (double) dataSnapshot.child(String.valueOf(i)).child("lat").getValue();
                    closestLng = (double) dataSnapshot.child(String.valueOf(i)).child("lng").getValue();
                    //add the coordinates to find the smallest latch between current location and medic location.
                    closestCounter = closestLat+closestLng;
                    if(closestCounter+closestSum<calculate){
                        calculate = closestCounter+closestSum;
                        if(dataSnapshot.child(String.valueOf(i)).child("track").getValue()!="refused" ||
                                dataSnapshot.child(String.valueOf(i)).child("track").getValue() != "ready")
                        closestFlag=i;}
                    System.out.println("closest courdinates:" + closestFlag);

                }
                medicId = (String) dataSnapshot.child(String.valueOf(closestFlag)).child("id").getValue();
                medicPassword = (String) dataSnapshot.child(String.valueOf(closestFlag)).child("password").getValue();

                System.out.println(medicId + medicPassword);
                sendRequest();
                if(dataSnapshot.child(String.valueOf(closestFlag)).child("password").child("tripleshake1").getValue()=="refused") CalculateClosestMedic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendRequest() {
        medDatabase.child(medicId).child(medicPassword).child("tripleshake1").setValue("true");
        medDatabase.child(medicId).child(medicPassword).child("userLatitude").setValue(lat);
        medDatabase.child(medicId).child(medicPassword).child("userLongitude").setValue(lng);
        medDatabase.child(medicId).child(medicPassword).child("userInfo").setValue(medInfo);
        waitTitle.setText("חובש בדרך אלייך!");
    }


    private void setLocation() {
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

                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    LatLng userLatLong = new LatLng(lat, lng);
                    mDatabase.child("latitude").setValue(lat);
                    mDatabase.child("longitude").setValue(lng);
                    System.out.println("latitude:" + lat + "longitude: " + lng);
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

                    CalculateClosestMedic();



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


   @Override
   public void onMapReady(GoogleMap googleMap) {
        this.googleMap2 = googleMap;
   }

}
