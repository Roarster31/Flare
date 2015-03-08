package com.dbuggers.flare.connections;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.dbuggers.flare.helpers.MessageHasher;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class deals with continuously scanning for devices. It then connects with a device and
 * compares hashes of all the messages. If the hashes are equal then we do nothing, but who should
 * control this logic?
 */
public class BluetoothDiscoveryAdapter extends DiscoveryAdapter implements BluetoothDiscoveryListener {

    private static final String TAG = "BluetoothDiscoveryAdapter";
    private static final long CLEAN_SCAN_INTERVAL = 4000;
    private final BluetoothLeScanner mBluetoothScanner;
    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;


    public BluetoothDiscoveryAdapter(Context context, BluetoothAdapter bluetoothAdapter, DeviceInterface deviceInterface) {
        super(context, deviceInterface);

        mBluetoothAdapter = bluetoothAdapter;

        if (!bluetoothAdapter.isEnabled()) {
            throw new IllegalStateException("Please make sure bluetooth is enabled before trying to setup");
        }

        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new IllegalStateException("Device does not support bluetooth LE!!");
        } else {
            mBluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        mHandler = new Handler();

    }


    @Override
    public void stopScan() {
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
    }

    @Override
    public void scan() {

        scanWasClean = false;
        //Scan for devices advertising our custom service
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(BluetoothBroadcastAdapter.SERVICE_UUID))
                .build();
        ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
        filters.add(scanFilter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();
        mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, mScanCallback);
    }

    private boolean scanWasClean;
    private Runnable mScanRunnable = new Runnable() {
        @Override
        public void run() {
          if(scanWasClean){
              stopScan();
              scan();
          }
        }
    };
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            mHandler.removeCallbacks(mScanRunnable);
            scanWasClean = processDevice(result);
            mHandler.postDelayed(mScanRunnable, CLEAN_SCAN_INTERVAL);
        }

        /**
         *
         * @param result
         * @return true if the device was processed rather than skipped
         */
        private boolean processDevice(final ScanResult result) {


            byte[] messagesHash = null;

            Iterator it = result.getScanRecord().getServiceData().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                ParcelUuid uuid = (ParcelUuid) pair.getKey();
                byte[] bytes = (byte[]) pair.getValue();
                messagesHash = bytes;
                Log.d(TAG, "uuid: " + uuid + " byte length: " + bytes.length);
                it.remove(); // avoids a ConcurrentModificationException
            }

            try {
                byte[] hash = MessageHasher.hash(mDeviceInterface.getMessagesList());
                if (messagesHash != null && MessageHasher.doMatch(hash, messagesHash)) {
                    Log.e(TAG, "We're not getting involved with " + result.getDevice().getAddress() + " because its hash is up to date");
                    return true;
                } else {
                    Log.e(TAG, "hashes not not equal: " + new String(hash) + " != " + new String(messagesHash));
                    stopScan();

                    final BluetoothDevice device = new BluetoothDevice(mDeviceInterface, BluetoothDiscoveryAdapter.this);

//                     final Handler handler = new Handler();
//
//                    final Runnable t = new Runnable() {
//                        @Override
//                        public void run() {
//                            if(!device.isConnected()) {
//                                device.connect(mContext, mBluetoothAdapter, result.getDevice());
//                                handler.postDelayed(this,1000);
//                            }
//                        }
//                    };
//                     handler.postDelayed(t,1000);

                    device.connect(mContext, mBluetoothAdapter, result.getDevice());
                    return false;
//                    handler.postDelayed(this,1000);

                }
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            scanWasClean = true;
            for (ScanResult result : results) {
                if(processDevice(result)){
                    scanWasClean = false;
                }
            }
            scanWasClean = true;
            mHandler.postDelayed(mScanRunnable, CLEAN_SCAN_INTERVAL);
            stopScan();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @Override
    public void onDisconnected() {
        scan();
    }
}
