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

        //Checks if User is Registered or not .
        FirebaseDatabase.getInstance().getReference().child("user").
                child( mAuth.getCurrentUser().getUid() )
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // If the user is registered then takes him/her into the app .
                if( snapshot.exists() ) {
                    userIntent = new Intent(splash_screen.this, requests.class);
                    startActivity(userIntent);
                }
                else{
                    //If not registered takes the user to
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

    //On Start Checks if user is Logged in or Not.
    @Override
    protected void onStart() {
        super.onStart();

        //If not Logged in Takes user to Login Activity
        if( FirebaseAuth.getInstance().getCurrentUser() == null ){
            Intent i = new Intent(this, MainActivity.class) ;
            startActivity(i);
        }else{
            //If user is Logged int then Checks if User has registered or not .
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