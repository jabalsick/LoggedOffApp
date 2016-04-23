package com.fdv.loggedoff.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.fdv.loggedoff.Utils.Typefaces;


public class CustomTextView extends TextView {

    // Typefaces
    public static Typeface Roboto_Black = null;
    public static Typeface Roboto_Thin = null;
    public static Typeface Roboto_Light = null;
    public static Typeface Roboto_BoldItalic = null;
    public static Typeface Roboto_Medium = null;

    final Context mContext;

    public CustomTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        createTypefaces();
        init(attrs);
    }


    private void createTypefaces() {
        if (Roboto_Black == null) {
            final Typefaces typefaces = new Typefaces(mContext);
            Roboto_Black = typefaces.get(Typefaces.TYPEFACE.Roboto_Black);
            Roboto_Thin = typefaces.get(Typefaces.TYPEFACE.Roboto_Thin);
            Roboto_Light= typefaces.get(Typefaces.TYPEFACE.Roboto_Light);
            Roboto_BoldItalic =typefaces.get(Typefaces.TYPEFACE.Roboto_Italic);
            Roboto_Medium = typefaces.get(Typefaces.TYPEFACE.Roboto_Medium);
        }
    }

    private void init(final AttributeSet attrs) {
        if (attrs != null) {

            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                if (attrs.getAttributeName(i).equals("typeface")) {
                    final String typeface = attrs.getAttributeValue(i);

                    if (typeface != null) {

                        switch (typeface) {

                            case ("Roboto_Black"):
                                setTypeface(Roboto_Black);
                                break;
                            case ("Roboto_Thin"):
                                setTypeface(Roboto_Thin);
                                break;

                            case ("Roboto_Italic"):
                                setTypeface(Roboto_BoldItalic);
                                break;

                            case ("Roboto_Medium"):
                                setTypeface(Roboto_Medium);
                                break;

                            default:
                                setTypeface(Roboto_Thin);
                        }
                    }
                    break;
                }
            }
        }
    }
}
