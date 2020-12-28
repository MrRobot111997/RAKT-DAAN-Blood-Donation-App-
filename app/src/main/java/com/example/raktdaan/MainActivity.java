package com.example.raktdaan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth ;
    Button buttonLogin ;
    TextView signlog ;
    boolean mode = false ;
    EditText editMail , editPassword ;
    ProgressBar progressBar ;
    Intent regIntent , userIntent ;

    GoogleSignInOptions gso ;
    GoogleSignInClient mGoogleSignInClient ;
    SignInButton mSignInButton ;
    int RC_SIGN_IN = 101 ;

    public void swapModes(View view){
        if( !mode ) {
            setTitle(R.string.signup);
            buttonLogin.setText(R.string.signup);
            signlog.setText("Or, Log In");
            mode = true ;
        }else{
            setTitle(R.string.login);
            buttonLogin.setText(R.string.login);
            signlog.setText("Or, Start Donating");
            mode = false ;
        }
    }

    public void signlog(View view){
        String email = String.valueOf(editMail.getText());
        String password = String.valueOf(editPassword.getText());

        if( password.length() < 6 ){
            Toast.makeText( MainActivity.this, "Password Length Should be Greater than Equal to 6" , Toast.LENGTH_SHORT ).show() ;
            return ;
        }

        buttonLogin.setVisibility(View.INVISIBLE);
        progressBar.setVisibility( View.VISIBLE );

        new Handler().postDelayed(() -> {
            buttonLogin.setVisibility( View.VISIBLE );
            progressBar.setVisibility( View.INVISIBLE );
        }, 3000 ) ;

        try {
            Log.i(email, password);
            if (!mode) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "Log In Successful", Toast.LENGTH_SHORT).show() ;
                                logIn();
                                //                                updateUI(user);
                            } else {
                                Toast.makeText(MainActivity.this, "Email or Password Incorrect.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

//                                user.sendEmailVerification().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if( task.isSuccessful() ){
//                                            logIn();
//                                        }else{
//                                            Toast.makeText(MainActivity.this, "Please verify Email",
//                                                    Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });

                                Toast.makeText(MainActivity.this, "Sign Up Successful.", Toast.LENGTH_SHORT).show() ;
                                logIn();
                            } else {
                                Toast.makeText(MainActivity.this, "Sign Up failed.\nPlease use Valid Email and Password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }catch( Exception e ){
            Toast.makeText(MainActivity.this , "Please make correct Input" , Toast.LENGTH_SHORT  ).show() ;
        }


    }

    public void gSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == RC_SIGN_IN ){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data) ;
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class) ;
                firebaseAuthWithGoogle( account.getIdToken() ) ;
            }catch (ApiException e){
                Toast.makeText(MainActivity.this, "Sign In with Google Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void firebaseAuthWithGoogle( String idToken ){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null) ;
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "SignIn/Up Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            logIn();
                        }else{
                            Toast.makeText(MainActivity.this, "SignIn/Up Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    public void logIn(){

        FirebaseDatabase.getInstance().getReference().child("user").child( mAuth.getCurrentUser().getUid() )
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.exists() ) {
                    userIntent = new Intent(MainActivity.this, requests.class);
                    startActivity(userIntent);
                }
                else{
                    regIntent = new Intent( MainActivity.this , registration.class ) ;
                    startActivity(regIntent) ;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                regIntent = new Intent( MainActivity.this , registration.class ) ;
                startActivity(regIntent) ;
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser() ;
        Log.i("User" , String.valueOf(currentUser)) ;
        if( currentUser != null ) logIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build() ;

        mGoogleSignInClient = GoogleSignIn.getClient(this , gso );
        mSignInButton = findViewById( R.id.google_button ) ;
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gSignIn();
            }
        });

        setTitle( R.string.login );
        mAuth = FirebaseAuth.getInstance();
        buttonLogin = findViewById( R.id.buttonLogin ) ;
        signlog = findViewById( R.id.sinlog ) ;
        editMail = findViewById(R.id.editTextEmail) ;
        editPassword = findViewById(R.id.editTextPassword) ;
        progressBar = findViewById( R.id.loginProgressBar ) ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}