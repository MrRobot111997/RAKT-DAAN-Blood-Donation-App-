package com.example.raktdaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class registration extends AppCompatActivity {

    Button getLoc , submitB ;
    EditText fname , lname , age , pno ;
    TextView textLocation  ;
    RadioGroup gender ,covid ;
    RadioButton gender1 , gender2 , gender3 ,yes , no ;
    ProgressBar progressBar ;
    Spinner groupSpinner ;
    String loc = "" ,  bGroup = "A+" , covidS = "No" ;
    FusedLocationProviderClient fusedLocationProviderClient ;
    LocationManager locManager ;
    LocationRequest locationRequest ;
    boolean back = true ; //Track Back Button Press .

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if( locationResult == null ) {
                return;
            }

            Location location = locationResult.getLastLocation() ;
            Geocoder geoLoc = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addList = geoLoc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addList != null && addList.size() > 0) {
                    loc = addList.get(0).getLocality();
                    textLocation.setText(loc);
                    Toast.makeText(registration.this, "Location " + loc, Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                Toast.makeText(registration.this, "Unable to Access Location", Toast.LENGTH_SHORT).show();
                ;
                e.printStackTrace();

            }
        }
    } ;


    public void getLocation( View view ){

        if(ContextCompat.checkSelfPermission( this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            Toast.makeText( this , "Repress Get Location after Granting Permission" , Toast.LENGTH_SHORT ).show() ;
        }else{

            if( !locManager.isProviderEnabled( LocationManager.GPS_PROVIDER )  ) {
                Toast.makeText( registration.this , "Please Enable Location Services" , Toast.LENGTH_SHORT ).show();
                return ;
            }

            fusedLocationProviderClient.requestLocationUpdates( locationRequest ,  locationCallback , Looper.getMainLooper() ) ;

            progressBar.setVisibility(View.VISIBLE);
            getLoc.setVisibility(View.INVISIBLE);


            new Handler().postDelayed(() -> {
                progressBar.setVisibility(View.INVISIBLE);
                getLoc.setVisibility(View.VISIBLE);
//                fusedLocationProviderClient.e
            }, 2000);

        }

    }


    public void commitData( View view ){
        String firstName , lastName , ageS , phoneNo , genderS = "Male" ;

            firstName = fname.getText().toString();
            lastName = lname.getText().toString();
            ageS = age.getText().toString() ;
            phoneNo = pno.getText().toString() ;

            if(TextUtils.isEmpty( firstName )){
                fname.setError("This Field can't be empty");
                return ;
            }

            if(TextUtils.isEmpty( lastName )){
                lname.setError("This Field can't be empty");
                return ;
            }

            if(TextUtils.isEmpty( ageS )){
                age.setError("This Field can't be empty");
                return ;
            }

            if(TextUtils.isEmpty( phoneNo )){
                pno.setError("This Field can't be empty");
                return ;
            }

            if( phoneNo.length() < 10 ){
                pno.setError( "Invalid Phone Number." );
                return ;
            }

            if( loc.equals("") ){
                getLoc.setError( "Please Provide Location" );
                Toast.makeText(this , "Please give your Location" , Toast.LENGTH_SHORT ).show(); ;
                return ;
            }

            if( gender.getCheckedRadioButtonId() == R.id.radioButton ) genderS = "Male" ;
            else if( gender.getCheckedRadioButtonId() == R.id.radioButton2 ) genderS = "Female" ;
            else genderS = "Others" ;

            if( covid.getCheckedRadioButtonId() == R.id.yes ) covidS= "Yes" ;
            else covidS = "No" ;

            try {
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fname").setValue(firstName);
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lname").setValue(lastName);
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("phone").setValue(phoneNo);
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("age").setValue(ageS);
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("gender").setValue(genderS);
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("covid").setValue(covidS);
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("blood").setValue(bGroup);
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").setValue( loc );

                //Query Key
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location_blood_covid").setValue( loc + "_" + bGroup + "_" + covidS );

                Toast.makeText(registration.this , "Successfully Registered" , Toast.LENGTH_SHORT ).show(); ;

                Intent i = new Intent( this , requests.class ) ;
                startActivity(i);
            }catch( Exception e ){
                e.printStackTrace();
                Toast.makeText(registration.this , "Registration Failed" , Toast.LENGTH_SHORT ).show(); ;
            }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setTitle( "Registration" ) ;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) ;
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE) ;
        locationRequest = LocationRequest.create().setInterval( 4000 )
                .setFastestInterval( 2000 )
                .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
                .setNumUpdates(1) ;

        groupSpinner = findViewById(R.id.groupSpinner) ;

        List<String> groups =  Arrays.asList("A+" , "A-" , "B+" , "B-" , "AB+" , "AB-" , "O+" , "O-" ) ;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this , android.R.layout.simple_spinner_dropdown_item , groups) ;
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        groupSpinner.setAdapter(    dataAdapter) ;

        fname = findViewById( R.id.editFname ) ;
        lname = findViewById( R.id.editLname ) ;
        age = findViewById( R.id.editTextage ) ;
        pno = findViewById( R.id.editTextPhone ) ;

        gender = findViewById( R.id.radioGroup ) ;
        gender1 = findViewById( R.id.radioButton ) ;
        gender2 = findViewById( R.id.radioButton2 ) ;
        gender3 = findViewById( R.id.radioButton3 ) ;
        progressBar = findViewById( R.id.locProgressBar ) ;
        getLoc = findViewById( R.id.getLoc ) ;
        submitB = findViewById( R.id.regSubmit ) ;

        covid = findViewById(R.id.covidrRadioGroup) ;
        yes = findViewById( R.id.yes ) ;
        no = findViewById( R.id.no ) ;

        textLocation = findViewById( R.id.locationTextView ) ;

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bGroup = groups.get(i) ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        }) ;

        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.exists() ){

                    back = false ;
                    fname.setText( Objects.requireNonNull(snapshot.child("fname").getValue()).toString() );
                    lname.setText( Objects.requireNonNull(snapshot.child("lname").getValue()).toString() );
                    age.setText( Objects.requireNonNull(snapshot.child("age").getValue()).toString() );
                    pno.setText( Objects.requireNonNull(snapshot.child("phone").getValue()).toString() );;
                    groupSpinner.setSelection( groups.indexOf( Objects.requireNonNull(snapshot.child("blood").getValue()).toString() )  );

                    if( !Objects.requireNonNull(snapshot.child("gender").getValue()).toString().equals( "Male" ) )
                    gender.check( ( Objects.requireNonNull(snapshot.child("gender").getValue()).toString().equals("Female") ? R.id.radioButton2 : R.id.radioButton3 ) );

                    if( Objects.requireNonNull(snapshot.child("covid").getValue()).toString().equals( "Yes" ) )
                        covid.check( R.id.yes ) ;

                    loc = Objects.requireNonNull(snapshot.child("location").getValue()).toString() ;
                    textLocation.setText( loc );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if( back ){
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

    }
}