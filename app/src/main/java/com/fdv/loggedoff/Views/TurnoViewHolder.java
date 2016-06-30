package com.fdv.loggedoff.Views;

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
public class TurnoViewHolder extends RecyclerView.ViewHolder{


    public TextView personNameView, personTurnView;
    public ImageView imageView;
    public LinearLayout linearCard;
    public LinearLayout linearButtons;
    public LinearLayout turnBody;
    public ImageButton btnAvisar;
    public ImageButton btnCancelar;

    public TurnoViewHolder(View itemView) {
        super(itemView);

        personNameView = (TextView) itemView.findViewById(R.id.person_name);
        personTurnView = (TextView) itemView.findViewById(R.id.person_turn);
        imageView = (ImageView) itemView.findViewById(R.id.person_photo);
        linearCard = (LinearLayout) itemView.findViewById(R.id.linear_header);
        turnBody = (LinearLayout) itemView.findViewById(R.id.turn_body);
        btnAvisar = (ImageButton) itemView.findViewById(R.id.btnAvisar);
        btnCancelar = (ImageButton) itemView.findViewById(R.id.btnCancelar);
        linearButtons = (LinearLayout)itemView.findViewById(R.id.linear_buttons);

    }

  /*  public void bindToTurno(Turno post) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);

        starView.setOnClickListener(starClickListener);
    }*/
}
