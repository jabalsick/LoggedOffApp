package com.fdv.loggedoff.Activtys;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.fdv.loggedoff.Model.Person;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public abstract class BaseActivity extends AppCompatActivity
        implements
        GoogleApiClient.OnConnectionFailedListener{

    private static final String username = "masajes.fdv@gmail.com";
    private static final String password = "fdv123456";
    static protected Person mUser;
    // [START declare_auth]
    static protected FirebaseAuth mAuth;
    // [END declare_auth]
    static protected FirebaseUser signInAccount;
    public static String DEFAULT_PHOTO ="DEFAULT";
    public static DatabaseReference userRef  = FirebaseDatabase.getInstance().getReference().child("users");
    public static DatabaseReference mSchedulerFirebase  = FirebaseDatabase.getInstance().getReference();

    // File Storage reference
    public static  FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReferenceFromUrl("gs://firebase-loggedoffapp.appspot.com");


    public static Person getmUser() {
        return mUser;
    }

    public static HashMap<String,Person> allAppUsers = new HashMap<>();


    public static void setmUser(Person mUser) {
        BaseActivity.mUser = mUser;
    }

    /**
     * Starts and Activity set it up in the intent adding the page animation to the transition.
     * @param intent    intent holding the activity to start.
     */
    public void nextScreen(Intent intent){
        startActivity(intent);
      //  overridePendingTransition (R.anim.open_next, R.anim.close_main);
    }


    /**
     * Redefine to add page effect to the back transition.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    public static FirebaseUser getFirebaseUserSignIn() {
        return signInAccount;
    }

    public static void setFirebaseUserSignIn(FirebaseUser signInAccount) {
        BaseActivity.signInAccount = signInAccount;
        registerUserOnDatabase();
    }

    public static void registerUserOnDatabase() {
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("uid",signInAccount.getUid());
        nombre.put("nombre", signInAccount.getDisplayName());
        nombre.put("profile_photo",signInAccount.getPhotoUrl());
        userRef.child(signInAccount.getUid()).setValue(nombre);

    }

    public static void updateUserPhotoOnDatabase(Uri uri){
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("uid",signInAccount.getUid());
        nombre.put("nombre", signInAccount.getDisplayName());
        nombre.put("profile_photo",uri);
        userRef.child(signInAccount.getUid()).setValue(nombre);
    }


    //EMAIL IMPLEMENTATION
    public void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username, "[MASAJES]"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
     //   private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         // progressDialog = ProgressDialog.show(BaseActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
         //   progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static DatabaseReference getUserRef() {
        return mSchedulerFirebase.child("users");
    }


    public static DatabaseReference getmSchedulerFirebase() {
        return mSchedulerFirebase.child("horas");
    }

    public static void setmSchedulerFirebase(DatabaseReference mSchedulerFirebase) {
        BaseActivity.mSchedulerFirebase = mSchedulerFirebase;
    }

    public static HashMap<String, Person> getAllAppUsers() {
        return allAppUsers;
    }

    public static void setAllAppUsers(HashMap<String, Person> allAppUsers) {
        BaseActivity.allAppUsers = allAppUsers;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static void setmAuth(FirebaseAuth mAuth) {
        BaseActivity.mAuth = mAuth;
    }


    public static FirebaseUser getSignInAccount() {
        return signInAccount;
    }

    public static void setSignInAccount(FirebaseUser signInAccount) {
        BaseActivity.signInAccount = signInAccount;
    }

    public static String getDefaultPhoto() {
        return DEFAULT_PHOTO;
    }

    public static void setDefaultPhoto(String defaultPhoto) {
        DEFAULT_PHOTO = defaultPhoto;
    }

    public static void setUserRef(DatabaseReference userRef) {
        BaseActivity.userRef = userRef;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("GOOGLE_SIGN_IN", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
