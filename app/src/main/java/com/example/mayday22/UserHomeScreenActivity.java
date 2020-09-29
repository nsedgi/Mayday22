package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHomeScreenActivity extends AppCompatActivity {
    private int PHONE_PERMISSION_CODE=1;
    TextView dialAmbulance, title, userMedInfo;
    ImageView dialAmbulanceIcon, distressCall;
    DatabaseReference uDatabase;
    int callFlag;

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
        callFlag = 0; //set call flag to false as default.
        final String id = intent.getStringExtra("lastId");
        final String password = intent.getStringExtra("lastPassword");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id).child(password);
        getUserInfo();

        distressCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the user has manually called an ambulance via the app.
                if (callFlag == 0){
                    AlertDialog.Builder callAmbulance = new AlertDialog.Builder(UserHomeScreenActivity.this);
                    callAmbulance.setTitle("האם התקשרת למוקד חירום?");
                    callAmbulance.setMessage("טרם התקשרת למוקד חירום. אנחנו ממליצים להתקשר למוקד החירום. זכור - חובש לא מחליף אמבולנס ופינוי לבית החולים!");
                    callAmbulance.setPositiveButton("שיחה למוקד חירום", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+101));
                            if (ActivityCompat.checkSelfPermission(UserHomeScreenActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                            }else{
                                requestPhonePermission();

                            }
                            startActivity(callIntent);
                        }
                    });
                    callAmbulance.setNegativeButton("לא מעוניין להתקשר.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            distressCall(id, password, userMedInfo.toString());
                        }
                    }); callAmbulance.create().show();
                }
                else
                distressCall(id, password, userMedInfo.toString());
            }
        });




        dialAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFlag = 1;
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+101));
                if (ActivityCompat.checkSelfPermission(UserHomeScreenActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }else{
                    requestPhonePermission();

                }
                startActivity(callIntent);
            }
        });


    }

    private void requestPhonePermission() {
            ActivityCompat.requestPermissions(UserHomeScreenActivity.this,new String[]{Manifest.permission.CALL_PHONE}, PHONE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PHONE_PERMISSION_CODE){
            if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "גישה אושרה", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), "בעיה בהרשאת גישה", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUserInfo(){
//get the info for the user based off id and password.
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
    public void distressCall(String id, String password, String medInfo){
        Intent moveToDistressCall =  new Intent(this, UserDistressActivity.class);
        moveToDistressCall.putExtra("lastId", id);
        moveToDistressCall.putExtra("lastPassword", password);
        moveToDistressCall.putExtra("lastMedInfo", medInfo);
        startActivity(moveToDistressCall);
        UserHomeScreenActivity.this.finish();

    }
}
