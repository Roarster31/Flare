package com.dbuggers.flare.models;

import java.util.List;

/**
 * Created by rory on 07/03/15.
 */
public class MessageResponse {
    private String currentHash;
    private int userId;
    private int groupId;
    private List<MessageEntry> messages;

    public String getCurrentHash() {
        return currentHash;
    }

    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public List<MessageEntry> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageEntry> messages) {
        this.messages = messages;
    }
}
