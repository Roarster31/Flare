package com.dbuggers.flare.httpHandler;

import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by benallen on 08/03/15.
 */
public class GroupMakeRequest {
    private final GroupMakeListener mListener;
    private String TAG = "GroupMakeRequest";

    public interface GroupMakeListener {
        public void onGroupJoin();
        public void onGroupError();
    }
    public GroupMakeRequest(String gId, String uId, GroupMakeListener listener){
        mListener = listener;
        GroupHTTPService service;
        RestAdapter restAdapter;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(GroupHTTPService.class);

        service.listData(gId,uId, new Callback<JsonModelResponse>() {
            @Override
            public void success(JsonModelResponse s, Response response) {
                Log.v(TAG,s.error);
                Log.v(TAG,s.errorMessage);
                Log.v(TAG,"Error making group");
                if(s.error.equals("0")){
                    mListener.onGroupJoin();
                    Log.v(TAG, "Group added successfully");
                } else {
                    mListener.onGroupError();
                    Log.v(TAG,s.errorMessage);
                    Log.v(TAG,"Error making group");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mListener.onGroupError();
                Log.v(TAG,error.toString());
                Log.v(TAG,"Error making group");
            }
        });
    }
}
interface GroupHTTPService {
    @GET("/group.php")
    void listData(@Query("gid") String gid, @Query("uid") String uid, Callback<JsonModelResponse> cb);
}
class JsonModelResponse {
    public String errorMessage;
    public String error;

}