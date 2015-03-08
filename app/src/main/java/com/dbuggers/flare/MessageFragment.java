package com.dbuggers.flare;

import android.app.Activity;
import android.app.Fragment;
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

import com.dbuggers.flare.connections.DataManager;
import com.dbuggers.flare.models.MessageEntry;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by benallen on 07/03/15.
 */
public class MessageFragment extends Fragment implements DataManager.DataUpdateListener {
    private static final String TAG = "Interfaces";
    public static final String mTAG = "Messages";
    ListView mListView;
    StableArrayAdapter adapter;
    EditText inputTxt;
    RestAdapter restAdapter;
    HTTPService service;
    MessageFragmentInterface mMessageFragmentInterface;

    public interface MessageFragmentInterface {



        public DataManager getDataManager();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageFragmentInterface.getDataManager().addDataListener(this);
    }

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

        adapter = new StableArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mMessageFragmentInterface.getDataManager().getMessagesList());
        mListView.setAdapter(adapter);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://benallen.info/projects/friendBeacon")
                .build();

        service = restAdapter.create(HTTPService.class);

        return rootView;
    }

    @Override
    public void onDataUpdated() {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
    public void userSendMessage(){
        broadcastMessage();
        String userInput = inputTxt.getText().toString();
        inputTxt.setText("");
        mMessageFragmentInterface.getDataManager().sendMessage(userInput);

        adapter.notifyDataSetChanged();

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof MessageFragmentInterface){
            mMessageFragmentInterface = (MessageFragmentInterface) activity;
        }else{
            throw new IllegalStateException(getActivity().getClass().getName()+" must implement MessageFragmentInterface");
        }
    }

    private class StableArrayAdapter extends ArrayAdapter<MessageEntry> {


        public StableArrayAdapter(Context context, int textViewResourceId, List<MessageEntry> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final MessageEntry entry = getItem(position);

            View rowView;
            if(entry.getUserId() == mMessageFragmentInterface.getDataManager().getUserId()) {
                //rowView = inflater.inflate(R.layout.row_layout_user, parent, false);
            }else{
                //rowView = inflater.inflate(R.layout.row_layout_else, parent, false);

            }

            //TextView messageTextview = (TextView) rowView.findViewById(R.id.message);
          //  messageTextview.setText(entry.getMessage());

            return null;
        }
    }

    interface HTTPService {
        @GET("/main.php")
        void listData(@Query("u") String u, Callback<JsonModel> cb);
    }
    class JsonModel {
        public String r ;

    }

}
