package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    private void validatePassword(String idTemp) {
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
                    moveActivity();
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
            private void validatePasswordMedic(String idTemp) {
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
                            moveActivityMedic();
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
    public void moveActivity(){
        Intent moveToHomeScreen =  new Intent(this, UserHomeScreenActivity.class);
        startActivity(moveToHomeScreen);
    }
    public void moveActivityMedic(){
        Intent moveToHomeScreen =  new Intent(this, MedicHomeScreenActivity.class);
        startActivity(moveToHomeScreen);
    }
}
