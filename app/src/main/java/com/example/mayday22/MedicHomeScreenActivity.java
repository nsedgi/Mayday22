package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MedicHomeScreenActivity extends AppCompatActivity {
    TextView nameText, idText, organizationText, linkText, title;
    String name, id, organization, url;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getMedicInfo();
        idText.setText(id);

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
