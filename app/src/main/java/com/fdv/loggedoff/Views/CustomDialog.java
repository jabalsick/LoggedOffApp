package com.fdv.loggedoff.Views;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fdv.loggedoff.R;


public class CustomDialog extends Dialog implements View.OnClickListener {

    private void createView(String titleMsg, String errorMsg) {

        setContentView(R.layout.custom_dialog);

        TextView text = (TextView) findViewById(R.id.text);
        text.setText(errorMsg);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(titleMsg);

        Button dialogButton = (Button) findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(this);

    }

    public CustomDialog(Context context, String titleMsg, String errorMsg) {
        super(context, R.style.FullHeightDialog);
        createView(titleMsg, errorMsg);
        getWindow().setLayout (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    public CustomDialog(Context context, int titleId, int contentId) {
        super(context, R.style.FullHeightDialog);
        createView(getContext().getString(titleId), getContext().getString(contentId));
        getWindow().setLayout (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }
}
