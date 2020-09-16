package com.example.mayday22;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Ref;

public class MedicSignUpActivity extends AppCompatActivity {
    EditText id, password, name, organization;
    Button signUp, upload;
    ProgressBar uploadProgressBar, signUpBar;
    boolean confirmFlag;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private String urlLink;
    private int urlFlag=0;

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
        upload = findViewById(R.id.uploadButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("medicImages");
        uploadProgressBar = findViewById(R.id.uploadbar);
        signUpBar = findViewById(R.id.signUpBar);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload.setVisibility(View.INVISIBLE);
                uploadProgressBar.setVisibility(View.VISIBLE);
                Upload();

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp.setVisibility(View.INVISIBLE);
                signUpBar.setVisibility(View.VISIBLE);
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
                    if(urlFlag ==1)
                    medic.setIdUrl(urlLink);
                    else
                        medic.setIdUrl("");
                    mDatabase.child("medics").child(idTemp).child(passwordTemp).setValue(medic);
                    signUp.setVisibility(View.VISIBLE);
                    signUpBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "הנתונים נשמרו בהצלחה", Toast.LENGTH_SHORT).show();
                }
    }
    public void Upload(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData()!=null) {
            uri = data.getData();
            fileExtension(uri);
            uploadFile(uri);
        }
    }
    public String fileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void uploadFile(Uri uri){
        StorageReference ref = mStorageRef.child(System.currentTimeMillis()+"."+fileExtension(uri));

        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlLink = uri.toString();
                            }
                        });


                        //Uri downloadUrl = downloadUrlTask.getResult();

                        uploadProgressBar.setVisibility(View.INVISIBLE);
                        upload.setVisibility(View.VISIBLE);
                        upload.setClickable(false);
                        Toast.makeText(getApplicationContext(), "תמונה נשמרה במאגר בהצלחה", Toast.LENGTH_SHORT).show();
                        urlFlag=1;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), "תמונה לא נשמרה במאגר בהצלחה", Toast.LENGTH_SHORT).show();
                        // ...
                    }
                });

    }
}
