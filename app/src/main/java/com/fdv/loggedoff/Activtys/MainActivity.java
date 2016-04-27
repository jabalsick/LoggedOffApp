package com.fdv.loggedoff.Activtys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fdv.loggedoff.Model.Person;
import com.fdv.loggedoff.R;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.michael.easydialog.EasyDialog;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button signInButton;
    private Firebase mFirebaseRef;
    private TextView mSignup;
    /* Data from the authenticated user */
    private AuthData mAuthData;
    String temp_profile_photo = DEFAULT_PHOTO;
    static EasyDialog signUpDialog;
    private ImageView loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mUser = null;
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        initLayout();

    }

    private void initLayout(){
        mEmailView = (AutoCompleteTextView)findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSignup = (TextView) findViewById(R.id.link_signup);
        // mNameView = (EditText) findViewById(R.id.name);
        loading = (ImageView) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);


        signInButton = (Button) findViewById(R.id.sign_in_btn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*    Intent intent = new Intent(getBaseContext(), CreateUserActivity.class);
                nextScreen(intent);*/
                showDialog();

            }
        });


    }


    public void showDialog(){
       signUpDialog = new EasyDialog(MainActivity.this)
                .setLayoutResourceId(R.layout.sign_up_dialog)
                .setBackgroundColor(MainActivity.this.getResources().getColor(R.color.background_color_purple))
                .setLocationByAttachedView(mSignup)
                .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 1000, -800, 100, -50, 50, 0)
                .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 500, 0, -800)
                .setGravity(EasyDialog.GRAVITY_TOP)
                .setTouchOutsideDismiss(true)
                .setMatchParent(true)
                .setMarginLeftAndRight(24,24)
                .setOutsideColor(MainActivity.this.getResources().getColor(R.color.outside_color_pink))
                .show();
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
       final String name = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        String message = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
          //  mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for a valid email address.
     /*   if (!isValidFdvMail(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
            message = getString(R.string.logueo_fdv);
        }*/


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if(message!=null){
                Snackbar.make(signInButton, message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            focusView.requestFocus();

        } else {
            loading.setVisibility(View.VISIBLE);
            mFirebaseRef.authWithPassword(email, password, new AuthResultHandler("password") {
                @Override
                public void onAuthenticated(AuthData authData) {

                    // Authentication just completed successfully :)
                    Map<String, Object> map = new HashMap<>();
                    map.put("provider", authData.getProvider());
                    map.put("email", email);
                    map.put("photo", authData.getProviderData().get("profileImageURL"));
                    if(authData.getProviderData().containsKey("displayName")) {
                        map.put("displayName", authData.getProviderData().get("displayName").toString());
                    }

                    mFirebaseRef.child("users").child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            mUser = snapshot.getValue(Person.class);
                            loading.setVisibility(View.GONE);
                            goToScheduler();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Toast.makeText(MainActivity.this, "NO SE PUDO LOGUEAR", Toast.LENGTH_LONG).show();
                        }
                    });



                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    if(firebaseError.getCode() == FirebaseError.INVALID_PASSWORD ||
                            firebaseError.getCode() == FirebaseError.INVALID_CREDENTIALS ){
                        showErrorDialog("Mmmm...alguno de los datos no es correcto");
                    }
                    if(firebaseError.getCode() == FirebaseError.USER_DOES_NOT_EXIST){
                        showErrorDialog("El usuario no existe.");
                    }

                    ;
                }
            });

        }

    }


    private boolean isValidFdvMail(String email) {
        return email.contains("@fdvsolutions.com");
    }


    private void goToScheduler(){
        Intent intent = new Intent(getApplicationContext(), PrincipalActivity.class);
        nextScreen(intent);
     /*   Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        boolean dayOfMasagge = cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY;

        if(dayOfMasagge) {
            Intent intent = new Intent(getBaseContext(), ScheduleActivity.class);
            nextScreen(intent);
        }else{
            String name =new SimpleDateFormat("EEEE",new Locale("es")).format(cal.getTime());

            showErrorDialog("Hoy no es día de masajes, hoy es " + name);
        }*/
    }



    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {

            String name = null;

            name = authData.getUid();

        }
        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */
        supportInvalidateOptionsMenu();
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {

            setAuthenticatedUser(authData);

        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {

            showErrorDialog(firebaseError.toString());
        }

    }

    public void registerUser(View view){
        LinearLayout rootview =(LinearLayout) view.getParent();
        EditText eName = (EditText) rootview.findViewById(R.id.r_name);
        AutoCompleteTextView eMail = (AutoCompleteTextView)rootview.findViewById(R.id.r_email);
        EditText  ePass = (EditText) rootview.findViewById(R.id.r_password);
        String temp_Email =eMail.getText().toString();
        String temp_pass =ePass.getText().toString();

        createUser(eName.getText().toString(), temp_Email, temp_pass, temp_profile_photo);
    }

    private void createUser(final String nombre, final String email,String pass, final String temp_profile_photo) {
        mFirebaseRef.createUser(email, pass, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
             /*   Snackbar.make(signInButton, "Usuario creado con éxito!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();*/
                // Authentication just completed successfully :)
                String uid = result.get("uid").toString();

                Map<String, Object> map = new HashMap<>();
                map.put("uid", uid);
                map.put("name", nombre);
                map.put("email", email);
                map.put("provider", "password");
                map.put("profile_photo", temp_profile_photo);
                map.put("isAdmin",0);

                mFirebaseRef.child("users").child(uid).setValue(map);
                Toast.makeText(getBaseContext(), "Listo para loguearte!!!", Toast.LENGTH_LONG).show();
                mEmailView.setText(email);
                signUpDialog.dismiss();

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(getBaseContext(), "No se pudo crear usuario", Toast.LENGTH_LONG).show();
            }
        });
    }




}