package com.dbuggers.flare.connections;

import android.bluetooth.*;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import com.dbuggers.flare.connections.filetransfer.BluetoothFileServer;
import com.dbuggers.flare.connections.filetransfer.BluetoothSocketWorker;
import com.dbuggers.flare.connections.filetransfer.FileTransferInterface;
import com.dbuggers.flare.helpers.MessageHasher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by rory on 07/03/15.
 */
public class BluetoothBroadcastAdapter extends BroadcastAdapter {

    public static final UUID SERVICE_UUID = UUID.fromString("1706BBC0-88AB-4B8D-877E-2237916EE929");
    public static final UUID CHARACTERISTIC_GROUP_ID_UUID = UUID.fromString("20033A23-F091-4903-AFFA-C652CCE7E220");
    public static final UUID CHARACTERISTIC_MESSAGE_HASH_UUID = UUID.fromString("D7D2C1BE-C6AA-4487-A288-C19B7508D1DE");
    public static final ParcelUuid CHARACTERISTIC_MESSAGE_HASH_UUID_PARCELED = ParcelUuid.fromString("D7D2C1BE-C6AA-4487-A288-C19B7508D1DE");
    public static final UUID CHARACTERISTIC_MAC_ADDRESS_UUID = UUID.fromString("0FFE3A4C-BC25-4F47-97A7-C95B0CB11C9E");
    public static final UUID CHARACTERISTIC_SEND_MESSAGELIST_UUID = UUID.fromString("5C6A2A30-3FCF-4EDB-AC17-AF9EE6955AB1");
    private static final String TAG = "BluetoothBroadcastAdapter";
    public static final byte[] SERVER_SEND_MESSAGELIST_KEY = {0};
    public static final byte[] CLIENT_SEND_MESSAGELIST_KEY = {1};
    private final BluetoothManager mBluetoothManager;
    private BluetoothGattServer mGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    public BluetoothBroadcastAdapter(Context context, BluetoothManager bluetoothManager, DeviceInterface deviceInterface) {
        super(context, deviceInterface);
        mBluetoothManager = bluetoothManager;
    }

    @Override
    public void beginBroadcast() {

        mGattServer = mBluetoothManager.openGattServer(mContext, mGattServerCallback);
        mBluetoothLeAdvertiser = mBluetoothManager.getAdapter().getBluetoothLeAdvertiser();

        initServer();
        startAdvertising();
    }

    @Override
    public void stopBroadcast() {

        if(mBluetoothLeAdvertiser != null){
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }

        if(mGattServer != null){
            mGattServer.close();
            mGattServer = null;
        }

    }

