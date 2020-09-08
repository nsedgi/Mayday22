package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    CheckBox isMedic;
    Button signUp, signIn;
    ProgressBar progressBar;
    EditText id, password;
    DatabaseReference mDatabase, uDatabase, uDatabaseP;
    private int PERMISSIONS_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id = findViewById(R.id.id);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);
        signUp = findViewById(R.id.signUp);
        isMedic = findViewById(R.id.checkMedic);
        progressBar = findViewById(R.id.progressBar);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("medics");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) +
        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("דרושות הרשאות");
                builder.setMessage("יש לאשר את ההרשאות הבאות: מיקום, טלפון. ההרשאות ישמשו אך ורק בשביל שימוש באפליקציה.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, PERMISSIONS_CODE);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpAction(isMedic);
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMedic.isChecked()){
                    progressBar.setVisibility(View.VISIBLE);
                    signIn.setVisibility(View.INVISIBLE);
                    validateMedic();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    signIn.setVisibility(View.INVISIBLE);
                    validateUser();
                }
            }
        });
    }

    private void validateUser() {
        final String signInTemp = id.getText().toString();
        uDatabase.child(signInTemp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(signInTemp.length()<9){
                    progressBar.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "שם המשתמש או הסיסמה לא תקינים!", Toast.LENGTH_LONG).show();}
                else
                if (dataSnapshot.exists()){
                    validatePassword(signInTemp);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "שם המשתמש או הסיסמה לא תקינים!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void validatePassword(final String idTemp) {
        final String passwordTemp = password.getText().toString();
        uDatabase.child(idTemp).child(passwordTemp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                if(passwordTemp.length()<3||passwordTemp.length()>10){
                    progressBar.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "שם המשתמש או הסיסמה לא תקינים!", Toast.LENGTH_LONG).show();}
                else
                if(dataSnapshot2.exists()){
                    Toast.makeText(getApplicationContext(), "ברוך הבא", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                    moveActivity(idTemp, passwordTemp);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "שם המשתמש או הסיסמה לא תקינים!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void validateMedic() {
        final String signInTemp = id.getText().toString();
        mDatabase.child(signInTemp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(signInTemp.length()<9){
                    progressBar.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "שם המשתמש או הסיסמה לא תקינים!", Toast.LENGTH_LONG).show();}
                else
                if (dataSnapshot.exists()){
                    validatePasswordMedic(signInTemp);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    signIn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "שם המשתמש או הסיסמה לא תקינים!", Toast.LENGTH_LONG).show();
                }
            }
            private void validatePasswordMedic(final String idTemp) {
                final String passwordTemp = password.getText().toString();
                mDatabase.child(idTemp).child(passwordTemp).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        if(passwordTemp.length()<3||passwordTemp.length()>10){
                            progressBar.setVisibility(View.INVISIBLE);
                            signIn.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "שם המשתמש או הסיסמה לא תקינים!", Toast.LENGTH_LONG).show();}
                        else
                        if(dataSnapshot2.exists()){
                            Toast.makeText(getApplicationContext(), "ברוך הבא", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            signIn.setVisibility(View.VISIBLE);
                            moveActivityMedic(idTemp, passwordTemp);
                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            signIn.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "שם המשתמש או הסיסמה לא תקינים!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    public void SignUpAction(CheckBox isMedic){
        if(!isMedic.isChecked()){
            Intent signUpUser = new Intent(this, UserSignUpActivity.class);
            startActivity(signUpUser);
        } else{
            Intent signUpMedic = new Intent(this, MedicSignUpActivity.class);
            startActivity(signUpMedic);
        }
    }
    public void moveActivity(String idTemp, String passwordTemp){
        Intent moveToHomeScreen =  new Intent(this, UserHomeScreenActivity.class);
        moveToHomeScreen.putExtra("lastId", idTemp);
        moveToHomeScreen.putExtra("lastPassword", passwordTemp);
        startActivity(moveToHomeScreen);
    }
    public void moveActivityMedic(String idTemp, String passwordTemp){
        Intent moveToHomeScreen =  new Intent(this, MedicHomeScreenActivity2.class);
        moveToHomeScreen.putExtra("lastId", idTemp);
        moveToHomeScreen.putExtra("lastPassword", passwordTemp);
        startActivity(moveToHomeScreen);
    }
}
