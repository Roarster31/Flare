package com.dbuggers.flare.models;

/**
 * Created by rory on 07/03/15.
 */
public class BroadcastResponse {
    private String currentHash;
    private int userId;
    private int groupId;

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
}
