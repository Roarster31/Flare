package com.dbuggers.flare.connections.filetransfer;

import android.bluetooth.BluetoothSocket;

/**
 * Created by rory on 07/03/15.
 */
public interface FileTransferInterface {
    public void onSocketReady(BluetoothSocket socket);
}
