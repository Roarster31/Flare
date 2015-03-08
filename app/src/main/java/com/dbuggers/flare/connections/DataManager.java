package com.dbuggers.flare.connections;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dbuggers.flare.helpers.MergeHelper;
import com.dbuggers.flare.helpers.MessageHasher;
import com.dbuggers.flare.models.MessageEntry;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by rory on 07/03/15.
 */
public class DataManager implements DeviceInterface {

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final String TAG = "DataManager";
    private final BluetoothAdapter mBluetoothAdapter;
    private final Activity mActivity;
    private final BluetoothManager mBluetoothManager;
    private int mGroupId;
    private List <MessageEntry> messages;
    private int userId;

    @Override
    public List<MessageEntry> getMessagesList() {
        return messages;
    }

    @Override
    public void onHashReceived(Device serverDevice, byte[] messagesHash) {
        try {
            byte[] localHash = MessageHasher.hash(messages);
            if(!Arrays.equals(localHash, messagesHash)){
                Log.e(TAG,"hashes not not equal: " + new String(localHash) + " != " + new String(messagesHash));
                serverDevice.requestMessages();
            }else{
                Log.i(TAG,"hashes are equal: " + new String(localHash) + " == " + new String(messagesHash));
                serverDevice.disconnect();
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServerMessagesReceived(final List<MessageEntry> messages, final Device serverDevice) {
        List<MessageEntry> list = MergeHelper.merge(this.messages, messages);
        onNewMessagesReceived(list);
        serverDevice.updateMessages(list);

        Log.d(TAG,"updating message list here and on server to "+getMessagesList().toString());
    }

    @Override
    public void onNewMessagesReceived(List<MessageEntry> messages) {
        this.messages = messages;
        Log.d(TAG,"updating messages to: "+messages.toString());
    }

    @Override
    public int getClientGroupId() {
        return mGroupId;
    }



    public void sendMessage(String message) {
        messages.add(new MessageEntry(System.currentTimeMillis(), userId, message));
    }


    public enum ConnectionType {BLUETOOTH, HTTP}

    private List<DiscoveryAdapter> mDiscoveryAdapterList;
    private List<BroadcastAdapter> mBroadcastAdapterList;


    public DataManager(Activity activity, ConnectionType... types) {

        userId = new Random().nextInt(10000);
        mActivity = activity;
        mDiscoveryAdapterList = new ArrayList<DiscoveryAdapter>();
        mBroadcastAdapterList = new ArrayList<BroadcastAdapter>();
        messages = new ArrayList<MessageEntry>();

        mBluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if(mBluetoothAdapter.getAddress().equals("EC:88:92:B9:86:76")){
            messages.add(new MessageEntry(1231301123, 56123, "heya"));
            messages.add(new MessageEntry(1231401123, 1231, "boo"));
            messages.add(new MessageEntry(1251301123, 56123, "ouch"));
        }else{
            messages.add(new MessageEntry(1231301123, 56123, "heya"));
        }

        for(ConnectionType type : types){
            if(type == ConnectionType.BLUETOOTH){

                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }else {
                    addBluetoothAdapters(activity, mBluetoothAdapter);
                }
            }else{
                mDiscoveryAdapterList.add(new HTTPDiscoveryAdapter(activity, this));
            }
        }
    }

    private void addBluetoothAdapters(Activity activity, BluetoothAdapter mBluetoothAdapter) {
        mDiscoveryAdapterList.add(new BluetoothDiscoveryAdapter(activity, mBluetoothAdapter, this));

        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
            mBroadcastAdapterList.add(new BluetoothBroadcastAdapter(activity, mBluetoothManager, this));
        }
    }

    public void startServices(){
        for(DiscoveryAdapter discoveryAdapter : mDiscoveryAdapterList){
            discoveryAdapter.scan();
        }

        for(BroadcastAdapter broadcastAdapter : mBroadcastAdapterList){
            broadcastAdapter.beginBroadcast();
        }
    }

    public void setGroupId(int id){

        mGroupId = id;
    }

    public boolean handleOnActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
            addBluetoothAdapters(mActivity, mBluetoothAdapter);
            return true;
        }
        return false;
    }

    public void kill() {

        for(DiscoveryAdapter discoveryAdapter : mDiscoveryAdapterList){
            discoveryAdapter.stopScan();
        }

        for(BroadcastAdapter broadcastAdapter : mBroadcastAdapterList){
            broadcastAdapter.stopBroadcast();
        }

    }


}
