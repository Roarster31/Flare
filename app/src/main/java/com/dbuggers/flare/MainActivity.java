package com.dbuggers.flare;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbuggers.flare.connections.DataManager;


public class MainActivity extends Activity {

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            final Button button = (Button) rootView.findViewById(R.id.signUpBtn);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    Log.v("Ah","Button Press");
                }
            });

            return rootView;
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class GroupMaker extends Fragment {

        public GroupMaker() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_creategroup, container, false);
            return rootView;
        }
    }
}
