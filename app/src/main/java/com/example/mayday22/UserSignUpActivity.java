package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSignUpActivity extends AppCompatActivity {
    private DatabaseReference mDatabase, checkUser;

    private EditText userName, name, password, medInfo;
    private Button signUp;
    private User newUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        userName = findViewById(R.id.id);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        medInfo = findViewById(R.id.info3);
        signUp = findViewById(R.id.signUp3);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        checkUser = FirebaseDatabase.getInstance().getReference();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
            }


        });

    }


    private void validateUser() {
       String idTemp=userName.getText().toString();
        checkUser.child("users").child(idTemp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                if(dataSnapshot2.exists()){
                    //Username exists
                    Toast.makeText(getApplicationContext(), "תעודת הזהות קיימת במערכת!", Toast.LENGTH_LONG).show();
                }else
                    //Username does not exist
                    setUserInfo();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void setUserInfo(){
        String idTemp, nameTemp, passwordTemp, medInfoTemp;
        idTemp=userName.getText().toString();
        nameTemp=name.getText().toString();
        passwordTemp=password.getText().toString();
        medInfoTemp=medInfo.getText().toString();
//check vital info
        if(idTemp.length()!=9) Toast.makeText(this, "תעודת הזהות חייבת להיות בת 9 ספרות!", Toast.LENGTH_SHORT).show();
        else
        if(name.length()<=0) Toast.makeText(this, "נא להזין שם מלא", Toast.LENGTH_SHORT).show();
        else
        if (password.length()<3 || password.length()>10) Toast.makeText(this, "על הסיסמה להכיל בין 3 ל-10 תווים!", Toast.LENGTH_LONG).show();
        else {
//add user to database as a class.
            newUser.setUser(idTemp);
            newUser.setName(nameTemp);
            newUser.setPassword(passwordTemp);
            newUser.setMedInfo(medInfoTemp);
            newUser.setLatitude(0.0);
            newUser.setLongitude(0.0);
            mDatabase.child(idTemp).child(passwordTemp).setValue(newUser);

            Toast.makeText(getApplicationContext(), "נתונים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
            moveActivity(idTemp, passwordTemp, nameTemp);
        }
    }
//move to home screen. use the id and password to extract info.
    public void moveActivity(String idTemp, String passwordTemp, String nameTemp){
        Intent moveToHomeScreen =  new Intent(UserSignUpActivity.this, UserHomeScreenActivity.class);
        moveToHomeScreen.putExtra("lastId", idTemp);
        moveToHomeScreen.putExtra("lastPassword", passwordTemp);
        moveToHomeScreen.putExtra("lastName", nameTemp);
        startActivity(moveToHomeScreen);
        UserSignUpActivity.this.finish();
    }


}
