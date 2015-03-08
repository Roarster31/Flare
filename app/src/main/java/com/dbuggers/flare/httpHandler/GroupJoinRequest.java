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
public class GroupJoinRequest {
    private final GroupJoinListener mListener;
    private String TAG = "GroupJoinRequest";
    final private String initMessage = "[init_begin]";
    public interface GroupJoinListener {
        public void onGroupJoin();
        public void onGroupError();
    }
    public GroupJoinRequest(String gId, String uId, GroupJoinListener listener){
        mListener = listener;
        GroupJoinHTTPService service;
        RestAdapter restAdapter;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(GroupJoinHTTPService.class);

        service.listData(gId,uId,initMessage, new Callback<JsonModelResponseJoin>() {
            @Override
            public void success(JsonModelResponseJoin s, Response response) {
                if(s.error.equals("0")){
                    mListener.onGroupJoin();
                    Log.v(TAG, "Group added successfully");
                } else {
                    mListener.onGroupError();
                    Log.v(TAG,"Error making group");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v(TAG, "Error: " + error.toString());
                mListener.onGroupError();
                Log.v(TAG,"Error making group");
            }
        });
    }
}
interface GroupJoinHTTPService {
    @GET("/message.php")
    void listData(@Query("gid") String gid, @Query("uid") String uid, @Query("msg") String msg, Callback<JsonModelResponseJoin> cb);
}
class JsonModelResponseJoin {
    public String errorMessage;
    public String error;

}