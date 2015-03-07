package com.dbuggers.flare.models;

/**
 * Created by rory on 07/03/15.
 */
public class MessageEntry {
    private long time;
    private int userId;
    private String message;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
