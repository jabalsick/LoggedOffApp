package com.fdv.loggedoff.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.fdv.loggedoff.Activtys.BaseActivity;
import com.fdv.loggedoff.Activtys.PrincipalActivity;
import com.fdv.loggedoff.Model.Person;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Utils.CropCircleTransformation;
import com.fdv.loggedoff.Views.CustomTextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment  implements View.OnClickListener{
    private final static int SELECT_PICTURE = 1;
    private View rootView;
    private ImageView profilePicture;
    private static Person user;
    public static String DEFAULT_PHOTO ="DEFAULT";
    public static final String MY_PREFS_NAME = "LoggedOffProfile";
    public static final int MODE_PRIVATE = 0;
    private AutoCompleteTextView name;
    private Switch notif_switch;
    private ImageButton btnNewPhoto;
    private ImageButton btnGallery;
    private ImageButton btnRemovePhoto;
    private Button btnSave;
    private String inputName;
    private CustomTextView profileName;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI();
        return rootView;
    }


    private void initUI(){
        profilePicture =(ImageView) rootView.findViewById(R.id.profile_picture);
        name = (AutoCompleteTextView) rootView.findViewById(R.id.name);
        notif_switch = (Switch) rootView.findViewById(R.id.switch_notification);
        btnNewPhoto =(ImageButton) rootView.findViewById(R.id.btnNewPhoto);
        btnRemovePhoto =(ImageButton) rootView.findViewById(R.id.btnRemove);
        profileName =  (CustomTextView) rootView.findViewById(R.id.profile_user_name);
        btnSave = (Button) rootView.findViewById(R.id.save);

        setupButtonListeners();
    }



    private void initProfileSettings() {

        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
         name.setText(prefs.getString("name", BaseActivity.getFirebaseUserSignIn().getDisplayName()));
         profileName.setText(name.getText());
         notif_switch.setChecked(prefs.getBoolean("notification", true));

        setNameListener();

        notif_switch.setTextOn(getResources().getString(R.string.on));
        notif_switch.setTextOff(getResources().getString(R.string.off));

        Glide.with(this)
                .load(BaseActivity.getFirebaseUserSignIn().getPhotoUrl())
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into( profilePicture);

    }

    private void setupButtonListeners() {
        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", profileName.getText().toString() );
                editor.putBoolean("notification", notif_switch.isChecked());
                editor.commit();
            }
        });
    }

    public void setNameListener(){
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputName = s.toString();
                if (inputName.length() > 0) {
                    profileName.setText(inputName);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                //nothing to do
            }
        });
    }


    @Override
    public void onClick(View v) {

        if(v == btnNewPhoto){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }

        if(v == btnGallery){

        }

        if(v == btnRemovePhoto){
            Glide.with(this)
                    .load(R.drawable.default_user_picture)
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into( profilePicture);
            user.setProfilePhoto(DEFAULT_PHOTO);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initProfileSettings();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
