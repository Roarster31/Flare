package com.dbuggers.flare.connections.filetransfer;

import android.bluetooth.*;
import android.util.Log;

import java.io.IOException;

/**
 * Created by rory on 07/03/15.
 */
public class BluetoothFileClient {

    private static final String TAG = "BluetoothFileCLient";
    private final BluetoothAdapter mBluetoothAdapter;
    private final FileTransferInterface mTransferInterface;
    private final ConnectThread thread;

    public BluetoothFileClient(BluetoothAdapter bluetoothAdapter, android.bluetooth.BluetoothDevice device, FileTransferInterface transferInterface){
        mBluetoothAdapter = bluetoothAdapter;
        mTransferInterface = transferInterface;

        thread = new ConnectThread(device);

        thread.start();

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final android.bluetooth.BluetoothDevice mmDevice;

        public ConnectThread(android.bluetooth.BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(BluetoothFileServer.SERVER_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Log.d(TAG, "attempting to connect to server");
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    connectException.printStackTrace();
                    Log.e(TAG, "closing socket!");
                    mTransferInterface.onError();
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            mTransferInterface.onSocketReady(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
