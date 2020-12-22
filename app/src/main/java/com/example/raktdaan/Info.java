package com.example.raktdaan ;

//Class to Create Objects of Users and Store there Info.
public class Info {

    private  String uid = "" , fname = "" , lname = "" , gender = "" , location = "" , blood = "" , covid = "" , email = "" , pno = "" , age = "" ;
    private int imgID = 0 ;
    public Info( String id , String f , String ln , String g , String l , String b , String c , int iId ){
        uid = id ; fname = f ; lname = ln ; gender = g ; location = l ; blood = b ; covid = c ;
        imgID = iId ;
    }

    public Info( String u , String f , String l , String e , String p ,String a  ){
        uid = u ; fname = f ; lname = l ; email = e ; pno = p ; age = a;
    }

    public String getUid() {
        return uid;
    }

    public String getFname() {
        return fname;
    }

    public String getGender() {
        return gender;
    }

    public String getLname() {
        return lname;
    }

    public String getLocation() {
        return location;
    }

    public int getImgID() {
        return imgID;
    }

    public String getCovid() {
        return covid;
    }


    public String getBlood() {
        return blood;
    }

    public String getEmail() {
        return email;
    }

    public String getPno() {
        return pno;
    }

    public String getAge() {
        return age;
    }
}
