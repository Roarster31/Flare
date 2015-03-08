package com.dbuggers.flare;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dbuggers.flare.httpHandler.SignupRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.mime.TypedFile;

/**
 * Created by benallen on 07/03/15.
 */
public class SignupFragment extends Fragment {
    private static final String TAG = "Interfaces";
    private static final int RESULT_OK = 1;
    private SignupInterface signupInterface;
    private EditText nameTxt;
    private EditText bioTxt;
    private EditText numTxt;
    private ImageView profileImg;
    final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private int hasSetImage = 0;
    public interface SignupInterface {
        public void onSignupClicked();
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.v(TAG, "Saving Image in: file:/"+ mCurrentPhotoPath);
        return image;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof SignupInterface){
            signupInterface = (SignupInterface) getActivity();
        }else{
            throw new IllegalStateException(getActivity().getClass().getName()+" must implement SignupInterface");
        }

    }

    public SignupFragment() {
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == -1 ) {
            // Image path
            Log.v(TAG, mCurrentPhotoPath);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);

            // Load in image
            profileImg.setImageURI(contentUri);

            hasSetImage = 1;
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.v(TAG, "Could not create image - no space?");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        clearSettingsFile();
        // Check to see if user already exists
        readSettingsFile();

        // Get the text from the form
        nameTxt = (EditText)rootView.findViewById(R.id.nameTxt);
        bioTxt = (EditText)rootView.findViewById(R.id.bioTxt);
        numTxt = (EditText)rootView.findViewById(R.id.numTxt);
        profileImg = (ImageView)rootView.findViewById(R.id.profile_image);

        // Sign up button
        final Button button = (Button) rootView.findViewById(R.id.signUpBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create toast
                Context context = getActivity().getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                // Perform action on click
                Log.v(TAG, "Sign up button pressed");

                // Get field values
                String nameTextValue = nameTxt.getText().toString();
                String bioTextValue = bioTxt.getText().toString();
                String numTextValue = numTxt.getText().toString();

                int testsOk = 1;

                // Check there is content
                if(nameTextValue.equals("")){
                    testsOk = 0;

                    // Create Toast
                    Toast toast = Toast.makeText(context, "Name Required", duration);
                    toast.show();
                }else if(bioTextValue.equals("")){
                    testsOk = 0;

                    // Create Toast
                    Toast toast = Toast.makeText(context, "Bio Required", duration);
                    toast.show();
                }else if(numTextValue.equals("")){
                    testsOk = 0;

                    // Create Toast
                    Toast toast = Toast.makeText(context, "Number Required", duration);
                    toast.show();
                }else if(hasSetImage == 0){
                    testsOk = 0;

                    // Create Toast
                    Toast toast = Toast.makeText(context, "Profile Picture Required", duration);
                    toast.show();
                }

                // If all tests ok
                if(testsOk == 1) {
                    // Convert image
                    File photo = new File(mCurrentPhotoPath);
                    TypedFile typedImage = new TypedFile("application/octet-stream", photo);
                    Log.v(TAG, "Converted Image");

                    // Create signup request
                    SignupRequest signUp = new SignupRequest(nameTxt.getText().toString(),
                            bioTxt.getText().toString(),
                            numTxt.getText().toString(),
                            typedImage,
                            new SignupRequest.SignupRequestListener() {
                                @Override
                                public void onSignedUp(String id) {
                                    // Save the details into a file
                                    createSettingsFile(id);
                                    // Make a transition
                                    signupInterface.onSignupClicked();
                                }

                                @Override
                                public void onInternetError() {
                                    int duration = Toast.LENGTH_SHORT;

                                    if(getActivity() != null) {
                                        Toast toast = Toast.makeText(getActivity(), "Error connecting", duration);
                                        toast.show();
                                    }

                                }

                                @Override
                                public void onUserExistsAlready() {
                                    int duration = Toast.LENGTH_SHORT;

                                    if(getActivity() != null) {
                                        Toast toast = Toast.makeText(getActivity(), "User Already Exists", duration);
                                        toast.show();
                                    }

                                }

                                @Override
                                public void onInvalidResponse() {
                                    int duration = Toast.LENGTH_SHORT;

                                        if(getActivity() != null) {
                                            Toast toast = Toast.makeText(getActivity(), "Invalid Response", duration);
                                            toast.show();
                                        }


                                }
                            });
                }


            }


        });

        // Image view press
        final ImageView v = (ImageView) rootView.findViewById(R.id.profile_image);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Log.v(TAG, "Image Pressed");
                        dispatchTakePictureIntent();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{

                        break;
                    }
                }
                return true;
            }
        });
        return rootView;

    }

    private void createSettingsFile(String id) {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Get field values
        String nameTextValue = nameTxt.getText().toString();
        String bioTextValue = bioTxt.getText().toString();
        String numTextValue = numTxt.getText().toString();

        editor.putString("nameTextValue", nameTextValue);
        editor.putString("bioTextValue", bioTextValue);
        editor.putString("numTextValue", numTextValue);
        editor.putString("userId", id);
        Log.v(TAG, "User ID = " + id);
        Log.v(TAG,"Updated settings file");
        editor.commit();
    }
    private void readSettingsFile() {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Get field values
        String nameText = sharedPref.getString("nameTextValue",null);

        if(nameText != null && !nameText.equals("")){

            Log.v(TAG, "Settings File has values will move frags: " + nameText + sharedPref.getString("userId",null));
            if(getActivity() instanceof SignupInterface){
                signupInterface = (SignupInterface) getActivity();
            }else{
                throw new IllegalStateException(getActivity().getClass().getName()+" must implement SignupInterface");
            }

            signupInterface.onSignupClicked();
        } else {
            Log.v(TAG, "Settings File is empty");
        }
    }
    public void clearSettingsFile(){
        Log.v("CLEAR", "Clearing Settings");
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
        Log.v(TAG, "Cleared Settings File");

    }
}