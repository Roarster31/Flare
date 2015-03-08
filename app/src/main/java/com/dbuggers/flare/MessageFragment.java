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

/**
 * Created by benallen on 07/03/15.
 */
public class MessageFragment extends Fragment implements DataManager.DataUpdateListener {
    private static final String TAG = "Interfaces";
    public static final String mTAG = "Messages";
    ListView mListView;
    StableArrayAdapter adapter;
    EditText inputTxt;
    DataManagerInterface mDataManagerInterface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataManagerInterface.getDataManager().addDataListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        Log.v(TAG, "Message Fragment Loaded");

        // Send button
        final Button button = (Button) rootView.findViewById(R.id.sendBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(mTAG, "Message Being Sent");
                userSendMessage();
            }
        });

        // At Bar Button
        final Button buttonBar = (Button) rootView.findViewById(R.id.barBtn);
        buttonBar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(mTAG, "Message Being Sent");
                userSendCustomMessage("I'm at the bar!");
            }
        });
        // Outside Button
        final Button buttonOutside = (Button) rootView.findViewById(R.id.outsideBtn);
        buttonOutside.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(mTAG, "Message Being Sent");
                userSendCustomMessage("Lets leave");
            }
        });
        // Help Button
        final Button buttonHelp = (Button) rootView.findViewById(R.id.helpBtn);
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(mTAG, "Message Being Sent");
                userSendCustomMessage("I need help! Come find me!");
            }
        });
        // Toilets Button
        final Button buttonToilet = (Button) rootView.findViewById(R.id.toiletBtn);
        buttonToilet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(mTAG, "Message Being Sent");
                userSendCustomMessage("I've gone to the loo");
            }
        });
        // Toilets Button
        final Button buttonKebab = (Button) rootView.findViewById(R.id.kebabBtn);
        buttonKebab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(mTAG, "Message Being Sent");
                userSendCustomMessage("I've gone to the kebab shop");
            }
        });

        inputTxt = (EditText)rootView.findViewById(R.id.inputTxt);
        mListView = (ListView) rootView.findViewById(R.id.list);

        adapter = new StableArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mDataManagerInterface.getDataManager().getMessagesList());
        mListView.setAdapter(adapter);

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
        String userInput = inputTxt.getText().toString();
        userSendCustomMessage(userInput);
    }
    public void userSendCustomMessage(String userInput){
        inputTxt.setText("");
        mDataManagerInterface.getDataManager().sendMessage(userInput);
        adapter.notifyDataSetChanged();
        scrollMyListViewToBottom();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputTxt.getWindowToken(), 0);

    }
    private void scrollMyListViewToBottom() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(adapter.getCount() - 1);
            }
        });
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if(activity instanceof DataManagerInterface){
            mDataManagerInterface = (DataManagerInterface) activity;
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
            if(entry.getUserId() == mDataManagerInterface.getDataManager().getUserId()) {
                rowView = inflater.inflate(R.layout.row_layout_user, parent, false);
            }else{
                rowView = inflater.inflate(R.layout.row_layout_else, parent, false);

            }

            TextView messageTextview = (TextView) rowView.findViewById(R.id.message);
            messageTextview.setText(entry.getMessage());

            return rowView;
        }
    }

}
