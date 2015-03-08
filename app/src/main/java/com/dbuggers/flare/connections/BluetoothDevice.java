package com.dbuggers.flare.connections;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.dbuggers.flare.connections.filetransfer.BluetoothFileClient;
import com.dbuggers.flare.connections.filetransfer.BluetoothSocketWorker;
import com.dbuggers.flare.connections.filetransfer.FileTransferInterface;
import com.dbuggers.flare.helpers.MessageHasher;
import com.dbuggers.flare.models.MessageEntry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rory on 07/03/15.
 */
public class BluetoothDevice extends Device {


    private static final String TAG = "BluetoothDevice";
    private final BluetoothDiscoveryListener mDiscoveryListener;
    private BluetoothGatt mCurrentBluetoothSession;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean connected;


    private String realBluetoothMacAddress;

    private byte[] mMessageTransferMethod;

    public BluetoothDevice(DeviceInterface deviceInterface, BluetoothDiscoveryListener discoveryListener) {
        super(deviceInterface);
        mDiscoveryListener = discoveryListener;
    }

    @Override
    public void updateMessages(List<MessageEntry> list) {

        BluetoothGattCharacteristic characteristic = mCurrentBluetoothSession
                .getService(BluetoothBroadcastAdapter.SERVICE_UUID)
                .getCharacteristic(BluetoothBroadcastAdapter.CHARACTERISTIC_SEND_MESSAGELIST_UUID);

        mMessageTransferMethod = BluetoothBroadcastAdapter.CLIENT_SEND_MESSAGELIST_KEY;
        characteristic.setValue(mMessageTransferMethod);

        mCurrentBluetoothSession.writeCharacteristic(characteristic);

        Log.d(TAG, "requesting file server start");

    }

    public void connect(Context context, BluetoothAdapter bluetoothAdapter, android.bluetooth.BluetoothDevice device) {
        mBluetoothAdapter = bluetoothAdapter;
        Log.d(TAG,"connect " + device.getAddress());

        mCurrentBluetoothSession = device.connectGatt(context, false, mCallback);

        mCurrentBluetoothSession.discoverServices();
    }

    @Override
    public void requestMessages() {

        BluetoothGattCharacteristic characteristic = mCurrentBluetoothSession
                .getService(BluetoothBroadcastAdapter.SERVICE_UUID)
                .getCharacteristic(BluetoothBroadcastAdapter.CHARACTERISTIC_SEND_MESSAGELIST_UUID);

        mMessageTransferMethod = BluetoothBroadcastAdapter.SERVER_SEND_MESSAGELIST_KEY;
        characteristic.setValue(mMessageTransferMethod);

        mCurrentBluetoothSession.writeCharacteristic(characteristic);

        Log.d(TAG, "requesting file server start");
    }

