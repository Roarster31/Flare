package com.dbuggers.flare.connections;

import android.content.Context;

import com.dbuggers.flare.models.MinimalPayload;

/**
 * Created by rory on 07/03/15.
 */
public abstract class BroadcastAdapter {

    protected final Context mContext;
    protected final DeviceInterface mDeviceInterface;
    protected MinimalPayload mPayload;

    public BroadcastAdapter (Context context, DeviceInterface deviceInterface){
        mContext = context;
        mDeviceInterface = deviceInterface;
    }

    public void setPayload(MinimalPayload payload){
        mPayload = payload;
    }

    public abstract void beginBroadcast();

    public abstract void stopBroadcast();
}
