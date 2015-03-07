package com.dbuggers.flare.httpHandler;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;

import com.dbuggers.flare.SignupFragment;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by benallen on 07/03/15.
 */
public class SignupRequest {
    final String finalTAG = "SignUp";
    private final SignupRequestListener mListener;

    public interface SignupRequestListener {
        public void onSignedUp();
    }

    public SignupRequest(String name, String bio, String number, TypedFile img, SignupRequestListener listener){
        String TAG;
        mListener = listener;
        RestAdapter restAdapter;
        HTTPService service;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(HTTPService.class);
        if(img == null){
            Log.v(finalTAG,"Provided Image is null");
        }
        Log.v(finalTAG, "Request init");

        service.listData(name, bio, number, img, new Callback<JsonModel>() {
            @Override
            public void success(JsonModel s, Response response) {
                Log.v(finalTAG,s.toString());
                if(s.error.equals("1")){
                    Log.v(finalTAG,"There was an error adding the user");
                    Log.v(finalTAG, s.errorMessage);
                } else if(s.error.equals("0")){
                    Log.v(finalTAG,"All ok");
                    Log.v(finalTAG, s.errorMessage);
                    mListener.onSignedUp();

                } else {
                    Log.v(finalTAG,"No Valid Response");
                    Log.v(finalTAG, s.errorMessage);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.v(finalTAG, error.toString());

            }
        });
    }

}
interface HTTPService {
    @Multipart
    @POST("/signup.php")
    void listData(@Query("name") String name,@Query("bio") String bio,@Query("number") String number,@Part("fileToUpload") TypedFile image, Callback<JsonModel> cb);
}
class JsonModel {
    public String errorMessage;
    public String error;

}
