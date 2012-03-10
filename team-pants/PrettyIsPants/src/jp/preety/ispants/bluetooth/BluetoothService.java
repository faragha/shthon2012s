package jp.preety.ispants.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import jp.preety.ispants.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothService {
    // Debugging
    private static final String TAG = "BluetoothService";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "ActivitySecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;

    private AcceptAsServerThread mAcceptAsServerThread;
    private ConnectAsClientThread mConnectAsClientThread;
    private ConnectedThread mConnectedAsServerThread;
    private ConnectedThread mConnectedAsClientThread;

    private int mStateAsServer;
    private int mStateAsClient;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
                                              // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
                                                  // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
                                                 // device

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_AS_SERVER_CHANGE = 1;
    public static final int MESSAGE_STATE_AS_CLIENT_CHANGE = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_READ = 4;
    public static final int MESSAGE_DEVICE_NAME = 5;
    public static final int MESSAGE_TOAST = 6;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    public static final int FROM_SERVER = 0;
    public static final int FROM_CLIENT = 1;
    
    private static final int BUFFER_MAX = 1024;
    
    Context mContext;

    /**
     * Constructor. Prepares a new SomenSliderActivity session.
     * 
     * @param context
     *            The UI Activity Context
     * @param handler
     *            A Handler to send messages back to the UI Activity
     */
    public BluetoothService(Context context, Handler handler) {
        mContext = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mStateAsServer = STATE_NONE;
        mStateAsClient = STATE_NONE;
        mHandler = handler;
    }

    private synchronized void setStateAsServer(int state) {
        Log.d(TAG, "setStateAsServer() " + mStateAsServer + " -> " + state);
        mStateAsServer = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_AS_SERVER_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getStateAsServer() {
        return mStateAsServer;
    }

    private synchronized void setStateAsClient(int state) {
        Log.d(TAG, "setStateAsClient() " + mStateAsClient + " -> " + state);
        mStateAsClient = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_AS_CLIENT_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getStateAsClient() {
        return mStateAsClient;
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        clearServerThread();
        clearClientThread();
        startAcceptAsServer();
    }

    public synchronized void startAcceptAsServer() {
        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptAsServerThread == null) {
            mAcceptAsServerThread = new AcceptAsServerThread();
            mAcceptAsServerThread.start();
        }
        setStateAsServer(STATE_LISTEN);
    }

    public synchronized void connectAsClient(BluetoothDevice device) {
        Log.d(TAG, "connect as client to: " + device);

        clearClientThread();
        // Start the thread to connect with the given device
        mConnectAsClientThread = new ConnectAsClientThread(device);
        mConnectAsClientThread.start();
        setStateAsClient(STATE_CONNECTING);
    }

    public synchronized void connectedAsServer(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "connectedAsServer");

        clearServerThread();
        // Start the thread to manage the connection and perform transmissions
        mConnectedAsServerThread = new ConnectedThread(socket, true);
        mConnectedAsServerThread.isClient = false;
        mConnectedAsServerThread.start();

        sendDeviceNameToast(device);
        setStateAsServer(STATE_CONNECTED);

        // Cancel the accept thread because we only want to connect to one
        // device
        if (mAcceptAsServerThread != null) {
            mAcceptAsServerThread.cancel();
            mAcceptAsServerThread = null;
        }

    }

    public synchronized void connectedAsClient(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "connectedAsClient");

        clearClientThread();
        // Start the thread to manage the connection and perform transmissions
        mConnectedAsClientThread = new ConnectedThread(socket, false);
        mConnectedAsClientThread.isClient = true;
        mConnectedAsClientThread.start();

        sendDeviceNameToast(device);
        setStateAsClient(STATE_CONNECTED);
    }

    public synchronized void stop() {
        Log.d(TAG, "stop");

        clearServerThread();
        clearClientThread();

        // Cancel the accept thread because we only want to connect to one
        // device
        if (mAcceptAsServerThread != null) {
            mAcceptAsServerThread.cancel();
            mAcceptAsServerThread = null;
        }

    }

    public void writeAsServer(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mStateAsServer != STATE_CONNECTED) {
                return;
            }
            r = mConnectedAsServerThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }
    
    public void writeAsClient(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mStateAsClient != STATE_CONNECTED) {
                return;
            }
            r = mConnectedAsClientThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private void connectionAsClietnFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, mContext.getString(R.string.unable_to_connect));
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        clearClientThread();
    }

    private void connectionLost(boolean server) {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, mContext.getString(R.string.connection_was_lost));
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        if (server) {
            clearServerThread();
        } else {
            clearClientThread();
        }
    }

    private synchronized void sendDeviceNameToast(BluetoothDevice device) {
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, mContext.getString(R.string.connected, device.getName()));
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private synchronized void clearServerThread() {
        // Cancel any thread currently running a connection
        if (mConnectedAsServerThread != null) {
            mConnectedAsServerThread.cancel();
            mConnectedAsServerThread = null;
        }

        setStateAsServer(STATE_NONE);
    }

    private synchronized void clearClientThread() {
        // Cancel any thread attempting to make a connection
        if (mConnectAsClientThread != null) {
            mConnectAsClientThread.cancel();
            mConnectAsClientThread = null;
        }

        if (mConnectedAsClientThread != null) {
            mConnectedAsClientThread.cancel();
            mConnectedAsClientThread = null;
        }

        setStateAsClient(STATE_LISTEN);
    }

    public boolean isRoot() {
        if (mStateAsClient == STATE_CONNECTED) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isLast() {
        if (mStateAsServer == STATE_CONNECTED) {
            return false;
        } else {
            return true;
        }
    }

    private class AcceptAsServerThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptAsServerThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
            } catch (IOException e) {
                Log.e(TAG, "*AcceptThread* listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            Log.d(TAG, "*AcceptThread* BEGIN mAcceptThread" + this);

            BluetoothSocket socket = null;
            while (mStateAsServer != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "*AcceptThread* accept() failed", e);
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mStateAsServer) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            connectedAsServer(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate
                            // new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "*AcceptThread* Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            Log.i(TAG, "*AcceptThread* END mAcceptThread");
        }

        public void cancel() {
            Log.d(TAG, "*AcceptThread* cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "*AcceptThread* close() of server failed", e);
            }
        }
    }

    private class ConnectAsClientThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectAsClientThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) {
                Log.e(TAG, "*ConnectAsClientThread* createRfcommSocketToServiceRecord() failed", e);
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            Log.i(TAG, "*ConnectAsClientThread* BEGIN mConnectAsClientThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "*ConnectAsClientThread* unable to close() socket during connection failure", e2);
                }
                connectionAsClietnFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectAsClientThread = null;
            }

            // Start the connected thread
            connectedAsClient(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "*ConnectAsClientThread* close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean mmServer;
        
        public boolean isClient = false;

        public ConnectedThread(BluetoothSocket socket, boolean server) {
            Log.d(TAG, "*ConnectedThread* create ConnectedThread");
            mmSocket = socket;
            mmServer = server;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "*ConnectedThread* temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            Log.i(TAG, "*ConnectedThread* BEGIN mConnectedThread");
            byte[] buffer = new byte[BUFFER_MAX];
            int bytes;
            

            // Keep listening to the InputStream while connected
            int readAllBufferSize = 0;
            byte[] readAllBuffer = new byte[1];
            while (true) {
                try {
                    // Read from the InputStream
                    readAllBufferSize = 0;
                    bytes = mmInStream.read(buffer);
                    if(readAllBufferSize != 0) {
                        byte[] tempBuffer = new byte[readAllBufferSize];
                        for(int i = 0; i < readAllBufferSize; i++) {
                            tempBuffer[i] = readAllBuffer[i];
                        }
                        readAllBuffer = new byte[readAllBufferSize + bytes];
                        for(int i = 0; i < readAllBufferSize; i++) {
                            readAllBuffer[i] = tempBuffer[i];
                        }
                        for(int i = 0; i < bytes; i++) {
                            readAllBuffer[readAllBufferSize + i] = buffer[i];
                        }
                    } else {
                        readAllBuffer = new byte[bytes];
                        for(int i = 0; i < bytes; i++) {
                            readAllBuffer[i] = buffer[i];
                        }
                    }
                    readAllBufferSize += bytes;
                    
                    // 終端判定
                    for(int i = readAllBufferSize - 1; i > readAllBufferSize - 5; i--) {
                        if(readAllBuffer[i] != 0x0) {
                            // 終端4byteのうち1つでも0でない場合は終端ではない
                            continue;
                        }
                    }

                    // Send the obtained bytes to the UI Activity
                    int from = (isClient? FROM_SERVER : FROM_CLIENT);
                    Log.d(TAG, String.valueOf(readAllBufferSize));
                    mHandler.obtainMessage(MESSAGE_READ, readAllBufferSize, from, readAllBuffer).sendToTarget();
                    readAllBufferSize = 0;
                    readAllBuffer = new byte[1];
                } catch (IOException e) {
                    Log.e(TAG, "*ConnectedThread* disconnected", e);
                    connectionLost(mmServer);
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * 
         * @param buffer
         *            The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                byte[] sendBuffer = new byte[buffer.length + 4];
                for(int i = 0; i < buffer.length; i++) {
                    sendBuffer[i] = buffer[i];
                }
                for(int i = 0; i < 4; i++) {
                    sendBuffer[buffer.length + i] = 0x0;
                }
                mmOutStream.write(sendBuffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "*ConnectedThread* Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "*ConnectedThread* close() of connect socket failed", e);
            }
        }
    }

}