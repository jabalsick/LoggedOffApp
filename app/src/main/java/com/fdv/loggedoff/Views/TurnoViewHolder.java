package com.fdv.loggedoff.Views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fdv.loggedoff.R;

/**
 * Created by Diego Blajackis on 29/6/2016.
 */
public class TurnoViewHolder extends RecyclerView.ViewHolder{


    public TextView personNameView, personTurnView;
    public ImageView imageView;
    public ImageButton btnAvisar;
    public RelativeLayout fullContainer;
   // public ImageButton btnCancelar;

    public TurnoViewHolder(View itemView) {
        super(itemView);
        fullContainer = (RelativeLayout) itemView.findViewById(R.id.full_container);
        personNameView = (TextView) itemView.findViewById(R.id.person_name);
        personTurnView = (TextView) itemView.findViewById(R.id.person_turn);
        imageView = (ImageView) itemView.findViewById(R.id.person_photo);
        btnAvisar = (ImageButton) itemView.findViewById(R.id.btnAvisar);
      //  btnCancelar = (ImageButton) itemView.findViewById(R.id.btnCancelar);

    }

}
