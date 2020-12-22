package com.example.raktdaan ;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

//This Class Overrides Array Adapter and Allows me Implement my Own Vision of the List View , This One is for the List of Users From whom you Can
//Request Blood
public class InfoAdapter extends ArrayAdapter {

    public InfoAdapter(Activity context , ArrayList<Info> infos , int color ){
        super(context , 0  , infos) ;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View customView = convertView ;

        if( customView == null ){
            customView = LayoutInflater.from(getContext()).inflate((R.layout.custom_listview) , parent ,false) ;
        }

        Info currInfo = (Info) getItem(position) ;

        TextView name = customView.findViewById( R.id.nameView ) ;
        TextView gender = customView.findViewById( R.id.genderView ) ;
        TextView location = customView.findViewById( R.id.locationView ) ;
        TextView covid = customView.findViewById(R.id.covidView) ;
        ImageView imageView = customView.findViewById( R.id.groupImgView ) ;

        String fullName = currInfo.getFname() + " " + currInfo.getLname() ;
        name.setText( fullName );
        gender.setText( currInfo.getGender() );
        location.setText( currInfo.getLocation() );
        covid.setText(currInfo.getCovid());

        String bloodGroup = currInfo.getBlood() ;
        imageView.setImageDrawable( customView.getResources().getDrawable( currInfo.getImgID() ) );


        return customView ;
    }
}
