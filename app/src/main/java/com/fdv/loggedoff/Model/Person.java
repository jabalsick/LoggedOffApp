package com.fdv.loggedoff.Model;

/**
 * Created by blaja on 21/10/2015.
 */
public class Person {

    String uid;
    String name;
    String email;
    String provider;
    String profile_photo;
    String isAdmin;

   public Person(){}

   public Person(String name) {
        this.name = name;

    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String  getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profile_photo = profilePhoto;
    }
}
