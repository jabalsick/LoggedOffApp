package com.fdv.loggedoff.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by blaja on 21/10/2015.
 */

@IgnoreExtraProperties
public class    Turno {
  //  private int pos;
    private String nombre;
    private String hora;
    private String profile_photo;
    private String mail;
    private String uid;

    public Turno(){
    }

    public Turno(String hora, String nombre, String profile_photo,String mail,String uid){
        this.nombre=nombre;
        this.hora=hora;
        this.profile_photo = profile_photo;
        this.mail=mail;
        this.uid=uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getProfile_photo() {
        return  profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("nombre", nombre);
        result.put("profile_photo", profile_photo);
        result.put("mail", mail);

        return result;
    }

}
