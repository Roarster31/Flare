package com.dbuggers.flare.connections;

/**
 * Created by rory on 07/03/15.
 */
public abstract class ConnectionAdapter {

    protected ConnectionInterface mConnectionInterface;

    public interface ConnectionInterface {
        public void onDeviceFound(Device device);
    }
    public void scan(ConnectionInterface connectionInterface){
        mConnectionInterface =connectionInterface;
    }

}
