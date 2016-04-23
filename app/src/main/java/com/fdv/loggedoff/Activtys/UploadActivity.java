package com.fdv.loggedoff.Activtys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fdv.loggedoff.LoggedOffApplication;
import com.fdv.loggedoff.R;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class UploadActivity extends BaseActivity {
    private final static int SELECT_PICTURE = 1;
    private final Activity current = this;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                setDefaultLayout();
                Uri selectedImageUri = data.getData();
               // L.d("Uploading file from URI: %s", selectedImageUri.getPath());
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        selectedImageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
             //   L.d("Uploading file: %s", filePath);
                startUpload(filePath);
            }
        }
    }

    private void setDefaultLayout() {
        setContentView(R.layout.activity_upload_photo);
        // Show the Up button in the action bar.
       //setupActionBar();
    }

    private void startUpload(String filePath) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... paths) {
                // Upload to cloudinary
                Cloudinary cloudinary = LoggedOffApplication.getInstance(current).getCloudinary();
                File file = new File(paths[0]);
                @SuppressWarnings("rawtypes")
                Map cloudinaryResult;
                try {
       // Cloudinary: Upload file using the retrieved signature and upload params
                    Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());


                    userRef.child(mUser.getUid()).child("profile_photo").setValue(uploadResult.get("url").toString());
                    mUser.setProfilePhoto(uploadResult.get("url").toString());
                  //  L.i("Uploaded file: %s", cloudinaryResult.toString());
                } catch (RuntimeException e) {
                   // L.e(e, "Error uploading file");
                    return "Error uploading file: " + e.toString();
                } catch (IOException e) {
                   // L.e(e, "Error uploading file");
                    return "Error uploading file: " + e.toString();
                }

                // update Firebase

                return null;
            }

            protected void onPostExecute(String error) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (error == null) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    new AlertDialog.Builder(current)
                            .setTitle("Error")
                            .setMessage(error)
                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }
                            })
                            .setCancelable(true)
                            .create().show();
                }
            }
        };
        dialog = ProgressDialog.show(this, "Uploading", "Uploading image");
        task.execute(filePath);
    }


}