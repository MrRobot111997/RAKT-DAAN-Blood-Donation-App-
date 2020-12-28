package com.example.raktdaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// Code Companion of the Request Activity

public class requests extends AppCompatActivity {

    Spinner spinner ;
    RadioGroup covReq ;
    RadioButton yes , no ;
    Button searchB ;
    ProgressBar reqProgress ;
    ListView avaList ;
    ArrayList<Info> arrayList ;
    InfoAdapter infoAdapter ;
    LocationManager locationManager ;
    LocationRequest locationRequest ;
    FusedLocationProviderClient fusedLocationProviderClient ;
    String loc = "" , bloodG = "A+" , covidS = "No" ;
    Info userInfo ;


    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if( locationResult == null ){
                return ;
            }

            Location location = locationResult.getLastLocation() ;
            Geocoder geocoder = new Geocoder( getApplicationContext() , Locale.getDefault() ) ;

            try {
                List<Address> addList = geocoder.getFromLocation( location.getLatitude() , location.getLongitude() , 1 ) ;

                if( addList.size() > 0 ){
                    loc = addList.get(0).getLocality() ;
                }
            } catch (Exception e) {
                Toast.makeText( requests.this , "Start Location Services First" , Toast.LENGTH_SHORT ).show() ;
            }

        }
    };

    //Creates and Add Menu to the UI
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =new MenuInflater(this ) ;
        menuInflater.inflate( R.menu.usermenu , menu );
        return super.onCreateOptionsMenu(menu);
    }

    //Gets the Menu Selected and Provides Proper Response
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if( item.getItemId() == R.id.editprofile ){
            Intent i = new Intent( this , registration.class ) ;
            startActivity( i );
        }else if( item.getItemId() == R.id.logout ){
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent( this , MainActivity.class  ) ;
            startActivity( i );
        }
        else if(item.getItemId() == R.id.editpass){
            FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if( task.isSuccessful() ){
                                Toast.makeText(requests.this, "Please Check Your Mail",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(requests.this, "Unable to Send Password Reset Mail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else if( item.getItemId() == R.id.request ){
            Intent i = new Intent( this , your_requests.class );
            startActivity(i) ;
        }else {
            new MyDialogFragment().show(getSupportFragmentManager(), "My Dialog");
        }

        return super.onOptionsItemSelected(item);
    }

    // Fetches User Location in the Background for the ease of Searching Blood Donors in your Location .
    public boolean getLocation(){

        if(ContextCompat.checkSelfPermission( this , Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions( this , new String[] { Manifest.permission.ACCESS_FINE_LOCATION } , 1 ) ;
        }else {

            if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ){
                Toast.makeText( this , "Start Location Services First" , Toast.LENGTH_SHORT ).show();
                return false ;
            }

            fusedLocationProviderClient.requestLocationUpdates( locationRequest , locationCallback , Looper.getMainLooper() ) ;

        }
        return true ;
    }

    // Works When Search Button is Pressed
    public void search(View view){

        //Covid Radio Switch
        covidS = ( yes.isChecked() ? "Yes" : "No" ) ;

        reqProgress.setVisibility( View.VISIBLE );

        Log.i("Query" , loc + "_" + bloodG + "_" + covidS ) ;

        int resID = 0 ;

        switch (bloodG) {
            case "A+": resID = R.drawable.ap; break;
            case "A-": resID = R.drawable.an; break;
            case "AB+": resID = R.drawable.abp; break;
            case "AB-": resID = R.drawable.abn; break;
            case "O+": resID = R.drawable.op; break;
            case "O-": resID = R.drawable.on; break;
            case "B+": resID = R.drawable.bp; break;
            default: resID = R.drawable.bn; break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reqProgress.setVisibility(View.INVISIBLE) ;
            }
        } , 3000 ) ;

        if( !getLocation() ) return ;

        int finalResID = resID;
        FirebaseDatabase.getInstance().getReference().child("user").orderByChild("location_blood_covid").equalTo( loc + "_" + bloodG + "_" + covidS ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for( DataSnapshot dataSnapshot : snapshot.getChildren() ){
                    arrayList.add( new Info(Objects.requireNonNull(dataSnapshot.getKey()),
                            Objects.requireNonNull(dataSnapshot.child("fname").getValue()).toString() ,
                            Objects.requireNonNull(dataSnapshot.child("lname").getValue()).toString() ,
                            Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString() ,
                            Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString() ,
                            Objects.requireNonNull(dataSnapshot.child("blood").getValue()).toString() ,
                            Objects.requireNonNull(dataSnapshot.child("covid").getValue()).toString() ,
                            finalResID) ) ;
                }
                infoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;


    }

    //As Your Info has to be Sent to the Donor from whom you Request Blood , Your Info is Featched and Saved from Firebase RealTime Database .
    public void getCurrentUserInfo(){
        FirebaseDatabase.getInstance().getReference().child("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userInfo = new Info( Objects.requireNonNull(snapshot.getKey()),
                                Objects.requireNonNull(snapshot.child("fname").getValue()).toString() ,
                                Objects.requireNonNull(snapshot.child("lname").getValue()).toString() ,
                                Objects.requireNonNull(snapshot.child("email").getValue()).toString() ,
                                Objects.requireNonNull(snapshot.child("phone").getValue()).toString(),
                                Objects.requireNonNull(snapshot.child("age").getValue()).toString()) ;

                        Toast.makeText(requests.this , "Welcome,"+userInfo.getFname() , Toast.LENGTH_SHORT ).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( FirebaseAuth.getInstance().getCurrentUser() == null ){
            Intent i = new Intent(this, MainActivity.class) ;
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        locationManager = ( LocationManager ) getSystemService( LOCATION_SERVICE ) ;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) ;
        locationRequest = LocationRequest.create().setInterval(1000)
                .setFastestInterval(500)
                .setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY )
                .setNumUpdates(1) ;

        List<String> groups =  Arrays.asList("A+" , "A-" , "B+" , "B-" , "AB+" , "AB-" , "O+" , "O-" ) ;

        covReq = findViewById( R.id.covidrRadioGroup ) ;
        yes = findViewById( R.id.yes ) ;
        no = findViewById( R.id.no ) ;

        searchB = findViewById( R.id.reqButton ) ;
        avaList = findViewById( R.id.donorsList ) ;
        reqProgress = findViewById( R.id.reqProgressBar ) ;

        spinner = findViewById( R.id.reqSpinner) ;
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this , android.R.layout.simple_spinner_dropdown_item , groups) ;
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter(dataAdapter) ;


        arrayList = new ArrayList<>() ;
        infoAdapter = new InfoAdapter (this  , arrayList , R.layout.custom_listview ) ;
        avaList.setAdapter( infoAdapter );

        getCurrentUserInfo();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bloodG = groups.get( i ) ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        avaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                new AlertDialog.Builder(requests.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Do You Want to Request Blood from " + arrayList.get(i).getFname() + "?" )
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int ii ) {
                                FirebaseDatabase.getInstance().getReference().child("requests").child(arrayList.get(i).getUid()).child(userInfo.getUid()).child("fname").setValue(userInfo.getFname()) ;
                                FirebaseDatabase.getInstance().getReference().child("requests").child(arrayList.get(i).getUid()).child(userInfo.getUid()).child("lname").setValue(userInfo.getLname()) ;
                                FirebaseDatabase.getInstance().getReference().child("requests").child(arrayList.get(i).getUid()).child(userInfo.getUid()).child("email").setValue(userInfo.getEmail()) ;
                                FirebaseDatabase.getInstance().getReference().child("requests").child(arrayList.get(i).getUid()).child(userInfo.getUid()).child("phone").setValue(userInfo.getPno()) ;
                                FirebaseDatabase.getInstance().getReference().child("requests").child(arrayList.get(i).getUid()).child(userInfo.getUid()).child("age").setValue(userInfo.getAge()) ;
                                FirebaseDatabase.getInstance().getReference().child("requests").child(arrayList.get(i).getUid()).child(userInfo.getUid()).child("uid").setValue( arrayList.get(i).getUid() ) ;
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(requests.this , "Request Cancelled" , Toast.LENGTH_SHORT).show();
                            }
                        }).show() ;
            }
        });

        //Gets User Permission to use Location
        if(ContextCompat.checkSelfPermission( this , Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions( this , new String[] { Manifest.permission.ACCESS_FINE_LOCATION } , 1 ) ;
        }else{
            getLocation() ;
        }
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