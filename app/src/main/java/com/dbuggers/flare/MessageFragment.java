package com.dbuggers.flare;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by benallen on 07/03/15.
 */
public class MessageFragment extends Fragment {
    private static final String TAG = "Interfaces";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        Log.v(TAG, "Message Fragment Loaded");
        return rootView;
    }
    private MessageInterface messageInterface;
    public interface MessageInterface {
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