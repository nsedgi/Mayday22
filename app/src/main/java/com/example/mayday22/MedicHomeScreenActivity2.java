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
import com.google.android.gms.maps.MapsInitializer;
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

public class MedicHomeScreenActivity2 extends FragmentActivity implements OnMapReadyCallback {
    TextView nameText, idText, organizationText, linkText;
    double lat, lng;
    String name, id, organization, url, password;
    DatabaseReference mDatabase, avDatabase;
    LocationManager locationManager;
    GoogleMap googleMap2;
    MedicTrack medicTrack;
    long maxId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_home_screen2);
        nameText= findViewById(R.id.name);
        idText=findViewById(R.id.id);
        organizationText=findViewById(R.id.organization);
        linkText=findViewById(R.id.link);
        Intent intent = getIntent();
        id = intent.getStringExtra("lastId");
        password = intent.getStringExtra("lastPassword");
        medicTrack = new MedicTrack();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("medics").child(id).child(password);
        avDatabase = FirebaseDatabase.getInstance().getReference().child("medic locations");
        MapsInitializer.initialize(this);
        idText.setText(id);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getMedicInfo();
        method();
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

    }
    public void getMedicInfo(){

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("name").getValue().toString();
                organization=dataSnapshot.child("organization").getValue().toString();
                url=dataSnapshot.child("idUrl").getValue().toString();
                nameText.setText(name);
                organizationText.setText(organization);
                linkText.setText(url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    LatLng userLatLong = new LatLng(lat, lng);
                    mDatabase.child("latitude").setValue(lat);
                    mDatabase.child("longitude").setValue(lng);
                    setAvailableMedic();

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

    private void setAvailableMedic() {
        avDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxId = dataSnapshot.getChildrenCount();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        maxId++;
        medicTrack.setLat(lat);
        medicTrack.setLng(lng);
        medicTrack.setId(id);
        medicTrack.setPassword(password);
        avDatabase.child(String.valueOf(maxId+1)).setValue(medicTrack);
    }
}