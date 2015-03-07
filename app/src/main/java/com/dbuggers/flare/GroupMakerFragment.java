package com.dbuggers.flare;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by benallen on 07/03/15.
 */
public class GroupMakerFragment extends Fragment {
    private static final String TAG = "Interfaces";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creategroup, container, false);
        Log.v(TAG, "GroupMaker Fragment Loaded");
        final Button button = (Button) rootView.findViewById(R.id.nextBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(TAG, "Sign up button pressed");
                groupMakerInterface.onGroupJoin();

            }
        });
        return rootView;
    }
    private GroupMakerInterface groupMakerInterface;
    public interface GroupMakerInterface {
        public void onGroupJoin();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof GroupMakerInterface){
            groupMakerInterface = (GroupMakerInterface) getActivity();
        }else{
            throw new IllegalStateException(getActivity().getClass().getName()+" must implement GroupMakerInterface");
        }
    }

}