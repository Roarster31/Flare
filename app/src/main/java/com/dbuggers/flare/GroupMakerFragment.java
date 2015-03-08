package com.dbuggers.flare;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.dbuggers.flare.httpHandler.GroupJoinRequest;
import com.dbuggers.flare.httpHandler.GroupMakeRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.glxn.qrgen.android.QRCode;

/**
 * Created by benallen on 07/03/15.
 */
public class GroupMakerFragment extends Fragment {
    private static final String TAG = "Interfaces";
    private long gId;
    private int uId;
    private final int shouldDisplayError = 1;
    private Button scanBtn;
    private Button continueBtn;
    DataManagerInterface mDataManagerInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creategroup, container, false);
        Log.v(TAG, "GroupMaker Fragment Loaded");


        scanBtn = (Button) rootView.findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //
                IntentIntegrator.forFragment(GroupMakerFragment.this).initiateScan();
//                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//                startActivityForResult(intent, 0);

            }
        });
        continueBtn = (Button) rootView.findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "GroupID: " + gId + " UserID: " + uId);
                mDataManagerInterface.getDataManager().setGroupId((int) gId);
                mDataManagerInterface.getDataManager().setUserId(uId);
                groupMakerInterface.onGroupJoin();

            }
        });
        long currentTime = System.currentTimeMillis();
        gId = currentTime;
        Bitmap bmp = QRCode.from(String.valueOf(currentTime)).bitmap();
        ImageView myImage = (ImageView) rootView.findViewById(R.id.qrCodeImg);
        myImage.setImageBitmap(bmp);

        mDataManagerInterface.getDataManager().setUserId(Integer.parseInt(readUserId()));

        return rootView;
    }

    private GroupMakerInterface groupMakerInterface;

    public interface GroupMakerInterface {
        public void onGroupJoin();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String contents = scanResult.getContents();
            Log.v(TAG, "barcode content: " + contents);
            gId = Long.parseLong(contents);
            mDataManagerInterface.getDataManager().setGroupId((int) gId);
            mDataManagerInterface.getDataManager().setUserId(uId);
            Log.v(TAG, "GroupID: " + gId + " UserID: " + uId);
            groupMakerInterface.onGroupJoin();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof DataManagerInterface) {
            mDataManagerInterface = (DataManagerInterface) activity;
        } else {
            throw new IllegalStateException(getActivity().getClass().getName() + " must implement DataManagerInterface");
        }

        if (activity instanceof GroupMakerInterface) {
            groupMakerInterface = (GroupMakerInterface) activity;
        } else {
            throw new IllegalStateException(getActivity().getClass().getName() + " must implement GroupMakerInterface");
        }
    }

    //    public void makeGroupHTTP(String gId, String uId){
//        GroupMakeRequest groupMaker = new GroupMakeRequest(
//                gId,
//                uId,
//                new GroupMakeRequest.GroupMakeListener() {
//                    @Override
//                    public void onGroupJoin() {
//                        //Log.v(TAG, "Group make so join it");
//                        groupMakerInterface.onGroupJoin();
//                    }
//
//                    @Override
//                    public void onGroupError() {
//                        //Toast toast = Toast.makeText(getActivity(), "Group Already Exists", Toast.LENGTH_SHORT);
//                        //toast.show();
//                    }
//                });
//    }
//    public void joinGroupHTTP(String gId, String uId){
//        GroupJoinRequest groupJoiner = new GroupJoinRequest(
//                gId,
//                uId,
//                new GroupJoinRequest.GroupJoinListener() {
//                    @Override
//                    public void onGroupJoin() {
//                        Log.v(TAG, "Group joined so join it");
//                        groupMakerInterface.onGroupJoin();
//                    }
//
//                    @Override
//                    public void onGroupError() {
//                        if(shouldDisplayError == 1) {
//                            //Toast toast = Toast.makeText(getActivity(), "There was an error joining you to the group", Toast.LENGTH_SHORT);
//                            //toast.show();
//                        }
//                    }
//                });
//    }
    private String readUserId() {
        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Get field values
        String userId = sharedPref.getString("userId", null);
        return userId;

    }

}
