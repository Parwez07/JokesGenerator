package com.example.retroassigment;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    TextInputEditText idName, idEmail, idPassword, idUser;
    TextView signIn;
    Button btnlogin;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    public static final int REQUEST_PICK_IMAGE = 102;
    CircleImageView idProfile;
    ActivityResultLauncher<Intent> selectImg;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
     FirebaseAuth auth;
    private Bitmap selectedImg;
    Uri imgPath;
    boolean imgControl = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnlogin = findViewById(R.id.btnUpdate);
        idProfile = findViewById(R.id.idProfile);
        idName = findViewById(R.id.idName);
        idEmail = findViewById(R.id.idEmail);
        idPassword = findViewById(R.id.idPassword);
        idUser = findViewById(R.id.idUser);
        signIn = findViewById(R.id.SignIn);


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        auth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference();

        selectImg();
        // when signIn button got clicked
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = idPassword.getText().toString();
                String userId = idUser.getText().toString().trim();

                // check for password
                if (password.length() < 8 && !isValidPassword(password)) {
                    Toast.makeText(SignUp.this, "Enter NumAlphaChar password", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = idName.getText().toString();
                String email = idEmail.getText().toString();

                // register
                register(email,password,name,userId);

                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        idProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // check for runtime permission

                if (ActivityCompat.checkSelfPermission(SignUp.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignUp.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_ID_MULTIPLE_PERMISSIONS);
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
                    selectImg.launch(galleryIntent);
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,logIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // checking for the password validation
    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }



    // selection of img
    public  void selectImg(){
        selectImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if(resultCode==RESULT_OK && data !=null){
                        try {
                            selectedImg= MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                            imgPath = data.getData();
                            idProfile.setImageBitmap(selectedImg);
                            imgControl=true;
                        } catch (IOException e) {
                            imgControl=false;
                            throw new RuntimeException(e);
                        }

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_ID_MULTIPLE_PERMISSIONS && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
            selectImg.launch(galleryIntent);
        }
    }

    public void register(String mail ,String password,String name,String userId){
        auth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(name,password,userId,mail);
                    databaseReference.child("Users").child(Objects.requireNonNull(auth.getUid())).child("userId").setValue(name);
                    if(imgControl)
                    {
                        UUID randomId = UUID.randomUUID();
                        String imgName = "images/"+randomId+".jpg";
                        storageReference.child(imgName).putFile(imgPath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference myStorage = firebaseStorage.getReference(imgName);
                                myStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();
                                        databaseReference.child("Users").child(auth.getUid()).child("image").setValue(filePath).
                                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(SignUp.this,"Successfull..",Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SignUp.this,"Failed..",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        });

                    }else{
                        databaseReference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }
                    Toast.makeText(SignUp.this,"Registered...",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SignUp.this,"not Registered",Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(SignUp.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}