package com.dbuggers.flare;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
<<<<<<< HEAD
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
=======
>>>>>>> 2d78352b4445bdf639844d1602ae2667b104f94e
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
<<<<<<< HEAD
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
=======

import com.dbuggers.flare.connections.DataManager;
>>>>>>> 2d78352b4445bdf639844d1602ae2667b104f94e


public class MainActivity extends Activity implements SignupFragment.SignupInterface, GroupMakerFragment.GroupMakerInterface, MessageFragment.MessageInterface {
    private NfcAdapter mNfcAdapter;
    private static final String TAG = "Interfaces";
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SignupFragment())
                    .commit();
        }


        dataManager = new DataManager(this, DataManager.ConnectionType.BLUETOOTH);
        dataManager.setGroupId(4543456);

        dataManager.startServices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dataManager.handleOnActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataManager.kill();
    }

    @Override
    public void onSignupClicked() {
        Log.v("ah", "Signing up user");
        /*getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, new GroupMakerFragment())
                .commit();*/
        getFragmentManager().beginTransaction()
                .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE )
                .replace(R.id.container, new GroupMakerFragment())
                .commit();
    }
    @Override
    public void onGroupJoin() {
        Log.v(TAG, "The user has either created or found a group");
        /*getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, new GroupMakerFragment())
                .commit();*/
        getFragmentManager().beginTransaction()
                .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE )
                .replace(R.id.container, new MessageFragment())
                .commit();
    }
}
