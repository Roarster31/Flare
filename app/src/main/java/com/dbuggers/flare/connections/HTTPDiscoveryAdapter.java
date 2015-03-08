package com.dbuggers.flare.connections;

import android.content.Context;

import com.dbuggers.flare.helpers.MessageHasher;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by rory on 07/03/15.
 */
public class HTTPDiscoveryAdapter extends DiscoveryAdapter {

    private HTTPDevice device;

    public HTTPDiscoveryAdapter(Context context, DeviceInterface deviceInterface) {
        super(context, deviceInterface);
    }

    @Override
    public void stopScan() {

    }

    @Override
    public void scan() {
        device = new HTTPDevice(mDeviceInterface);
    }
}
