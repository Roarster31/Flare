package com.dbuggers.flare.connections;

import android.util.Log;

import com.dbuggers.flare.helpers.MessageHasher;
import com.dbuggers.flare.models.MessageEntry;
import com.dbuggers.flare.models.MessagesPayload;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by benallen on 08/03/15.
 */
public class HTTPDevice extends Device {
    private static final String TAG = "HttpDevice";
    private List<MessageEntry> messageEntries;
    public HTTPDevice(DeviceInterface deviceInterface) {
        super(deviceInterface);
        retrieveAllData();

    }

    @Override
    public void requestMessages() {
        if(messageEntries != null){
            mDeviceInterface.onServerMessagesReceived(messageEntries, this);
        }
    }

    private void retrieveAllData() {
        Log.v(TAG, "Requesting Messages");
        MessagesHTTPService service;
        RestAdapter restAdapter;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(MessagesHTTPService.class);

        service.listData(mDeviceInterface.getClientGroupId(), new Callback<List<JSONMessage>>() {
            @Override
            public void success(List<JSONMessage> jsonMessages, Response response) {
                Log.v(TAG, "Response success!");
                Log.v(TAG, response.toString());
                if(jsonMessages != null && jsonMessages.size() > 0) {
                    Log.v(TAG, "Response:" + jsonMessages.toString());
                    messageEntries = new ArrayList<MessageEntry>();
                    for (JSONMessage j : jsonMessages) {
                        //2015-03-06 23:36:33
                        MessageEntry temp = new MessageEntry(Integer.valueOf(j.date), Integer.valueOf(j.uid), j.msg);
                        Log.v(TAG, temp.toString() + " ");
                        if (temp != null) {
                            messageEntries.add(temp);
                        }

                        try {
                            mDeviceInterface.onHashReceived(HTTPDevice.this, MessageHasher.hash(messageEntries));
                        } catch (IOException | NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v(TAG, "Error: " + error.toString());

            }
        });
    }

    @Override
    public void disconnect() {

    }


    @Override
    public void updateMessages(List<MessageEntry> list) {

        Log.v(TAG, "Sending Messages");
        SendHTTPMessage service;
        RestAdapter restAdapter;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(SendHTTPMessage.class);

        service.listData(list, new Callback<String>() {
            @Override
            public void success(String result, Response response) {
                Log.v(TAG, "Send success!");

            }

            @Override
            public void failure(RetrofitError error) {
                Log.v(TAG, "Error: " + error.toString());

            }
        });

    }
    public List<MessageEntry> getMessages(){
        return messageEntries;
    }
}
interface SendHTTPMessage {
    @GET("/addMessages.php")
    void listData(@Query("data") List<MessageEntry> messageEntries,Callback<String> cb);
}
interface MessagesHTTPService {
    @GET("/readMessages.php")
    void listData(@Query("gid") int gid,Callback<List<JSONMessage>> cb);
}
class JSONMessage {
    public String date;
    public String msg;
    public String uid;

}