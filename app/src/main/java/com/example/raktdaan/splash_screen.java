package com.example.raktdaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class splash_screen extends AppCompatActivity {

    Intent regIntent , userIntent ;
    FirebaseAuth mAuth ;
    public void logIn(){

        FirebaseDatabase.getInstance().getReference().child("user").child( mAuth.getCurrentUser().getUid() ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.exists() ) {
                    userIntent = new Intent(splash_screen.this, requests.class);
                    startActivity(userIntent);
                }
                else{
                    regIntent = new Intent( splash_screen.this , registration.class ) ;
                    startActivity(regIntent) ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                regIntent = new Intent( splash_screen.this , registration.class ) ;
                startActivity(regIntent) ;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if( FirebaseAuth.getInstance().getCurrentUser() == null ){
            Intent i = new Intent(this, MainActivity.class) ;
            startActivity(i);
        }else{
            logIn();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance() ;

    }
}