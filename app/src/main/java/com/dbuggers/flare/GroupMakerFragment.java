package com.dbuggers.flare;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dbuggers.flare.httpHandler.GroupJoinRequest;
import com.dbuggers.flare.httpHandler.GroupMakeRequest;
import com.dbuggers.flare.httpHandler.SignupRequest;

import java.nio.charset.Charset;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by benallen on 07/03/15.
 */
public class GroupMakerFragment extends Fragment {
    private static final String TAG = "Interfaces";
    private String gId = "ajsfkjhd23412sf823";
    private String uId = "isjd12234foi932";
    private final int shouldDisplayError = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creategroup, container, false);
        Log.v(TAG, "GroupMaker Fragment Loaded");
        final Button button = (Button) rootView.findViewById(R.id.nextBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                groupMakerInterface.onGroupJoin();

            }
        });
        makeGroupHTTP(gId, uId);


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
    public void makeGroupHTTP(String gId, String uId){
        GroupMakeRequest groupMaker = new GroupMakeRequest(
                gId,
                uId,
                new GroupMakeRequest.GroupMakeListener() {
                    @Override
                    public void onGroupJoin() {
                        Log.v(TAG, "Group make so join it");
                        groupMakerInterface.onGroupJoin();
                    }

                    @Override
                    public void onGroupError() {
                        Toast toast = Toast.makeText(getActivity(), "Group Already Exists", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }
    public void joinGroupHTTP(String gId, String uId){
        GroupJoinRequest groupJoiner = new GroupJoinRequest(
                gId,
                uId,
                new GroupJoinRequest.GroupJoinListener() {
                    @Override
                    public void onGroupJoin() {
                        Log.v(TAG, "Group joined so join it");
                        groupMakerInterface.onGroupJoin();
                    }

                    @Override
                    public void onGroupError() {
                        if(shouldDisplayError == 1) {
                            Toast toast = Toast.makeText(getActivity(), "There was an error joining you to the group", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
    }

}
