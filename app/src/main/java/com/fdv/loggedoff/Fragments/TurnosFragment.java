package com.fdv.loggedoff.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.fdv.loggedoff.Activtys.BaseActivity;
import com.fdv.loggedoff.Activtys.PrincipalActivity;
import com.fdv.loggedoff.Model.Turno;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Utils.CropCircleTransformation;
import com.fdv.loggedoff.Utils.DateUtils;
import com.fdv.loggedoff.Utils.NotificationUtils;
import com.fdv.loggedoff.Views.CustomDialog;
import com.fdv.loggedoff.Views.TurnoFreeViewHolder;
import com.fdv.loggedoff.Views.TurnoViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.konifar.fab_transformation.FabTransformation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TurnosFragment extends Fragment {

    private static final String EMPTY_STRING ="EMPTY" ;
    public static final String FREE_TURN_STRING = "LIBRE";
    public static final String MAIL_OFICINAS = "oficinas";
    private String dateNode;
    private View rootView;
    private Context mContext;
    private RecyclerView turnosFreeRecycler;
    private RecyclerView mainRecyclerView;
    private FloatingActionButton fab;
//    private LinearLayout allFreeMessage;
    private View overlay;
    private CardView sheet;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Turno,TurnoFreeViewHolder> mFreeAdapter;
    private FirebaseRecyclerAdapter<Turno,TurnoViewHolder> mTurnAdapter;
    static boolean hasTurnSelected = false;

    public TurnosFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_turnos, container, false);
        mContext = rootView.getContext();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        turnosFreeRecycler = (RecyclerView)rootView.findViewById(R.id.my_turn_recycler_view);
        mainRecyclerView = (RecyclerView)rootView.findViewById(R.id.main_recycler_view);
        mainRecyclerView.setHasFixedSize(true);
        turnosFreeRecycler.setHasFixedSize(true);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        overlay = rootView.findViewById(R.id.overlay);
        sheet = (CardView) rootView.findViewById(R.id.sheet);
