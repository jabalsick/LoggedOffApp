package com.fdv.loggedoff;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.fdv.loggedoff.Activtys.BaseActivity;
import com.fdv.loggedoff.Activtys.LoginActivity;


public class SplashScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

  //      setupInitialUsers();
        //thread for splash screen running
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    Log.d("Exception", "Exception" + e);
                } finally {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
                finish();
            }
        };
        logoTimer.start();
    }

   /* public void setupInitialUsers() {
      // Attach an listener to read the data at our users reference
        userRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Person user = dataSnapshot.getValue(Person.class);
                allAppUsers.put(user.getUid(),user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Person user = dataSnapshot.getValue(Person.class);
                allAppUsers.get(user.getUid()).setUid(user.getUid());
                allAppUsers.get(user.getUid()).setName(user.getName());
                allAppUsers.get(user.getUid()).setProfilePhoto(user.getProfile_photo());
                allAppUsers.get(user.getUid()).setEmail(user.getEmail());
                allAppUsers.get(user.getUid()).setProvider(user.getProvider());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Person user = dataSnapshot.getValue(Person.class);
                allAppUsers.remove(user.getUid());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
*/

}