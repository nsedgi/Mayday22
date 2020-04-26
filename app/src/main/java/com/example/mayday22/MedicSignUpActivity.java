package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MedicSignUpActivity extends AppCompatActivity {
    EditText id, password, name, organization;
    Button signUp;
    boolean confirmFlag;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic_sign_up);
        id = findViewById(R.id.id);
        password=findViewById(R.id.password);
        name = findViewById(R.id.name);
        organization=findViewById(R.id.organization);
        signUp=findViewById(R.id.signUp);
        confirmFlag=true;
        mDatabase = FirebaseDatabase.getInstance().getReference();



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();


            }
        });

    }
    public void validateUser(){
        String idTemp = id.getText().toString();
        mDatabase.child("medics").child(idTemp).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                if(dataSnapshot2.exists()){
                    Toast.makeText(getApplicationContext(), "תעודת זהות קיימת במערכת!", Toast.LENGTH_LONG).show();
                }
                else {
                    setMedicInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setMedicInfo() {
        Medic medic = new Medic();
        String idTemp, passwordTemp, nameTemp, organizationTemp;
        idTemp = id.getText().toString();
        passwordTemp=password.getText().toString();
        nameTemp=name.getText().toString();
        organizationTemp=organization.getText().toString();
        if(idTemp.length()!=9)
            Toast.makeText(getApplicationContext(), "על תעודת הזהות להכיל 9 ספרות בלבד!", Toast.LENGTH_LONG).show();
        else
            if(passwordTemp.length()<3 || passwordTemp.length()> 10)
                Toast.makeText(getApplicationContext(), "על הסיסמה להכיל בין 3-10 תווים!", Toast.LENGTH_LONG).show();
            else
                if (name.length()<1)
                    Toast.makeText(getApplicationContext(), "יש להזין שם מלא", Toast.LENGTH_LONG).show();
                else{
                    medic.setId(idTemp);
                    medic.setName(nameTemp);
                    medic.setPassword(passwordTemp);
                    medic.setOrganization(organizationTemp);
                    mDatabase.child("medics").child(idTemp).child(passwordTemp).setValue(medic);
                    Toast.makeText(getApplicationContext(), "הנתונים נשמרו בהצלחה", Toast.LENGTH_SHORT).show();
                }
    }
}
