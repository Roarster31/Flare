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
import android.util.Log;

import com.dbuggers.flare.helpers.MessageHasher;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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



            Iterator it = result.getScanRecord().getServiceData().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                ParcelUuid uuid = (ParcelUuid) pair.getKey();
                byte[] bytes = (byte[]) pair.getValue();
                Log.d(TAG,"uuid: "+uuid+" byte length: "+bytes.length);
                it.remove(); // avoids a ConcurrentModificationException
            }


//            byte[] messagesHash = result.getScanRecord().getServiceData().getServiceData(BluetoothBroadcastAdapter.CHARACTERISTIC_MESSAGE_HASH_UUID_PARCELED);

            //we're a bit sneaky here and discount a device if it's hash is the same as ours
            //we should really let DataManager do this for us
//            try {
//                if(MessageHasher.doMatch(MessageHasher.hash(mDeviceInterface.getMessagesList()), messagesHash)){
//                    Log.e(TAG, "We're not getting involved with " + result.getDevice().getAddress() + " because its hash is up to date");
//                    return;
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
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
