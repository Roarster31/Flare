package com.dbuggers.flare;

import android.app.Activity;
import android.app.Fragment;
<<<<<<< HEAD
import android.app.FragmentTransaction;
=======
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
>>>>>>> bluetooth
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
=======
import android.widget.Button;
>>>>>>> bluetooth

import com.dbuggers.flare.connections.DataManager;


public class MainActivity extends Activity implements SignupFragment.SignupInterface, GroupMakerFragment.GroupMakerInterface, MessageFragment.MessageInterface {

<<<<<<< HEAD
    private static final String TAG = "Interfaces";
=======
    private DataManager dataManager;

>>>>>>> bluetooth
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
