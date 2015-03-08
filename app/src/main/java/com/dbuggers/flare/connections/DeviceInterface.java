package com.dbuggers.flare.connections;

import com.dbuggers.flare.models.MessageEntry;

import java.util.List;

/**
 * Created by rory on 07/03/15.
 */
public interface DeviceInterface {
    /**
     * Call this when the device receives its message hash
     *
     * @param serverDevice
     * @param messagesHash
     */
    public void onHashReceived(Device serverDevice, byte[] messagesHash);

    /**
     * This should be called after retrieveMessages has been called on the Device
     *
     * @param messages
     * @param serverDevice
     */
    public void onServerMessagesReceived(List<MessageEntry> messages, Device serverDevice);
    public void onNewMessagesReceived(List<MessageEntry> messages);
    public int getClientGroupId();
    public List<MessageEntry> getMessagesList();
}