package com.dbuggers.flare.connections;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;

/**
 * This class deals with continuously scanning for devices. It then connects with a device and
 * compares hashes of all the messages. If the hashes are equal then we do nothing, but who should
 * control this logic?
 */
public class BluetoothDiscoveryAdapter extends DiscoveryAdapter {

    private static final String TAG = "BluetoothDiscoveryAdapter";
    private final BluetoothLeScanner mBluetoothScanner;
    private final BluetoothAdapter mBluetoothAdapter;



    public BluetoothDiscoveryAdapter(Context context, BluetoothAdapter bluetoothAdapter, DeviceInterface deviceInterface) {
        super(context, deviceInterface);

        mBluetoothAdapter = bluetoothAdapter;

        if(!bluetoothAdapter.isEnabled()){
            throw new IllegalStateException("Please make sure bluetooth is enabled before trying to setup");
        }

        if(!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            throw new IllegalStateException("Device does not support bluetooth LE!!");
        }else {
            mBluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

    }


    @Override
    public void stopScan() {
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
    }

    @Override
    public void scan() {

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

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            processDevice(result);
        }

        private void processDevice(ScanResult result) {
            new BluetoothDevice(mDeviceInterface, result.getDevice()).connect(mContext, mBluetoothAdapter);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for(ScanResult result : results){
                processDevice(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

}