    private void initServer() {
        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic groupIdCharacteristic =
                new BluetoothGattCharacteristic(CHARACTERISTIC_GROUP_ID_UUID,
                        //Read+write permissions
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        groupIdCharacteristic.setValue(ByteBuffer.allocate(Integer.SIZE/8).putInt(mDeviceInterface.getClientGroupId()).array());

        BluetoothGattCharacteristic macAddressCharacteristic =
                new BluetoothGattCharacteristic(CHARACTERISTIC_MAC_ADDRESS_UUID,
                        //Read+write permissions
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        macAddressCharacteristic.setValue(mBluetoothManager.getAdapter().getAddress());

        BluetoothGattCharacteristic messagesHashCharacteristic =
                new BluetoothGattCharacteristic(CHARACTERISTIC_MESSAGE_HASH_UUID,
                        //Read+write permissions
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        BluetoothGattCharacteristic startServerCharacteristic =
                new BluetoothGattCharacteristic(CHARACTERISTIC_SEND_MESSAGELIST_UUID,
                        //Read+write permissions
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        service.addCharacteristic(groupIdCharacteristic);
        service.addCharacteristic(macAddressCharacteristic);
        service.addCharacteristic(messagesHashCharacteristic);
        service.addCharacteristic(startServerCharacteristic);

        Log.d(TAG,"starting gatt server @ "+mBluetoothManager.getAdapter().getAddress());


        mGattServer.addService(service);
    }

    private void startAdvertising() {
        if (mBluetoothLeAdvertiser == null) return;

        try {
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        AdvertiseData data = null;

            byte[] hash = MessageHasher.hash(mDeviceInterface.getMessagesList());
            Log.d(TAG,"hash length: "+ hash.length);

            byte[] shortened = Arrays.copyOf(hash, 5);
            Log.d(TAG,"shortened length: "+ hash.length);

            data = new AdvertiseData.Builder()
//                    .setIncludeDeviceName(true)
                    .addServiceData(CHARACTERISTIC_MESSAGE_HASH_UUID_PARCELED, shortened)
                    .addServiceUuid(new ParcelUuid(SERVICE_UUID))
                    .build();
            mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG,"advertising service successfully");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e(TAG,"could not advertise service successfully, errorCode: "+errorCode);
        }
    };

    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.d(TAG,"onConnectionStateChange status: "+status+" newState: "+newState+" device: "+device.toString());
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status,service);
            Log.d(TAG, "onServiceAdded service: " + service.getUuid().toString());
        }

        @Override
        public void onCharacteristicReadRequest(final BluetoothDevice device, final int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device,requestId,offset,characteristic);
            Log.d(TAG,"onCharacteristicReadRequest requestId: "+requestId+" offset: "+offset+" characteristic: "+characteristic.toString()+" device: "+device.toString());

            if (CHARACTERISTIC_GROUP_ID_UUID.equals(characteristic.getUuid())) {

                mGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        ByteBuffer.allocate(Integer.SIZE/8).putInt(mDeviceInterface.getClientGroupId()).array());
            } else if (CHARACTERISTIC_MESSAGE_HASH_UUID.equals(characteristic.getUuid())) {

                try {
                    byte[] messageHash = MessageHasher.hash(mDeviceInterface.getMessagesList());

                    Log.d(TAG,"Sending message hash "+new String(messageHash));
                    mGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            0,
                            messageHash);
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


            } else if (CHARACTERISTIC_MAC_ADDRESS_UUID.equals(characteristic.getUuid())) {

                mGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        mBluetoothManager.getAdapter().getAddress().getBytes());

            }

        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            Log.d(TAG,"onCharacteristicWriteRequest requestId: "+requestId+" offset: "+offset+" characteristic: "+characteristic.toString()+" device: "+device.toString());

            if(characteristic.getUuid().equals(CHARACTERISTIC_SEND_MESSAGELIST_UUID)){
                boolean serverShouldSend = Arrays.equals(value, SERVER_SEND_MESSAGELIST_KEY);
                Log.d(TAG,"starting server, should send: "+serverShouldSend);
                startFileServer(serverShouldSend);

                mGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        null);
            }
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            Log.d(TAG,"onDescriptorReadRequest requestId: "+requestId+" offset: "+offset+" descriptor: "+descriptor.toString()+" device: "+device.toString());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            Log.d(TAG,"onDescriptorWriteRequest requestId: "+requestId+" offset: "+offset+" descriptor: "+descriptor.toString()+" device: "+device.toString());
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }
    };

    private void startFileServer(final boolean serverShouldSend) {
        Log.d(TAG,"starting bluetooth file server");
        new BluetoothFileServer(mBluetoothManager.getAdapter(), new FileTransferInterface() {
            @Override
            public void onSocketReady(BluetoothSocket socket) {
                Log.d(TAG,"socket ready");
                BluetoothSocketWorker worker = new BluetoothSocketWorker(socket);
                if(serverShouldSend){
                    Log.d(TAG,"sending data: "+mDeviceInterface.getMessagesList().toString());
                    try {
                        worker.send(MessageHasher.serializeMessageList(mDeviceInterface.getMessagesList()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG,"receiving data");
                    worker.receive(new BluetoothSocketWorker.BluetoothSocketListener() {
                        @Override
                        public void onBytesReceived(byte[] bytes) {
                            try {
                                mDeviceInterface.onNewMessagesReceived(MessageHasher.deserializeMessageList(bytes));
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

}
