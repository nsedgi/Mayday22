package com.example.mayday22;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
    Button zero;
    double lat, lng, userLat, userLng, medicLat, medicLng;
    String name, id, organization, url, password, userMedInfo;
    DatabaseReference mDatabase, avDatabase;
    LocationManager locationManager;
    GoogleMap googleMap2;
    MedicTrack medicTrack;
    LatLng medicLatLong;
    long maxId=0;
    int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_home_screen2);
        nameText= findViewById(R.id.name);
        idText=findViewById(R.id.id);
        organizationText=findViewById(R.id.organization);
        linkText=findViewById(R.id.link);
        zero=findViewById(R.id.zeroIn);
        Intent intent = getIntent();
        id = intent.getStringExtra("lastId");
        password = intent.getStringExtra("lastPassword");
        medicTrack = new MedicTrack();
        //refer to own info.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("medics").child(id).child(password);
        //refer to "available medics" info.
        avDatabase = FirebaseDatabase.getInstance().getReference().child("medic locations");
        MapsInitializer.initialize(this);
        idText.setText(id);
        getTrackNum();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getMedicInfo();
        method();
        mDatabase.child("tripleshake1").setValue("available");

        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTrackNum();
                setAvailableMedic();
                mDatabase.child("tripleshake1").setValue("available");
            }
        });

    }

    public void getTrackNum() {
        //find all available medics. fill gaps if any.
        avDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(i=0; i<=1+dataSnapshot.getChildrenCount()&&dataSnapshot.child(String.valueOf(i)).exists(); i++)
                maxId = i;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap2 = googleMap;

    }
    public void getMedicInfo(){
// set all of the medic info on screen. add latitude and longitude. open a listener for changes in "tripleShake1".
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("name").getValue().toString();
                organization=dataSnapshot.child("organization").getValue().toString();
                url=dataSnapshot.child("idUrl").getValue().toString();
                nameText.setText(name);
                organizationText.setText(organization);
                linkText.setText(url);
                medicLat= (double) dataSnapshot.child("latitude").getValue();
                medicLng= (double) dataSnapshot.child("longitude").getValue();
                if(dataSnapshot.child("userLatitude").exists())
                userLat = (double) dataSnapshot.child("userLatitude").getValue();
                if(dataSnapshot.child("userLongitude").exists())
                userLng = (double) dataSnapshot.child("userLongitude").getValue();
                String tripleShake1 = String.valueOf(dataSnapshot.child("tripleshake1").getValue());
                if(tripleShake1.equals("true")){
                    ConfirmDialog();
                }
                if(tripleShake1.equals("refused")){
//delete medic from "available medics". also zeroes in faults of several similar id problem.
                    dataSnapshot.child("userLatitude").getRef().removeValue();
                    dataSnapshot.child("userLongitude").getRef().removeValue();
                    avDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(i=1;i<=maxId;i++)
                                if(dataSnapshot.child(String.valueOf(i)).child("id").getValue()==id)
                                    dataSnapshot.child(String.valueOf(i)).getRef().removeValue();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void ConfirmDialog() {
        //self explanatory.
        AlertDialog.Builder confirm1 = new AlertDialog.Builder(MedicHomeScreenActivity2.this);
        confirm1.setTitle("משתמש זקוק לעזרתך!");
        confirm1.setMessage("משתמש בקרבת מקום זקוק לעזרה רפואית דחופה. האם אתה יכול להיענות לה?");
        confirm1.setPositiveButton("אני בדרך!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.child("tripleshake1").setValue("ready");
                DisplayTrack();
            }
        });
        confirm1.setNegativeButton("לא יכול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.child("tripleshake1").setValue("refused");
            }
        });confirm1.create().show();

    }

    public void DisplayTrack() {
        //refer to google maps track. more readily available than in=app implementation.
        LatLng userLatLng = new LatLng(userLat, userLng);
        LatLng medicLatLng = new LatLng(medicLat, medicLng);
        if(userLatLng.latitude>-1&&userLatLng.longitude>-1) {
            googleMap2.addMarker(new MarkerOptions().position(userLatLng).title("משתמש במצוקה"));
            // Creates an Intent that will load a map of San Francisco
            String intentUri = "http://maps.google.com/maps?saddr=" + medicLatLng.latitude + "," + medicLatLng.longitude + "&daddr=" + userLatLng.latitude + "," + userLatLng.longitude;
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intentUri));
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }


    private void method() {
        //set location on the map.
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
//set available latitutde and longitude. set max available medics and get an id.
// only after that set an available medic. solves most race condition problems.
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    mDatabase.child("latitude").setValue(lat);
                    mDatabase.child("longitude").setValue(lng);
                    setAvailableMedic();
                    medicLatLong = new LatLng(lat, lng);
                    Geocoder geo = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geo.getFromLocation(lat, lng, 1);
                        String str;
                        str = addressList.get(0).getLocality();
                        googleMap2.addMarker(new MarkerOptions().position(medicLatLong).title(str));
                        googleMap2.moveCamera(CameraUpdateFactory.newLatLngZoom(medicLatLong, 15.0f));
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
        //set current location info into "available medics".
        medicTrack.setLat(lat);
        medicTrack.setLng(lng);
        medicTrack.setId(id);
        medicTrack.setPassword(password);
        avDatabase.child(String.valueOf(maxId+1)).setValue(medicTrack);

    }




}
