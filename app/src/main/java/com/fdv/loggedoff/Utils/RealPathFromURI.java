package com.fdv.loggedoff.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class RealPathFromURI {
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(final Context context, final Uri uri){
        String filePath = "";
        final String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        final String[] ids = wholeID.split(":");
        final String id = ids[ids.length-1];

        final String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        final String sel = MediaStore.Images.Media._ID + "=?";

        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        final int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(final Context context, final Uri contentUri) {
        final String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        final CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        final Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            final int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(final Context context, final Uri contentUri){
        final String[] proj = { MediaStore.Images.Media.DATA };
        final Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        final int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