    @Override
    public void disconnect() {
        Log.e(TAG,"disconnecting");
        if(mCurrentBluetoothSession != null){
            mCurrentBluetoothSession.close();
            mCurrentBluetoothSession.disconnect();
            mCurrentBluetoothSession = null;
        }
        mDiscoveryListener.onDisconnected();
    }
    private BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG,"onConnectionStateChange status: "+status+" newState: "+newState);
            if(newState == BluetoothProfile.STATE_DISCONNECTED){
                connected = false;
                Log.i(TAG,"disconnected");
                mDiscoveryListener.onDisconnected();
            }else if(newState == BluetoothProfile.STATE_CONNECTING){
                Log.i(TAG,"connecting");
            }else if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.i(TAG,"connected");
                connected = true;
                mCurrentBluetoothSession.discoverServices();
            }else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                Log.i(TAG,"disconnecting");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered status: " + status);
            mCurrentBluetoothSession = gatt;
            requestDeviceGroupId();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead status: " + status + " characteristic: " + characteristic.toString());
            mCurrentBluetoothSession = gatt;
            if(characteristic.getUuid().equals(BluetoothBroadcastAdapter.CHARACTERISTIC_GROUP_ID_UUID)){
                int remoteGroupId = ByteBuffer.wrap(characteristic.getValue()).getInt();

                Log.d(TAG,"group id of server is "+remoteGroupId);

                if(remoteGroupId == mDeviceInterface.getClientGroupId()){
                    Log.d(TAG,"going on to request server messages hash");
                    requestDeviceMessagesHash();
                }else{
                    disconnect();
                }

            } else if(characteristic.getUuid().equals(BluetoothBroadcastAdapter.CHARACTERISTIC_MESSAGE_HASH_UUID)){
                byte[] messagesHash = characteristic.getValue();

                Log.d(TAG, "messages hash of server is " + new String(messagesHash));

                mDeviceInterface.onHashReceived(BluetoothDevice.this, messagesHash);
            } else if(characteristic.getUuid().equals(BluetoothBroadcastAdapter.CHARACTERISTIC_MAC_ADDRESS_UUID)){
                realBluetoothMacAddress = new String(characteristic.getValue());

                startFileServer(Arrays.equals(mMessageTransferMethod, BluetoothBroadcastAdapter.SERVER_SEND_MESSAGELIST_KEY));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicWrite characteristic: " + characteristic.toString());
            mCurrentBluetoothSession = gatt;
            if(characteristic.getUuid().equals(BluetoothBroadcastAdapter.CHARACTERISTIC_SEND_MESSAGELIST_UUID)){
                if(realBluetoothMacAddress != null){
                    startFileServer(Arrays.equals(mMessageTransferMethod, BluetoothBroadcastAdapter.SERVER_SEND_MESSAGELIST_KEY));
                }else {
                    requestMacAddress();
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged characteristic: " + characteristic.toString());

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorRead descriptor: " + descriptor.toString());

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite descriptor: " + descriptor.toString());
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    private void requestDeviceMessagesHash(){

        BluetoothGattCharacteristic characteristic = mCurrentBluetoothSession
                .getService(BluetoothBroadcastAdapter.SERVICE_UUID)
                .getCharacteristic(BluetoothBroadcastAdapter.CHARACTERISTIC_MESSAGE_HASH_UUID);

        mCurrentBluetoothSession.readCharacteristic(characteristic);

    }

    private void requestDeviceGroupId() {

        BluetoothGattCharacteristic characteristic = mCurrentBluetoothSession
                .getService(BluetoothBroadcastAdapter.SERVICE_UUID)
                .getCharacteristic(BluetoothBroadcastAdapter.CHARACTERISTIC_GROUP_ID_UUID);

        mCurrentBluetoothSession.readCharacteristic(characteristic);
    }

    private void requestMacAddress() {

        BluetoothGattCharacteristic characteristic = mCurrentBluetoothSession
                .getService(BluetoothBroadcastAdapter.SERVICE_UUID)
                .getCharacteristic(BluetoothBroadcastAdapter.CHARACTERISTIC_MAC_ADDRESS_UUID);

        mCurrentBluetoothSession.readCharacteristic(characteristic);
    }

    private void startFileServer(final boolean serverShouldSend) {
        Log.d(TAG,"starting bluetooth file server");
        Log.e(TAG,"using " + realBluetoothMacAddress + " instead of "+mCurrentBluetoothSession.getDevice().getAddress());
        new BluetoothFileClient(mBluetoothAdapter, mBluetoothAdapter.getRemoteDevice(realBluetoothMacAddress), new FileTransferInterface() {
            @Override
            public void onSocketReady(BluetoothSocket socket) {
                Log.d(TAG, "socket ready");
                BluetoothSocketWorker worker = new BluetoothSocketWorker(socket);
                if (!serverShouldSend) {
                    try {
                        Log.d(TAG, "sending data: " + mDeviceInterface.getMessagesList().toString());
                        worker.send(MessageHasher.serializeMessageList(mDeviceInterface.getMessagesList()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    requestDeviceMessagesHash();
                } else {
                    Log.d(TAG, "receiving data");
                    worker.receive(new BluetoothSocketWorker.BluetoothSocketListener() {
                        @Override
                        public void onBytesReceived(byte[] bytes) {
                            try {
                                mDeviceInterface.onServerMessagesReceived(MessageHasher.deserializeMessageList(bytes), BluetoothDevice.this);
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError() {
                disconnect();
            }
        });
    }

    public boolean isConnected() {
        return connected;
    }

}
