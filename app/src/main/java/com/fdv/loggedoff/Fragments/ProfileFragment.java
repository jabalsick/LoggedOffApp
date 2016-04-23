package com.fdv.loggedoff.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.fdv.loggedoff.Activtys.PrincipalActivity;
import com.fdv.loggedoff.Activtys.UploadActivity;
import com.fdv.loggedoff.Model.Person;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Utils.CropCircleTransformation;
import com.fdv.loggedoff.Views.CustomTextView;
import com.firebase.client.Firebase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment  implements View.OnClickListener{
    private final static int SELECT_PICTURE = 1;
    private View rootView;
    private ImageView profilePicture;
    private static Person user;
    public static String DEFAULT_PHOTO ="DEFAULT";
    private AutoCompleteTextView name;
    private AutoCompleteTextView email;
    private Switch notif_switch;
    private ImageButton btnNewPhoto;
    private ImageButton btnGallery;
    private ImageButton btnRemovePhoto;
    private Firebase myProfileRef;
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
        email = (AutoCompleteTextView) rootView.findViewById(R.id.email);
        notif_switch = (Switch) rootView.findViewById(R.id.switch_notification);
        btnNewPhoto =(ImageButton) rootView.findViewById(R.id.btnNewPhoto);
        btnGallery=(ImageButton) rootView.findViewById(R.id.btnGallery);
        btnRemovePhoto =(ImageButton) rootView.findViewById(R.id.btnRemove);
        profileName =  (CustomTextView) rootView.findViewById(R.id.profile_user_name);
        user = ((PrincipalActivity ) getActivity()).getmUser();
        setupProfileImage();


        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(v.getContext(), UploadActivity.class);
                startActivity(i);
            }
        });
        name.setText(user.getName().toString());
        profileName.setText(user.getName().toString());
        setNameListener();

        email.setText(user.getEmail().toString());
        email.setEnabled(false);

        notif_switch.setTextOn(getResources().getString(R.string.on));
        notif_switch.setTextOff(getResources().getString(R.string.off));
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
                    user.setName(inputName);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                //nothing to do
            }
        });
    }
    public void setupProfileImage(){
        if(user.getProfile_photo()
                .equals(DEFAULT_PHOTO)){
            Glide.with(this)
                    .load(R.drawable.default_user_picture)
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into( profilePicture);
        }else{

            Glide.with(this)
                    .load(user.getProfile_photo())
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(profilePicture);



     }
    }

    @Override
    public void onClick(View v) {

        if(v == btnNewPhoto){
/*            Intent i = new Intent();
            i.setClass(this.getActivity(), UploadActivity.class);
            this.getActivity().startActivity(i);*/
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }

        if(v == btnGallery){

        }

        if(v == btnRemovePhoto){

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setupProfileImage();
    }


}
