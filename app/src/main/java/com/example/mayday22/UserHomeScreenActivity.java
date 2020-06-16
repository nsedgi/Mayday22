package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHomeScreenActivity extends AppCompatActivity {
    TextView dialAmbulance, title, userMedInfo;
    ImageView dialAmbulanceIcon, distressCall;
    DatabaseReference uDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_screen);
        dialAmbulance = findViewById(R.id.dial);
        dialAmbulanceIcon = findViewById(R.id.dial2);
        title=findViewById(R.id.titleMessage);
        userMedInfo = findViewById(R.id.medInfo);
        distressCall = findViewById(R.id.panicButton);
        Intent intent = getIntent();
        String id = intent.getStringExtra("lastId");
        String password = intent.getStringExtra("lastPassword");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id).child(password);
        getUserInfo();

        distressCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distressCall();
            }
        });




        dialAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+101));
                if (ActivityCompat.checkSelfPermission(UserHomeScreenActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        });


    }

    public void getUserInfo(){

        uDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String medInfo = dataSnapshot.child("medInfo").getValue().toString();
                title.setText("שלום "+ name+"!");
                userMedInfo.setText(medInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void distressCall(){
        Intent moveToDistressCall =  new Intent(this, UserDistressActivity.class);
        startActivity(moveToDistressCall);

    }
}
