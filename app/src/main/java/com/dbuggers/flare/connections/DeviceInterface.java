package com.dbuggers.flare.connections;

import com.dbuggers.flare.models.MessageEntry;

import java.util.List;

/**
 * Created by rory on 07/03/15.
 */
public interface DeviceInterface {
    public void onHashReceived(Device serverDevice, byte[] messagesHash);
    public void onServerMessagesReceived(List<MessageEntry> messages, Device serverDevice);
    public void onNewMessagesReceived(List<MessageEntry> messages);
    public int getClientGroupId();
    public List<MessageEntry> getMessagesList();
}