//        allFreeMessage = (LinearLayout) rootView.findViewById(R.id.all_free);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    private void setupListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFab();
            }
        });
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOverlay();
            }
        });
    }

    public void onClickFab() {
        canSelectTurn();

    }

    public void onClickOverlay() {
        if (fab.getVisibility() != View.VISIBLE) {
            FabTransformation.with(fab).duration(200).setOverlay(overlay).transformFrom(sheet);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupFreeTurnList();
        setupAssignedTurnList();
    }

    private void setupAssignedTurnList() {
        dateNode = DateUtils.formatDate(new Date(),DateUtils.DAYMONTHYEAR);
        Query query = mDatabase.child(dateNode).orderByChild("asigned").equalTo(true);

        mTurnAdapter = new FirebaseRecyclerAdapter<Turno, TurnoViewHolder>(Turno.class, R.layout.turn_layout,
                TurnoViewHolder.class, query) {

            @Override
            protected void populateViewHolder(final TurnoViewHolder viewHolder, Turno turno, int position) {


                viewHolder.fullContainer.setTag(turno.getHora());
                viewHolder.personTurnView.setText(turno.getHora());
                viewHolder.personNameView.setText(turno.getNombre());


                if(turno.getProfile_photo().equals(EMPTY_STRING)){
                    Glide.with(TurnosFragment.this)
                            .load(R.drawable.ic_face_black_24dp)
                            .into(viewHolder.imageView);
                }else{
                    Glide.with(TurnosFragment.this)
                            .load(turno.getProfile_photo())
                            .bitmapTransform(new CropCircleTransformation(viewHolder.imageView.getContext()))
                            .into(viewHolder.imageView);
                }
            /*BUTTON CANCELAR

             viewHolder.btnCancelar.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       //  hideView(holder.linearCard);
                       hasTurnSelected = false;
                       freeTurn(viewHolder.fullContainer.getTag().toString(),viewHolder.personNameView.getText().toString());
                   }
               });*/

                viewHolder.btnAvisar.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
           /*       *//**//*     avisar(personNameView.getText().toString(), btnAvisar.getTag().toString(),
                               personTurnView.getText().toString());*//**//**/
                   }
               });

            }


        };

        mainRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        mainRecyclerView.setAdapter(mTurnAdapter);
    }

    private void setupFreeTurnList() {
        dateNode = DateUtils.formatDate(new Date(),DateUtils.DAYMONTHYEAR);
        Query query = mDatabase.child(dateNode).orderByChild("asigned").equalTo(false);

        mFreeAdapter = new FirebaseRecyclerAdapter<Turno, TurnoFreeViewHolder>(Turno.class, R.layout.free_turn_layout,
               TurnoFreeViewHolder.class, query) {

           @Override
           protected void populateViewHolder(final TurnoFreeViewHolder turnoFreeViewHolder, final Turno turno, final int position) {

               turnoFreeViewHolder.fullContainer.setTag(turno.getHora());
               turnoFreeViewHolder.personTurnView.setText(turno.getHora());
               turnoFreeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if(!hasTurnSelected) {
                           asignTurn(turnoFreeViewHolder.fullContainer.getTag().toString());
                           onClickOverlay();
                       }else{
                           showAlertMessage();
                       }
                   }
               });
           }
       };

        turnosFreeRecycler.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        turnosFreeRecycler.setAdapter(mFreeAdapter);
    }

  /*  private void setupLayoutForOccupedTurn(TurnoFreeViewHolder turnoFreeViewHolder, Turno turno) {
        turnoFreeViewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        turnoFreeViewHolder.imageView.setVisibility(View.VISIBLE);
        turnoFreeViewHolder.holderImageView.setVisibility(View.GONE);
        turnoFreeViewHolder.personTurnView.setTextColor(getResources().getColor(R.color.mb_white));
        turnoFreeViewHolder.personTurnView.setBackgroundColor(getResources().getColor(R.color.transparent_colorPrimaryDark));
        turnoFreeViewHolder.linearButtons.setBackgroundColor(getResources().getColor(R.color.transparent_colorPrimaryDark));
        turnoFreeViewHolder.personNameView.setText(turno.getNombre());
        turnoFreeViewHolder.personNameView.setTextColor(getResources().getColor(R.color.mb_white));

        if (BaseActivity.getSignInAccount().getUid().equals(turno.getUid())) {
            hasTurnSelected = true;
            turnoFreeViewHolder.btnCancelar.setVisibility(View.VISIBLE);
            turnoFreeViewHolder. btnAvisar.setVisibility(View.GONE);

        } else{
            turnoFreeViewHolder.btnAvisar.setTag(turno.getMail());
            turnoFreeViewHolder.btnAvisar.setVisibility(View.VISIBLE);
            turnoFreeViewHolder.btnCancelar.setVisibility(View.GONE);
        }
        //USUARIO LOGUEADO

        if(turno.getProfile_photo().equals(EMPTY_STRING)){
            Glide.with(TurnosFragment.this)
                    .load(R.drawable.chilling)
                    .into(turnoFreeViewHolder.imageView);
        }else{
            Glide.with(TurnosFragment.this)
                    .load(turno.getProfile_photo())
                    .into(turnoFreeViewHolder.imageView);
        }
        turnoFreeViewHolder.itemView.setOnClickListener(null);

    }*/


    public void freeTurn(String horario, String who){
        String hora = horario.replace(":","");
        dateNode = DateUtils.formatDate(new Date(),DateUtils.DAYMONTHYEAR);
        DatabaseReference hourRef =  mDatabase.child(dateNode).child(hora);
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("nombre", EMPTY_STRING);
        nombre.put("profile_photo",EMPTY_STRING);
        nombre.put("mail",EMPTY_STRING);
        nombre.put("uid", EMPTY_STRING);
        nombre.put("asigned",false);
        hourRef.updateChildren(nombre);

        ((PrincipalActivity) getActivity()).sendMail("diego.blajackis@fdvsolutions.com",who + " dejó libre el turno de " + horario," ");
    }
    public void asignTurn(String horario){
        String hora = horario.replace(":", "");
        dateNode = DateUtils.formatDate(new Date(),DateUtils.DAYMONTHYEAR);
        DatabaseReference hourRef = mDatabase.child(dateNode).child(hora);
        Map<String, Object> nombre = new HashMap<String, Object>();
        nombre.put("nombre", BaseActivity.getSignInAccount().getDisplayName());
        nombre.put("profile_photo",BaseActivity.getSignInAccount().getPhotoUrl() != null ?BaseActivity.getSignInAccount().getPhotoUrl().toString():"EMPTY");
        nombre.put("mail",BaseActivity.getSignInAccount().getEmail());
        nombre.put("uid", BaseActivity.getSignInAccount().getUid());
        nombre.put("asigned",true);
        hourRef.updateChildren(nombre);

       // NotificationUtils.scheduleNotification(NotificationUtils.getNotification("Recordatorio","En 5 minutos es tu turno de masajes"),30000);
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
                if(((TurnoFreeViewHolder) item).personNameView.getText().equals(FREE_TURN_STRING)){

                    NotificationUtils.getInstance(getActivity()).sendPersonalizedNotification(
                            "El turno de las " + ((TurnoFreeViewHolder) item).personTurnView.getText() + " está libre",BaseActivity.getSignInAccount().getDisplayName().toString() + "!");
                }
            }
        }
    }



    public void canSelectTurn(){
       String userId = BaseActivity.getSignInAccount().getUid();
        final String day = DateUtils.formatDate(new Date(),DateUtils.DAYMONTHYEAR);
        mDatabase.child(day).orderByChild("uid").equalTo(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                        hasTurnSelected = true;
                         showAlertMessage();
                        }else{
                        hasTurnSelected = false;
                            if (fab.getVisibility() == View.VISIBLE) {
                                FabTransformation.with(fab).duration(200).setOverlay(overlay).transformTo(sheet);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

/*   turnosFreeRecycler.addItemDecoration(new ItemDecorationAlbumColumns(
     getResources().getDimensionPixelSize(R.dimen.turn_list_spacing),2));*/
    /*    turnosFreeRecycler.setItemAnimator(new MyDefaultItemAnimator());*/
}
