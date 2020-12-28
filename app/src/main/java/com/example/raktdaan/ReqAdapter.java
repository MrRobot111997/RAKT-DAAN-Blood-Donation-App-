package com.example.raktdaan;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
//This Class Overrides Array Adapter and Allows me Implement my Own Vision of the List View , This One is for the List of Users
//Who has Requested Blood From You .
public class ReqAdapter extends ArrayAdapter {
    public ReqAdapter(Activity context , ArrayList<Info> infos , int color ){
        super(context , 0  , infos) ;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View customView = convertView ;

        if( customView == null ){
            customView = LayoutInflater.from(getContext()).inflate((R.layout.custom_req_listview) , parent ,false) ;
        }

        Info currInfo = (Info) getItem( position ) ;

        TextView rName = customView.findViewById( R.id.rNameView ) ;
        TextView rAge = customView.findViewById( R.id.rAgeView ) ;
        TextView rEmail = customView.findViewById( R.id.rEmailView ) ;
        TextView rPhone = customView.findViewById( R.id.rPhoneView ) ;

        String fullName = currInfo.getFname() + " " + currInfo.getLname() ;
        rName.setText( fullName );
        rAge.setText(currInfo.getAge());
        rEmail.setText(currInfo.getEmail());
        rPhone.setText( currInfo.getPno() );

        return customView ;
    }
}
