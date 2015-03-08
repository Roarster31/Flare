package com.dbuggers.flare.connections;

import android.util.Log;

import com.dbuggers.flare.models.MessageEntry;
import com.dbuggers.flare.models.MessagesPayload;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by benallen on 08/03/15.
 */
public class HTTPDevice extends Device {
    private static final String TAG = "HttpDevice";
    private static String mGroupId;
    private List<MessageEntry> messageEntries;
    public HTTPDevice(DeviceInterface deviceInterface, String groupId) {
        super(deviceInterface);
        mGroupId = groupId;
    }

    @Override
    public void requestMessages() {

        Log.v(TAG, "Requesting Messages");
        MessagesHTTPService service;
        RestAdapter restAdapter;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(MessagesHTTPService.class);

        service.listData(mGroupId, new Callback<List<JSONMessage>>() {
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
                       /* SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        Date d = null;
                        try {
                            d = f.parse(j.date);
                            long milliseconds = d.getTime();
                            MessageEntry temp = new MessageEntry(milliseconds, Integer.valueOf(j.uid), j.msg);
                            Log.v(TAG, temp.toString() + " ");
                            if (temp != null) {
                                messageEntries.add(temp);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/

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
    public void fetchData(DeviceInterface deviceInterface) {

    }

    @Override
    public void sendData(MessagesPayload payload) {
        Log.v(TAG, "Sending Messages");
        SendHTTPMessage service;
        RestAdapter restAdapter;
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(SendHTTPMessage.class);
        String jsonMessages= "[";
        List<MessageEntry> messages = payload.getMessages();
        int i = 0;
        for (MessageEntry m : messages){
            if (i > 0){
                jsonMessages += ",";
            }
            //[{"mid":"1","date":"2015-03-06 23:36:33","msg":"Hello World","uid":"23894"},{"mid":"2","date":"2015-03-08 04:19:05","msg":"Hello!!","uid":"14234"}]
            jsonMessages += "{" + "\"date\":\"" + m.getTime() + "\", \"msg\":\"" + m.getMessage() + "\",\"uid\":\"" + m.getUserId() + "\"}";
            i++;
        }
        jsonMessages += "]";
        Log.v(TAG, "Msg: " + jsonMessages);
        service.listData(String.valueOf(payload.getGroupId()), jsonMessages, new Callback<String>() {
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

    @Override
    public void updateMessages(List<MessageEntry> list) {

    }
    public List<MessageEntry> getMessages(){
        return messageEntries;
    }
}
interface SendHTTPMessage {
    @GET("/addMessages.php")
    void listData(@Query("gid") String gid,@Query("data") String data,Callback<String> cb);
}
interface MessagesHTTPService {
    @GET("/readMessages.php")
    void listData(@Query("gid") String gid,Callback<List<JSONMessage>> cb);
}
class JSONMessage {
    public String date;
    public String msg;
    public String uid;

}