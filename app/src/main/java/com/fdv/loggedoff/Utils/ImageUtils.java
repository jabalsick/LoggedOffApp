package com.fdv.loggedoff.Utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static String getEncoded64ImageStringFromBitmap(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);


        return imageEncoded;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    {

        //BEST QUALITY MATCH First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
    public static Bitmap evaluateRotation(Bitmap b,File file){
        ExifInterface exif= null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("loged")){
            return rotateBitmap(b,90);
        }else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
            return rotateBitmap(b,90);
        }else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
            return rotateBitmap(b,270);
        }else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
            return rotateBitmap(b, 180);
        }
        return b;
    }

    public static String getPhotoOrientation(File file){
      /*  portrait exif = 6
        landscape exif  = loged
        inverted landscape = 3
        inverted portrait =8*/

        ExifInterface exif= null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exif.getAttribute(ExifInterface.TAG_ORIENTATION);

    }

    public static Bitmap rotateBitmap(Bitmap b, float degrees) {

        int w = b.getWidth();
        int h = b.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degrees);

        return Bitmap.createBitmap(b, 0, 0, w, h, mtx, true);

    }

}
