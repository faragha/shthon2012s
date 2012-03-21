
package jp.preety.ispants.bluetooth.nopair;

import java.io.IOException;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class AcceptTask implements Runnable {

    private final ConnectionListener<BluetoothSocket> connectionListener;

    private final BluetoothServerSocket serverSocket;

    public AcceptTask(BluetoothServerSocket serverSocket,
            ConnectionListener<BluetoothSocket> connectionListener) {
        this.connectionListener = connectionListener;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {

        BluetoothSocket socket = null;
        for (;;) {
            try {

                socket = serverSocket.accept();
                connectionListener.onConnectionEstablished(socket, true);
            } catch (IOException maybeServerSocketClosed) {
                return;
            }
        }
    }
}
