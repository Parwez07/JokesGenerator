package com.example.retroassigment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    List<String> data;
    TextView setUp,setContent,punch,punchContent;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();


        setContent = findViewById(R.id.setupText);
        setUp = findViewById(R.id.idSetup);
        punch = findViewById(R.id.idPunch);
        punchContent = findViewById(R.id.punchText);
        next = findViewById(R.id.btnNext);

        setContent.setVisibility(View.GONE);
        punchContent.setVisibility(View.GONE);
        meme();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meme();
            }
        });

    }

    public void meme(){
        RetrofitClinet.getRetrofit().retrofitApi.getModelClass().enqueue(new Callback<ModelClass>() {
            @Override
            public void onResponse(Call<ModelClass> call, Response<ModelClass> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Error...",Toast.LENGTH_SHORT).show();
                    Log.e("api","fail.."+response.body());
                }
                String punch = response.body().getPunchline();
                String setup = response.body().getSetup();
                setContent.setText(setup);
                punchContent.setText(punch);
                setContent.setVisibility(View.VISIBLE);
                punchContent.setVisibility(View.VISIBLE);
                Log.e("api","success.."+response.body().getPunchline());

            }

            @Override
            public void onFailure(Call<ModelClass> call, Throwable t) {
            Log.e("api","fail"+t.getLocalizedMessage());
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.action_profile){
            startActivity( new Intent(MainActivity.this,profile.class));
        }
        if(item.getItemId()==R.id.idSingOut){
            auth.signOut();
            startActivity(new Intent(MainActivity.this,SignUp.class));
        }
        return super.onOptionsItemSelected(item);
    }
}