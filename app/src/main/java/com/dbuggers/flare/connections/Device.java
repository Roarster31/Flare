package com.dbuggers.flare.connections;

import com.dbuggers.flare.models.MessagesPayload;
import com.dbuggers.flare.models.MinimalPayload;

/**
 * Created by rory on 07/03/15.
 */
public abstract class Device {
    private String currentHash;
    private String macAddress;

    private DeviceInterface mdeviceInterface;

    public interface DeviceInterface {

        public void onDataFetched(MessagesPayload response);
        public void onHashUpdated(MinimalPayload response);
    }

    public void setInterface(DeviceInterface mdeviceInterface) {
        this.mdeviceInterface = mdeviceInterface;
    }

    public Device(String hash){
        currentHash = hash;
    }

    public String getHash(){
        return currentHash;
    }

    public abstract void fetchData(DeviceInterface deviceInterface);

    public abstract void sendData(MessagesPayload payload);
}
