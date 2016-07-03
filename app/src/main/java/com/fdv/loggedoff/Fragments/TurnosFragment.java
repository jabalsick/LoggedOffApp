package com.fdv.loggedoff.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fdv.loggedoff.Activtys.BaseActivity;
import com.fdv.loggedoff.Activtys.PrincipalActivity;
import com.fdv.loggedoff.Model.Turno;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Utils.CropCircleTransformation;
import com.fdv.loggedoff.Views.CustomDialog;
import com.fdv.loggedoff.Views.TurnoViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TurnosFragment extends Fragment {

    private static final String DEFAULT_PHOTO ="EMPTY" ;
    private View rootView;
    private Context mContext;
    private RecyclerView turnosRecycler;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Turno,TurnoViewHolder> mAdapter;
    static boolean hasTurnSelected = false;
    public TurnosFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_turnos, container, false);
        mContext = rootView.getContext();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        turnosRecycler = (RecyclerView)rootView.findViewById(R.id.my_turn_recycler_view);
        turnosRecycler.setHasFixedSize(true);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Set up FirebaseRecyclerAdapter with the Query
        //Query turnosQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<Turno, TurnoViewHolder>(Turno.class, R.layout.turn_layout,
                TurnoViewHolder.class, mDatabase.child("horas")) {

            @Override
            protected void populateViewHolder(final TurnoViewHolder turnoViewHolder, final Turno turno, final int position) {

                turnoViewHolder.linearCard.setTag(turno.getHora());

                turnoViewHolder.personTurnView.setText(turno.getHora());

                if(turno.getNombre().equals("LIBRE")){
                    turnoViewHolder.personNameView.setText("LIBRE");
                    turnoViewHolder.personTurnView.setBackground(null);
                    turnoViewHolder.holderImageView.setVisibility(View.VISIBLE);
                    turnoViewHolder.imageView.setVisibility(View.GONE);
                    turnoViewHolder.btnCancelar.setVisibility(View.GONE);
                    turnoViewHolder.btnAvisar.setVisibility(View.GONE);
                    turnoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!hasTurnSelected) {
                                asignTurn(turnoViewHolder.linearCard.getTag().toString());
                            }else{
                                showAlertMessage();
                            }
                        }
                    });
                }else{
                    turnoViewHolder.imageView.setVisibility(View.VISIBLE);
                    turnoViewHolder.holderImageView.setVisibility(View.GONE);
                    turnoViewHolder.personTurnView.setBackgroundColor(getResources().getColor(R.color.transparent_colorPrimaryDark));
                    if (BaseActivity.getSignInAccount().getUid().equals(turno.getUid())) {
                        hasTurnSelected = true;
                        //USUARIO LOGUEADO
                        turnoViewHolder.personNameView.setText(BaseActivity.getSignInAccount().getDisplayName());
                        BaseActivity.getSignInAccount().getProviderData();
                        if(turno.getProfile_photo() == DEFAULT_PHOTO){
                            Glide.with(TurnosFragment.this)
                                    .load(R.drawable.default_user_picture)
                                    .into( turnoViewHolder.imageView);
                        }else{
                            Glide.with(TurnosFragment.this)
                                    .load(BaseActivity.getSignInAccount().getPhotoUrl())
                                    .into(  turnoViewHolder.imageView);
                        }


                        turnoViewHolder.btnCancelar.setVisibility(View.VISIBLE);
                        turnoViewHolder. btnAvisar.setVisibility(View.GONE);
                    }else{

                        turnoViewHolder.personNameView.setText(turno.getNombre());
                            Glide.with(TurnosFragment.this)
                                    .load(R.drawable.default_user_picture)
                                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                                    .into( turnoViewHolder.imageView);

                        turnoViewHolder.btnAvisar.setTag(turno.getMail());
                        turnoViewHolder.btnAvisar.setVisibility(View.VISIBLE);
                        turnoViewHolder. btnCancelar.setVisibility(View.GONE);

                    }
                }

                 turnoViewHolder.btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  hideView(holder.linearCard);
                        hasTurnSelected = false;
                        freeTurn(turnoViewHolder.linearCard.getTag().toString(),turnoViewHolder.personNameView.getText().toString());
                    }
                });

                turnoViewHolder.btnAvisar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                /*        avisar(personNameView.getText().toString(), btnAvisar.getTag().toString(),
                                personTurnView.getText().toString());*/
                    }
                });
            }


                // Determine if the current user has liked this post and set UI accordingly
             /*   if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }*/

                // Bind Post to ViewHolder, setting OnClickListener for the star button


        };
        turnosRecycler.setLayoutManager(new GridLayoutManager(mContext , 2));
        turnosRecycler.setAdapter(mAdapter);

    }


    public void freeTurn(String horario, String who){
        String hora = horario.replace(":","");

        DatabaseReference hourRef =  mDatabase.child("horas").child(hora);
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("nombre", "LIBRE");
        nombre.put("profile_photo", "EMPTY");
        nombre.put("mail","EMPTY");
        nombre.put("uid", "EMPTY");
        hourRef.updateChildren(nombre);

        // super.sendMail(mUser.getEmail(),who + " dejó libre el turno de " + horario," ");
    }
    public void asignTurn(String horario){
        String hora = horario.replace(":", "");
        DatabaseReference hourRef = mDatabase.child("horas").child(hora);
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("nombre", BaseActivity.getSignInAccount().getDisplayName());
        nombre.put("profile_photo",BaseActivity.getSignInAccount().getPhotoUrl() != null ?BaseActivity.getSignInAccount().getPhotoUrl().toString():"EMPTY");
        nombre.put("mail",BaseActivity.getSignInAccount().getEmail());
        nombre.put("uid", BaseActivity.getSignInAccount().getUid());
        hourRef.updateChildren(nombre);
    }


    public void showAlertMessage(){
    CustomDialog dialog = new CustomDialog(mContext,
             R.string.already_choose_title
            ,R.string.already_choose_detail);
    dialog.show();
    }
}
