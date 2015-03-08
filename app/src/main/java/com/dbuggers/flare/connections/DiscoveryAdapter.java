package com.dbuggers.flare.connections;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rory on 07/03/15.
 */
public abstract class DiscoveryAdapter {

    protected final DeviceInterface mDeviceInterface;
    protected Context mContext;

    public DiscoveryAdapter(Context context, DeviceInterface deviceInterface){
        mDeviceInterface = deviceInterface;
        mContext = context;
    }

    public abstract void stopScan();

    public abstract void scan();

}
