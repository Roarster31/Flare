package com.dbuggers.flare.connections.filetransfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by rory on 07/03/15.
 */
public class BluetoothFileServer {

    static final UUID SERVER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String SERVER_NAME = "Nexus 6";
    private static final String TAG = "BluetoothFileServer";
    private final BluetoothAdapter mBluetoothAdapter;
    private final AcceptThread mAcceptThread;
    private final FileTransferInterface mTransferInterface;

    public BluetoothFileServer (BluetoothAdapter bluetoothAdapter,  FileTransferInterface transferInterface) {
        mBluetoothAdapter = bluetoothAdapter;
        mTransferInterface = transferInterface;

        mAcceptThread = new AcceptThread();

        mAcceptThread.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // SERVER_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, SERVER_UUID);
            } catch (IOException ignored) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    Log.d(TAG, "server waiting for connection");
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                } catch (NullPointerException e){
                    mTransferInterface.onError();
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    mTransferInterface.onSocketReady(socket);
                    try {
                        Log.e(TAG, "closing socket");
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }

}
