package com.dbuggers.flare;

import android.app.Fragment;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedByteArray;

/**
 * Created by benallen on 07/03/15.
 */
public class MessageFragment extends Fragment {
    private static final String TAG = "Interfaces";
    public static final String mTAG = "Messages";
    ListView mListView;
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    EditText inputTxt;
    RestAdapter restAdapter;
    HTTPService service;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        Log.v(TAG, "Message Fragment Loaded");

        final Button button = (Button) rootView.findViewById(R.id.sendBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(mTAG, "Message Being Sent");
                userSendMessage();
            }
        });
        inputTxt = (EditText)rootView.findViewById(R.id.inputTxt);
        mListView = (ListView) rootView.findViewById(R.id.list);

        adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);
        mListView.setAdapter(adapter);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(HTTPService.class);

        return rootView;
    }
    private MessageInterface messageInterface;
    public interface MessageInterface {
    }
    public void addItems(String content) {
        listItems.add(content);
        Log.v(mTAG,"Added item");
        adapter.notifyDataSetChanged();
    }
    public void userSendMessage(){
        broadcastMessage();
        String userInput = inputTxt.getText().toString();
        inputTxt.setText("");
        addItems(userInput);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputTxt.getWindowToken(), 0);

    }
    public void broadcastMessage(){
        // Internet

        service.listData("octocat", new Callback<JsonModel>() {
            @Override
            public void success(JsonModel s, Response response) {
                Log.v("Response", s.r.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("Response", error.toString());

            }
        });
        // Bluetooth
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof MessageInterface){
            messageInterface = (MessageInterface) getActivity();
        }else{
            throw new IllegalStateException(getActivity().getClass().getName()+" must implement MessageInterface");
        }
    }

}
interface HTTPService {
    @GET("/main.php")
    void listData(@Query("u") String u, Callback<JsonModel> cb);
}
class JsonModel {
    public String r ;

}