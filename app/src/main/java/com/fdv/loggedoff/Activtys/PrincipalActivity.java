package com.fdv.loggedoff.Activtys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fdv.loggedoff.Adapters.PagerAdapter;
import com.fdv.loggedoff.Model.Turno;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Utils.DateUtils;
import com.fdv.loggedoff.Views.CustomTextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PrincipalActivity  extends BaseActivity {
    TabLayout tabLayout;
    Toolbar toolbar;
    private static GoogleApiClient mGoogleApiClient;
    static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        mContext = getApplicationContext();
        createGoogleApiClient();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        setupTabIcons();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                        setupSelectedTab(tab);
                        viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                 setupUnSelectedTab(tab);
                viewPager.setCurrentItem(tab.getPosition());
                if (((CustomTextView) tab.getCustomView()).getText().
                        equals(getResources().getString(R.string.profile))) {
                   // updateUser();
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        checkScheduler();

    }

    private void checkScheduler() {
        final String day = DateUtils.formatDate(new Date(),DateUtils.DAYMONTHYEAR);

        mSchedulerFirebase.child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // handle the case where the data already exists
                    Toast.makeText(PrincipalActivity.this,"YA EXISTE",Toast.LENGTH_LONG).show(); //do other stuff;
                }
                else {
                    // handle the case where the data does not yet exist
                    resetScheduler(day);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void createGoogleApiClient() {

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    public void setupSelectedTab(TabLayout.Tab tab){
        ((CustomTextView)tab.getCustomView()).
                setTextColor(getResources().getColor(R.color.colorAccent));
        if(((CustomTextView)tab.getCustomView()).getText()
                .equals(getResources().getString(R.string.turnos))){
            ((CustomTextView)tab.getCustomView())
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.turnos_selected_icon, 0, 0, 0);


        }else{
            ((CustomTextView)tab.getCustomView())
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_selected_icon, 0, 0, 0);
        }

    }

    public void setupUnSelectedTab(TabLayout.Tab tab){
        ((CustomTextView)tab.getCustomView()).
                setTextColor(getResources().getColor(R.color.mb_white));

        if(((CustomTextView)tab.getCustomView()).getText()
                .equals(getResources().getString(R.string.turnos))){
            ((CustomTextView)tab.getCustomView())
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.turnos_seated_icon_24, 0, 0, 0);
        }else{
            ((CustomTextView)tab.getCustomView())
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_icon_24, 0, 0, 0);
        }

    }


    private void setupTabIcons() {

        CustomTextView tab1 = (CustomTextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tab1.setText(R.string.turnos);
        tab1.setTextColor(getResources().getColor(R.color.colorAccent));
        tab1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.turnos_selected_icon, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tab1);

        CustomTextView tab2 = (CustomTextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tab2.setText(R.string.profile);
        tab2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.profile_icon_24, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tab2);


    }

    public void resetScheduler(final String day){
        mSchedulerFirebase.child("horas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot turnSnapshot: snapshot.getChildren()) {
                       Turno mTurno = turnSnapshot.getValue(Turno.class);
                       String hora = mTurno.getHora().replace(":", "");
                    DatabaseReference hourRef = mSchedulerFirebase.child(day).child(hora);
                    Map<String, Object> nombre = new HashMap<String, Object>();
                    nombre.put("hora",mTurno.getHora());
                    nombre.put("asigned",false);
                    nombre.put("nombre", "LIBRE");
                    nombre.put("profile_photo", "EMPTY");
                    nombre.put("mail","EMPTY");
                    nombre.put("uid", "EMPTY");
                    hourRef.setValue(nombre);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                signOut();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal_activity, menu);
   /*     if(mUser.getIsAdmin().equals("1")){
            menu.getItem(0).setVisible(true) ;
        }else{
            menu.getItem(0).setVisible(false);
        }*/
        return true;
    }

    public void signOut() {
        // Firebase sign out
       mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        BaseActivity.signInAccount = null;
                        finish();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        signOut();
    }
}
