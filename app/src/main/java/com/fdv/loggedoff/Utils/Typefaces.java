package com.fdv.loggedoff.Utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class Typefaces {

    // Typefaces available
    public enum TYPEFACE {
        Roboto_Black,
        Roboto_Thin,
        Roboto_Light, Roboto_Italic, Roboto_Medium;

    }

    private static final HashMap <TYPEFACE, Typeface> mTypefaces = new HashMap<TYPEFACE, Typeface>();

    public Typefaces(final Context context){

        mTypefaces.put(
                TYPEFACE.Roboto_Black,
                Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf")
        );
        mTypefaces.put(
                TYPEFACE.Roboto_Thin,
                Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf")
        );
        mTypefaces.put(
                TYPEFACE.Roboto_Light,
                Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf")
        );

        mTypefaces.put(
                TYPEFACE.Roboto_Italic,
                Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldItalic.ttf")
        );

        mTypefaces.put(
                TYPEFACE.Roboto_Medium,
                Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf")
        );

    }

    public Typeface get(final TYPEFACE typeface){
        return mTypefaces.get(typeface);
    }
}