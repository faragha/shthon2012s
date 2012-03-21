
package jp.preety.ispants.bluetooth.nopair;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;

public class ConnectTask implements Runnable {

    private final ConnectionListener<BluetoothSocket> connectionListener;

    private final BluetoothSocket socket;

    public ConnectTask(BluetoothSocket socket,
            ConnectionListener<BluetoothSocket> connectionListener) {
        this.connectionListener = connectionListener;
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            socket.connect();
            connectionListener.onConnectionEstablished(socket, false);
        } catch (IOException e) {
            connectionListener.onConnectionFailure(socket);
        }
    }
}
