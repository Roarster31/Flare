package com.dbuggers.flare.helpers;

import android.util.Log;

import com.dbuggers.flare.models.MessageEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rory on 07/03/15.
 */
public class MessageHasher {

    private static final String TAG = "MessageHasher";

    public static List<MessageEntry> deserializeMessageList(byte[] bytes) throws IOException, ClassNotFoundException {

        Log.d(TAG, "bytecheck: " + Arrays.toString(bytes));
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        ObjectInputStream is = new ObjectInputStream(bis);

        List<MessageEntry> messages = (List<MessageEntry>) is.readObject();

        is.close();

        return messages;

    }

    public static byte[] serializeMessageList(List<MessageEntry> messageList) throws IOException {

        ByteArrayOutputStream bb = new ByteArrayOutputStream();

        ObjectOutputStream out = new ObjectOutputStream(bb);

        out.writeObject(messageList);

        out.close();

        byte[] bytes = bb.toByteArray();

        Log.d(TAG, "bytecheck: " + Arrays.toString(bytes));

        return bytes;

    }

    public static byte[] hash(List<MessageEntry> messageList) throws IOException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");

        return md.digest(serializeMessageList(messageList));
    }


}
