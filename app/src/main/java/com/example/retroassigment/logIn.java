package com.example.retroassigment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class logIn extends AppCompatActivity {

    CircleImageView idProfile;
    TextInputEditText idEmail,idPassword;
    Button btnLogin,btnSignUp;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        idProfile = findViewById(R.id.idProfile);
        idEmail = findViewById(R.id.idEmail);
        idPassword = findViewById(R.id.idPassword);
        btnLogin = findViewById(R.id.btnUpdate);
        auth = FirebaseAuth.getInstance();
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = idEmail.getText().toString();
                String password = idPassword.getText().toString();

                if(email.isEmpty()||password.isEmpty()){
                    Toast.makeText(logIn.this,"plz fill the required",Toast.LENGTH_SHORT).show();
                    return ;
                }else{
                    login(email,password);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(logIn.this,SignUp.class);
                startActivity(intent);
            }
        });

    }

    public void login(String email,String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Intent intent = new Intent(logIn.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(logIn.this,"login successful",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(logIn.this,"login not successful",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}