package com.dbuggers.flare;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import com.dbuggers.flare.connections.DataManager;
import com.dbuggers.flare.connections.Device;
import com.dbuggers.flare.connections.DeviceInterface;
import com.dbuggers.flare.connections.HTTPDevice;
import com.dbuggers.flare.helpers.MessageHasher;
import com.dbuggers.flare.models.MessageEntry;
import com.dbuggers.flare.models.MessagesPayload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SignupFragment.SignupInterface, GroupMakerFragment.GroupMakerInterface, MessageFragment.MessageFragmentInterface {

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
        HTTPDevice n = new HTTPDevice(new DeviceInterface() {
            @Override
            public void onHashReceived(Device serverDevice, byte[] messagesHash) {

            }

            @Override
            public void onServerMessagesReceived(List<MessageEntry> messages, Device serverDevice) {

            }

            @Override
            public void onNewMessagesReceived(List<MessageEntry> messages) {

            }

            @Override
            public int getClientGroupId() {
                return 0;
            }

            @Override
            public List<MessageEntry> getMessagesList() {
                return null;
            }
        }, "4543456");
        // Simulate a request
        n.requestMessages();

        // Simulate a send
        MessagesPayload payload = new MessagesPayload();
        payload.setGroupId(4543456);
        payload.setUserId(239842);
        List<MessageEntry> messages = new ArrayList<MessageEntry>();
        MessageEntry temp = new MessageEntry(4543456,34858345, "Hello World");
        messages.add(temp);
        payload.setMessages(messages);
        n.sendData(payload);

        // Simulate a hash check
        MessageHasher hashIt = new MessageHasher();
        try {
            hashIt.serializeMessageList(n.getMessages());

            // Do something with the hash
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public DataManager getDataManager() {
        return dataManager;
    }
}
