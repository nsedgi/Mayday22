package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
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

public class MedicHomeScreenActivity2 extends FragmentActivity implements OnMapReadyCallback {
    TextView nameText, idText, organizationText, linkText, title;
    String name, id, organization, url;
    DatabaseReference mDatabase;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_home_screen2);
        setContentView(R.layout.activity_medic_home_screen);
        nameText= findViewById(R.id.name);
        idText=findViewById(R.id.id);
        organizationText=findViewById(R.id.organization);
        linkText=findViewById(R.id.link);
        title=findViewById(R.id.title);
        Intent intent = getIntent();
        id = intent.getStringExtra("lastId");
        String password = intent.getStringExtra("lastPassword");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("medics").child(id).child(password);

        idText.setText(id);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getMedicInfo();
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
                title.setText("שלום! "+name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}