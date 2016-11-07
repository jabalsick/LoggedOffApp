package com.fdv.loggedoff.Fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fdv.loggedoff.Activtys.BaseActivity;
import com.fdv.loggedoff.R;
import com.fdv.loggedoff.Utils.CropCircleTransformation;
import com.fdv.loggedoff.Utils.RealPathFromURI;
import com.fdv.loggedoff.Views.CustomTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment  implements View.OnClickListener{
    private final static int SELECT_PICTURE = 1;
    private static final String TAG = ProfileFragment.class.getName();
    private View rootView;
    private ImageView profilePicture;
    private ProgressBar imageProgressBar;
    public static final String MY_PREFS_NAME = "LoggedOffProfile";
    public static final int MODE_PRIVATE = 0;
    private AutoCompleteTextView name;
  //  private Switch notif_switch;
    private ImageButton btnNewPhoto;
   // private ImageButton btnRemovePhoto;
    private Button btnSave;
    private String inputName;
    private CustomTextView profileName;
    public ProfileFragment() {
        // Required empty public constructor
    }
    private File photoFile;
    private static final int READ_REQUEST_CODE = 1337;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI();
        return rootView;
    }


    private void initUI(){
        profilePicture =(ImageView) rootView.findViewById(R.id.profile_picture);
     //   name = (AutoCompleteTextView) rootView.findViewById(R.id.name);
        imageProgressBar = (ProgressBar) rootView.findViewById(R.id.picture_loader);
        btnNewPhoto =(ImageButton) rootView.findViewById(R.id.btnNewPhoto);
        profileName =  (CustomTextView) rootView.findViewById(R.id.person_name);
        btnSave = (Button) rootView.findViewById(R.id.save);

        setupButtonListeners();
    }



    private void initProfileSettings() {

      //  SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
         profileName.setText(BaseActivity.getFirebaseUserSignIn().getDisplayName());
      //  profileName.setText(name.getText());
       //  notif_switch.setChecked(prefs.getBoolean("notification", true));

       // setNameListener();

        //   notif_switch.setTextOn(getResources().getString(R.string.on));
        //    notif_switch.setTextOff(getResources().getString(R.string.off));

        Glide.with(this)
                .load(BaseActivity.getFirebaseUserSignIn().getPhotoUrl())
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.no_photo)
                .placeholder(R.drawable.no_photo)
                .into(profilePicture);

    }

    private void setupButtonListeners() {
        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            profilePhotoOptions();
            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("name", profileName.getText().toString());
               // editor.putBoolean("notification", notif_switch.isChecked());
                editor.commit();
            }
        });
    }

    public void setNameListener(){
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputName = s.toString();
                if (inputName.length() > 0) {
                    profileName.setText(inputName);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                //nothing to do
            }
        });
    }


    @Override
    public void onClick(View v) {

        if(v == btnNewPhoto){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initProfileSettings();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                // coming from camera
                showImage(Uri.fromFile(photoFile), photoFile);

            } else if (requestCode == READ_REQUEST_CODE) {

                // coming from gallery
                final String realPath;
                if (Build.VERSION.SDK_INT < 19) {

                    realPath = RealPathFromURI.getRealPathFromURI_API11to18(getActivity(),
                            data.getData());

                    final File file = new File(realPath);
                    setImageFileToImageView(file, profilePicture);
                    saveProfilePicture(data.getData());

                } else {

                    final Uri uri;
                    if (data != null) {
                        uri = data.getData();

                        realPath = RealPathFromURI.getRealPathFromURI_API19(getActivity(),
                                data.getData());
                        if (realPath.isEmpty()) {
                            imageProgressBar.setVisibility(View.GONE);
                            return;
                        }
                        final File file = new File(realPath);
                        showImage(uri, file);
                    }

                }

            }
        }
    }



    private void showImage(final Uri uri, final File photoFile) {

        final AsyncTask<Uri, Void, Bitmap> imageLoadAsyncTask = new AsyncTask<Uri, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(final Uri... uris) {
                return getBitmapFromUri(uris[0]);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {


                Log.e(
                        "pre compress: ",
                        "Pre compress:" + Float.toString(bitmap.getByteCount() / 1024f / 1024f));
                Log.e(
                        "preCompress dimensions",
                        bitmap.getWidth() + " " + bitmap.getHeight());

                final Bitmap bitmapCompressed = compressFile(photoFile);

                if (bitmapCompressed != null) {
                    Log.e(
                            "pospos compress: ",
                            "Poscompress:" + Float.toString(
                                    bitmapCompressed.getByteCount() / 1024f / 1024f));
                    Log.e(
                            "posCompress dimensions",
                            bitmapCompressed.getWidth() + " " + bitmapCompressed.getHeight());
                }

                bitmap = rotateImageIfRequired(getActivity(), bitmapCompressed, uri, photoFile.getPath());
                profilePicture.setImageURI(uri);
                final File file = bitmapToFile(bitmap);
                saveProfilePicture(uri);
            }
        };
        imageLoadAsyncTask.execute(uri);
    }

    private File bitmapToFile(final Bitmap bitmap) {
        //create a file to write bitmap data
        final File file = new File(getActivity().getCacheDir(), "twinkl_temp");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        final byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        final FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);

            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private Bitmap compressFile(final File f) {
        try {
            // Decode image size
            final BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 150;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            final BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (final FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Create a Bitmap from the URI for that image and return it.
     *
     * @param uri the Uri for the image to return.
     */
    private Bitmap getBitmapFromUri(final Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getActivity().getContentResolver().openFileDescriptor(uri, "r");
            assert parcelFileDescriptor != null;
            final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            final Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();

            return image;
        } catch (final Exception e) {
            Log.e(TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }

    private void saveProfilePicture(final Uri uri) {
        // Create a reference to "mountains.jpg"
        StorageReference storageRef =((BaseActivity)getActivity()).storageRef.child(name.getText().toString() +".jpg");
        // Get the data from an ImageView as bytes
        profilePicture.setDrawingCacheEnabled(true);
        profilePicture.buildDrawingCache();
        Bitmap bitmap = profilePicture.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(),"UPLOAD ERROR " + exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                ((BaseActivity)getActivity()).updateUserPhotoOnDatabase(downloadUrl);
                Toast.makeText(getActivity(),"UPLOAD SUCCESS",Toast.LENGTH_LONG).show();
            }
        });


    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void performFileSearch() {

        // BEGIN_INCLUDE (use_open_document_intent)
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a file (as opposed to a list
        // of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers, it would be
        // "*/*".
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
        // END_INCLUDE (use_open_document_intent)
    }

    private void setImageFileToImageView(final File photoFile, final ImageView imageView) {
       Glide.with((getActivity().getApplicationContext()))
                .load(photoFile)
                .error(R.drawable.no_photo)
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .override(640, 400)
                .into(imageView);
    }

    private static Bitmap rotateImageIfRequired(final Context context,
                                                final Bitmap img,
                                                final Uri selectedImage, String path) {

        // Detect rotation
        int rotation = getRotation(context, selectedImage, path);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        } else {
            return img;
        }
    }


    private static int getRotation(final Context context, final Uri selectedImage, final String path) {
        int rotation = 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            try {
                final ExifInterface exif = new ExifInterface(new File(path).getAbsolutePath());
                final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = -90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = -180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = -270;
                        break;
                    default:
                        rotation = 0;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                context.getContentResolver().notifyChange(selectedImage, null);
                final File imageFile = new File(path);

                final ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                }
                Log.i("RotateImage", "Exif orientation: " + orientation);
                Log.i("RotateImage", "Rotate value: " + rotation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rotation;
    }

    private void profilePhotoOptions() {

       final Dialog dialog_card = new Dialog(getActivity());
        dialog_card.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_card.setContentView(R.layout.popup_profile_camera);
        dialog_card.getWindow().setGravity(Gravity.CENTER);
        dialog_card.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_card.getWindow().setDimAmount(0.6f);


        dialog_card.findViewById(R.id.popup_profile_upload_a_picture).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        dialog_card.dismiss();

                        if (getGalleryPermission()) {
                            if (Build.VERSION.SDK_INT < 19) {
                                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, READ_REQUEST_CODE);
                            } else {
                                performFileSearch();
                            }
                        }
                    }
                });

        dialog_card.findViewById(R.id.popup_profile_take_a_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog_card.dismiss();
                if (getCameraPermissions()) {
                    final Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        photoFile = createImageFile();
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePhotoIntent, 0);
                    } catch (final IOException ex) {
                        // Error occurred while creating the File
                        Log.e(getClass().getSimpleName(),
                                "Failed to load image from storage", ex);
                    }
                }
            }
        });

        dialog_card.show();
    }

    private boolean getCameraPermissions() {
        final int writeExternalPermission = ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        final int cameraPermission = ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.CAMERA);

        final int a = PackageManager.PERMISSION_DENIED;

        if (writeExternalPermission == a || cameraPermission == a) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You have to give Disconnect 'Storage' and 'Camera' permissions to open Camera.",
                    Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    private boolean getGalleryPermission() {
        final int readExternalPermission = ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (readExternalPermission == PackageManager.PERMISSION_DENIED) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    "You have to give Disconnect 'Storage' permission to open Gallery.",
                    Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    private static File createImageFile() throws IOException {
        final String imageFileName = "capturedImage";
        final File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

}
