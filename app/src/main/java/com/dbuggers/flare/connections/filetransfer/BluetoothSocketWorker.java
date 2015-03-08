package com.dbuggers.flare.connections.filetransfer;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by rory on 07/03/15.
 */
public class BluetoothSocketWorker {

    private static final String TAG = "BluetoothSocketWorker";
    private final ConnectedThread thread;
    private BluetoothSocketListener mListener;

    public interface BluetoothSocketListener {
        public void onBytesReceived(byte[] bytes);
    }

    public BluetoothSocketWorker(BluetoothSocket socket) {
        thread = new ConnectedThread(socket);
    }

    public void send(byte[] bytes){
        thread.write(bytes);
        thread.start();
    }

    public void receive(BluetoothSocketListener listener){
        mListener = listener;
        thread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] bytesToWrite;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            if(bytesToWrite != null){

                Log.d(TAG, "writing bytes");
                try {
                    mmOutStream.write(bytesToWrite);

//                    Thread.sleep(500);

//                    mmOutStream.close();
                } catch (IOException e) { }
                Log.d(TAG, "finished writing bytes");

            }else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        // Read from the InputStream
                        Log.d(TAG, "reading bytes");
                        bytes = mmInStream.read(buffer);

                        if (bytes != -1) {
                            Log.d(TAG, "writing bytes to buffer");
                            baos.write(buffer);
                        } else {
                            Log.d(TAG, "breaking reading");
                            break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "exception breaking reading");
                        break;
                    } finally {
                        try {
                            baos.close();
                            mmSocket.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }


                Log.d(TAG, "done reading");
                if (mListener != null) {
                    mListener.onBytesReceived(baos.toByteArray());
                }

                try {
                    baos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            bytesToWrite = bytes;
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
