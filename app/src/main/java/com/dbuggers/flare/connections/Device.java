package com.dbuggers.flare.connections;

import com.dbuggers.flare.models.MessageEntry;
import com.dbuggers.flare.models.MessagesPayload;

import java.util.List;

/**
 * Created by rory on 07/03/15.
 */
public abstract class Device {
    protected final DeviceInterface mDeviceInterface;

    public abstract void requestMessages();

    public Device (DeviceInterface deviceInterface){
        mDeviceInterface = deviceInterface;
    }

    public abstract void disconnect();

    public abstract void fetchData(DeviceInterface deviceInterface);

    public abstract void sendData(MessagesPayload payload);

    public abstract void updateMessages(List<MessageEntry> list);
}
