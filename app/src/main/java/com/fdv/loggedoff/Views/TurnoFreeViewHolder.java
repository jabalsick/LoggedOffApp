package com.fdv.loggedoff.Views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fdv.loggedoff.Model.Turno;
import com.fdv.loggedoff.R;

/**
 * Created by Diego Blajackis on 29/6/2016.
 */
public class TurnoFreeViewHolder extends RecyclerView.ViewHolder{


    public TextView personNameView, personTurnView;
    public CardView fullContainer;


    public TurnoFreeViewHolder(View itemView) {
        super(itemView);
        fullContainer = (CardView) itemView.findViewById(R.id.full_container);
        personNameView = (TextView) itemView.findViewById(R.id.person_name);
        personTurnView = (TextView) itemView.findViewById(R.id.person_turn);

    }

}
