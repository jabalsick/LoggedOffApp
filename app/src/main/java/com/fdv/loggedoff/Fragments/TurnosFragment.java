package com.fdv.loggedoff.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fdv.loggedoff.Activtys.BaseActivity;
import com.fdv.loggedoff.Model.Turno;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Utils.NotificationUtils;
import com.fdv.loggedoff.Views.CustomDialog;
import com.fdv.loggedoff.Views.ItemDecorationAlbumColumns;
import com.fdv.loggedoff.Views.TurnoViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TurnosFragment extends Fragment {

    private static final String EMPTY_STRING ="EMPTY" ;
    public static final String FREE_TURN_STRING = "LIBRE";
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

                if(turno.getNombre().equals(FREE_TURN_STRING)){
                    setupLayoutForFreeTurn(turnoViewHolder);
                }else{
                    setupLayoutForOccupedTurn(turnoViewHolder, turno);
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



        };
        turnosRecycler.setLayoutManager(new GridLayoutManager(mContext , 2));
        turnosRecycler.addItemDecoration(new ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen.turn_list_spacing),2));
        turnosRecycler.setAdapter(mAdapter);
        turnosRecycler.setItemAnimator(new MyDefaultItemAnimator());

    }

    private void setupLayoutForOccupedTurn(TurnoViewHolder turnoViewHolder, Turno turno) {
        turnoViewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        turnoViewHolder.imageView.setVisibility(View.VISIBLE);
        turnoViewHolder.holderImageView.setVisibility(View.GONE);
        turnoViewHolder.personTurnView.setTextColor(getResources().getColor(R.color.mb_white));
        turnoViewHolder.personTurnView.setBackgroundColor(getResources().getColor(R.color.transparent_colorPrimaryDark));
        turnoViewHolder.linearButtons.setBackgroundColor(getResources().getColor(R.color.transparent_colorPrimaryDark));
        turnoViewHolder.personNameView.setText(turno.getNombre());
        turnoViewHolder.personNameView.setTextColor(getResources().getColor(R.color.mb_white));

        if (BaseActivity.getSignInAccount().getUid().equals(turno.getUid())) {
            hasTurnSelected = true;
            turnoViewHolder.btnCancelar.setVisibility(View.VISIBLE);
            turnoViewHolder. btnAvisar.setVisibility(View.GONE);

        } else{
            turnoViewHolder.btnAvisar.setTag(turno.getMail());
            turnoViewHolder.btnAvisar.setVisibility(View.VISIBLE);
            turnoViewHolder.btnCancelar.setVisibility(View.GONE);
        }
        //USUARIO LOGUEADO

        if(turno.getProfile_photo().equals(EMPTY_STRING)){
            Glide.with(TurnosFragment.this)
                    .load(R.drawable.chilling)
                    .into(turnoViewHolder.imageView);
        }else{
            Glide.with(TurnosFragment.this)
                    .load(turno.getProfile_photo())
                    .into(turnoViewHolder.imageView);
        }
        turnoViewHolder.itemView.setOnClickListener(null);

    }

    private void setupLayoutForFreeTurn(final TurnoViewHolder turnoViewHolder) {
        turnoViewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.mb_white));

        turnoViewHolder.personNameView.setText(FREE_TURN_STRING);
        turnoViewHolder.personNameView.setTextColor(getResources().getColor(R.color.colorPrimary));
        turnoViewHolder.personTurnView.setTextColor(getResources().getColor(R.color.colorPrimary));
        turnoViewHolder.personTurnView.setBackgroundColor(getResources().getColor(R.color.mb_white));

        turnoViewHolder.linearButtons.setBackgroundColor(getResources().getColor(R.color.mb_white));

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
    }


    public void freeTurn(String horario, String who){
        String hora = horario.replace(":","");

        DatabaseReference hourRef =  mDatabase.child("horas").child(hora);
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("nombre", FREE_TURN_STRING);
        nombre.put("profile_photo",EMPTY_STRING);
        nombre.put("mail",EMPTY_STRING);
        nombre.put("uid", EMPTY_STRING);
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

        NotificationUtils.scheduleNotification(NotificationUtils.getNotification("Recordatorio","En 5 minutos es tu turno de masajes"),30000);
    }



    public void showAlertMessage(){
    CustomDialog dialog = new CustomDialog(mContext,
             R.string.already_choose_title
            ,R.string.already_choose_detail);
    dialog.show();
    }



    public class MyDefaultItemAnimator extends DefaultItemAnimator {

        @Override
        public void onChangeFinished(RecyclerView.ViewHolder item, boolean oldItem) {
            super.onChangeFinished(item, oldItem);
            if(!oldItem){
                if(((TurnoViewHolder) item).personNameView.getText().equals(FREE_TURN_STRING)){

                    NotificationUtils.getInstance(getActivity()).sendPersonalizedNotification(
                            "El turno de las " + ((TurnoViewHolder) item).personTurnView.getText() + " está libre",BaseActivity.getSignInAccount().getDisplayName().toString() + "!");
                }
            }
        }
    }
}
