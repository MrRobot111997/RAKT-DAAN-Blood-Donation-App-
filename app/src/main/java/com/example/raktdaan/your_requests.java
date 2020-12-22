package com.example.raktdaan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class your_requests extends AppCompatActivity {

    ArrayList<Info> arrayList ;

    ListView listView ;
    ReqAdapter reqAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_requests);
        setTitle( "Requests From You" );

        listView = findViewById( R.id.reqListView ) ;

        arrayList = new ArrayList<>() ;

        reqAdapter = new ReqAdapter( this , arrayList , R.layout.custom_req_listview ) ;
        listView.setAdapter( reqAdapter );

        FirebaseDatabase.getInstance().getReference().child("requests").child( FirebaseAuth.getInstance().getCurrentUser().getUid() ).orderByChild("uid").equalTo( FirebaseAuth.getInstance().getCurrentUser().getUid() ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();

                for( DataSnapshot dataSnapshot : snapshot.getChildren() ){

                    arrayList.add(new Info( "UID" ,
                            Objects.requireNonNull(dataSnapshot.child("fname").getValue()).toString(),
                            Objects.requireNonNull(dataSnapshot.child("lname").getValue()).toString(),
                            Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString(),
                            Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString(),
                            Objects.requireNonNull(dataSnapshot.child("age").getValue()).toString()));
                }

                reqAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {

            new AlertDialog.Builder(your_requests.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle( "Do You want to remove this Request?" )
                    .setPositiveButton("Yes", (dialogInterface, ii) -> {

                        FirebaseDatabase.getInstance().getReference().child("requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("fname").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeValue() ;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }) ;
                        try {
                            arrayList.remove(i) ;
                        }catch ( Exception e ){
                            Log.i("Index" , "" + i ) ;
                        }
                        reqAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int ii) {

                        }
                    })
                    .show() ;
                    arrayList.remove(i) ;
            return false;
        });

    }
}