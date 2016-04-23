package com.fdv.loggedoff.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fdv.loggedoff.Activtys.BaseActivity;
import com.fdv.loggedoff.Activtys.PrincipalActivity;
import com.fdv.loggedoff.Model.Turno;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Utils.CropCircleTransformation;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerViewAdapter;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TurnosFragment extends Fragment {

    private View rootView;
    private RecyclerView turnosRecycler;
    private FirebaseRecyclerViewAdapter<Turno,TurnoHolder> turnoAdapter;

    public TurnosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_turnos, container, false);
        turnosRecycler = (RecyclerView)rootView.findViewById(R.id.my_turn_recycler_view);

        turnoAdapter = new FirebaseRecyclerViewAdapter<Turno,TurnoHolder>(Turno.class,R.layout.turn_layout,TurnoHolder.class,
                ((PrincipalActivity)getActivity()).getSchedulerFirebase()) {
            @Override
            public void populateViewHolder(final TurnoHolder turnoHolder, Turno turno) {


                turnoHolder.linearCard.setTag(turno.getHora());

                turnoHolder.personTurnView.setText(turno.getHora());
                if(turno.getNombre().equals("LIBRE")){
                    //TURNO LIBRE
//                    if(hasAssigment()){
//                        turnoHolder.btnTakeTurn.setVisibility(View.GONE);
//                    }else{
                    turnoHolder.btnTakeTurn.setVisibility(View.VISIBLE);
                    turnoHolder.personNameView.setText("LIBRE");
                    turnoHolder.imageView.setVisibility(View.GONE);
                    turnoHolder.btnCancelar.setVisibility(View.GONE);
                    turnoHolder.btnAvisar.setVisibility(View.GONE);

                }else{

                    turnoHolder.imageView.setVisibility(View.VISIBLE);
                    if (((PrincipalActivity) getActivity()).getmUser().getUid().equals(turno.getUid())) {
                        //USUARIO LOGUEADO
                        turnoHolder.personNameView.setText(((PrincipalActivity) getActivity()).getmUser().getName());

                        if(((PrincipalActivity) getActivity()).getmUser().getProfile_photo().equals
                                (((PrincipalActivity) getActivity()).DEFAULT_PHOTO)){

                            Glide.with(TurnosFragment.this)
                                    .load(R.drawable.default_user_picture)
                                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                                    .into( turnoHolder.imageView);
                        }else{

                            Glide.with(TurnosFragment.this)
                                    .load(((PrincipalActivity) getActivity()).getmUser().getProfile_photo())
                                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                                    .into(  turnoHolder.imageView);
                        }


                        turnoHolder.btnCancelar.setVisibility(View.VISIBLE);
                        turnoHolder. btnAvisar.setVisibility(View.GONE);
                    }else{

                        turnoHolder.personNameView.setText(((PrincipalActivity) getActivity()).getAllAppUsers().
                                get(turno.getUid()).getName());
                        if(((PrincipalActivity) getActivity()).getAllAppUsers().
                                get(turno.getUid()).getProfile_photo().equals
                                (((PrincipalActivity) getActivity()).DEFAULT_PHOTO)){

                            Glide.with(TurnosFragment.this)
                                    .load(R.drawable.default_user_picture)
                                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                                    .into( turnoHolder.imageView);
                        }else{

                            Glide.with(TurnosFragment.this)
                                    .load(((PrincipalActivity) getActivity()).getAllAppUsers().
                                            get(turno.getUid()).getProfile_photo())
                                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                                    .into(  turnoHolder.imageView);
                        }

                        turnoHolder.btnAvisar.setTag(turno.getMail());
                        turnoHolder.btnAvisar.setVisibility(View.VISIBLE);
                        turnoHolder. btnCancelar.setVisibility(View.GONE);

                    }
                    turnoHolder.btnTakeTurn.setVisibility(View.GONE);

                }


                turnoHolder. btnTakeTurn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //revealView(holder.linearButtons);
                        asignTurn(turnoHolder.linearCard.getTag().toString());

                    }
                });

                turnoHolder.btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  hideView(holder.linearCard);
                        freeTurn(turnoHolder.linearCard.getTag().toString(),turnoHolder.personNameView.getText().toString());
                    }
                });

                turnoHolder.btnAvisar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                /*        avisar(personNameView.getText().toString(), btnAvisar.getTag().toString(),
                                personTurnView.getText().toString());*/
                    }
                });
            }
        };


        turnosRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        turnosRecycler.setAdapter(turnoAdapter);

        return rootView;
    }


 /*   public void avisar(String aQuienLlama,String email,String hora){
        super.sendMail(email, aQuienLlama + " : " + ((PrincipalActivity) getActivity()).getmUser().getName()
                + " te recuerda que a las "
                + hora + " es tu turno.", "");
    }*/

    public void freeTurn(String horario, String who){
        String hora = horario.replace(":","");

        Firebase hourRef =  ((PrincipalActivity)getActivity()).getSchedulerFirebase().child(hora);
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("nombre", "LIBRE");
        nombre.put("profile_photo", "EMPTY");
        nombre.put("mail","EMPTY");
        nombre.put("uid", "EMPTY");
        hourRef.updateChildren(nombre);


        //  turnoAdapter.notifyItemRangeChanged(0, turnoAdapter.getItemCount());
        // super.sendMail(mUser.getEmail(),who + " dejó libre el turno de " + horario," ");
    }
    public void asignTurn(String horario){
        String hora = horario.replace(":", "");
        Firebase hourRef = ((PrincipalActivity)getActivity()).getSchedulerFirebase().child(hora);
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("nombre", BaseActivity.getmUser().getName());
        nombre.put("profile_photo",((PrincipalActivity) getActivity()).getmUser().getProfile_photo());
        nombre.put("mail",((PrincipalActivity) getActivity()).getmUser().getEmail());
        nombre.put("uid", ((PrincipalActivity) getActivity()).getmUser().getUid());
        hourRef.updateChildren(nombre);
        //  turnoAdapter.notifyItemRangeChanged(0, turnoAdapter.getItemCount());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        turnoAdapter.cleanup();
    }

    public static class TurnoHolder extends RecyclerView.ViewHolder {
        TextView personNameView, personTurnView;
        ImageView imageView;
        LinearLayout linearCard;
        LinearLayout linearButtons;
        ImageButton btnAvisar;
        ImageButton btnCancelar;
        ImageButton btnTakeTurn;

        public TurnoHolder(View itemView) {
            super(itemView);

            personNameView = (TextView) itemView.findViewById(R.id.person_name);
            personTurnView = (TextView) itemView.findViewById(R.id.person_turn);
            imageView = (ImageView) itemView.findViewById(R.id.person_photo);
            linearCard = (LinearLayout) itemView.findViewById(R.id.linear_header);
            btnAvisar = (ImageButton) itemView.findViewById(R.id.btnAvisar);
            btnCancelar = (ImageButton) itemView.findViewById(R.id.btnCancelar);
            btnTakeTurn = (ImageButton) itemView.findViewById(R.id.btnTakeTurn);
            linearButtons = (LinearLayout)itemView.findViewById(R.id.linear_buttons);

        }
    }
}
