package com.dbuggers.flare.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by rory on 07/03/15.
 */
public class MessageEntry implements Comparable, Serializable{

    private Long time;
    private Integer userId;
    private String message;

    public MessageEntry(long time, int userId, String message){
        this.time = time;
        this.userId = userId;
        this.message = message;
    }

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageEntry that = (MessageEntry) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = time != null ? time.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object another) {
        if(another != null && another instanceof MessageEntry){
            MessageEntry otherMessage = (MessageEntry) another;
            return Long.compare(otherMessage.getTime(), getTime());
        }else{
            return 0;
        }
    }

    @Override
    public String toString() {
        return "MessageEntry{" +
                "time=" + time +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                '}';
    }


    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(this);

        oos.flush();
        oos.close();

        return baos.toByteArray();
    }

    public static MessageEntry fromByteArray(byte[] array) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(array);
        ObjectInput in = new ObjectInputStream(bis);
        return (MessageEntry) in.readObject();
    }
}
