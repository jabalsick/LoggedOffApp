package com.fdv.loggedoff.Activtys;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.fdv.loggedoff.Adapters.PagerAdapter;
import com.fdv.loggedoff.Model.Turno;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Views.CustomTextView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PrincipalActivity  extends BaseActivity  {
    TabLayout tabLayout;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.icon_app2);
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
                    updateUser();
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

    public void updateUser(){
        Map<String, Object> map = new HashMap<>();
        map.put("uid", mUser.getUid());
        map.put("name", mUser.getName());
        map.put("email", mUser.getEmail());
        map.put("profile_photo", mUser.getProfile_photo());
        map.put("provider", "password");
        map.put("isAdmin",mUser.getIsAdmin());
        userRef.child(mUser.getUid()).setValue(map);
    }

    public void resetScheduler(){
        mSchedulerFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot turnSnapshot: snapshot.getChildren()) {
                       Turno mTurno = turnSnapshot.getValue(Turno.class);
                       String hora = mTurno.getHora().replace(":", "");
                    Firebase hourRef = getSchedulerFirebase().child(hora);
                    Map<String, Object> nombre = new HashMap<String, Object>();
                    nombre.put("hora",mTurno.getHora());
                    nombre.put("nombre", "LIBRE");
                    nombre.put("profile_photo", "EMPTY");
                    nombre.put("mail","EMPTY");
                    nombre.put("uid", "EMPTY");
                    hourRef.updateChildren(nombre);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public Firebase getSchedulerFirebase(){
        return mSchedulerFirebase;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                onBackPressed();
                return true;

            case R.id.resetScheduler:
                 resetScheduler();
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
        if(mUser.getIsAdmin().equals("1")){
            menu.getItem(0).setVisible(true) ;
        }else{
            menu.getItem(0).setVisible(false);
        }
        return true;
    }
}
