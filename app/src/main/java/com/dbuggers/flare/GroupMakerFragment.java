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
public class GroupMakerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creategroup, container, false);
        Log.v("Ah", "Fragment Loaded");
        return rootView;
    }
    private GroupMakerInterface groupMakerInterface;
    public interface GroupMakerInterface {
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