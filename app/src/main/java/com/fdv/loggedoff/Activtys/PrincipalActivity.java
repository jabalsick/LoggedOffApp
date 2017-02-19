package com.fdv.loggedoff.Activtys;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fdv.loggedoff.Adapters.PagerAdapter;
import com.fdv.loggedoff.Model.Turno;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Services.AlarmReceiver;
import com.fdv.loggedoff.Utils.DateUtils;
import com.fdv.loggedoff.Views.CustomTextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

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
        setupTabTurnCounter();
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

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        checkScheduler();

        checkForAlarmNotification();
    }

    private void checkForAlarmNotification() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT).cancel();
    }

    private void setupTabTurnCounter() {
        String dateNode = DateUtils.formatDate(new Date(),DateUtils.DAYMONTHYEAR);
       Query freeTurnQuery = mSchedulerFirebase.child(dateNode).orderByChild("asigned").equalTo(false);
        ValueEventListener freeTurnChangetListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)tabLayout.getTabAt(0).getCustomView().findViewById(R.id.counter_tab)).setText(""+dataSnapshot.getChildrenCount());
                if( dataSnapshot.getChildrenCount() > 0) {
                    ((TextView)tabLayout.getTabAt(0).getCustomView().findViewById(R.id.counter_tab)).setVisibility(View.VISIBLE);
                }else{
                    ((TextView)tabLayout.getTabAt(0).getCustomView().findViewById(R.id.counter_tab)).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        freeTurnQuery.addValueEventListener(freeTurnChangetListener);
    }

    private void checkScheduler() {
        final String day = DateUtils.formatDate(new Date(),DateUtils.DAYMONTHYEAR);

        mSchedulerFirebase.child(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    createScheduler(day);
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
        ((TextView)tab.getCustomView().findViewById(R.id.text_tab)).
                setTextColor(getResources().getColor(R.color.colorAccent));
    }

    public void setupUnSelectedTab(TabLayout.Tab tab){
        ((TextView)tab.getCustomView().findViewById(R.id.text_tab)).
                setTextColor(getResources().getColor(R.color.mb_white));
    }


    private void setupTabIcons() {
        LinearLayout tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_with_counter, null);
        ((TextView)tab1.findViewById(R.id.text_tab)).setText(R.string.turnos);
        ((TextView)tab1.findViewById(R.id.text_tab)).setTextColor(getResources().getColor(R.color.colorAccent));
        tabLayout.getTabAt(0).setCustomView(tab1);

        LinearLayout tab2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ((TextView)tab2.findViewById(R.id.text_tab)).setText(R.string.profile);
        tabLayout.getTabAt(1).setCustomView(tab2);

    }



    public void createScheduler(final String day){
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
