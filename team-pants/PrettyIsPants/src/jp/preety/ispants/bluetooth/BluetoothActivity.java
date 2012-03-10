package jp.preety.ispants.bluetooth;

import jp.preety.ispants.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BluetoothActivity extends Activity {
    private final String TAG = "BluetoothActivity";
    
    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_PICK_BLUETOOTH_DEVICE = 4;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mService = null;
    private int mServerFlag = 0;
    
    String mConnectedDeviceName;
    
    public static final String EXTRA_SERVER = "EXTRA_SERVER";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.equals(null)) {
            Toast.makeText(this, R.string.bluetooth_not_support, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "++ ON START ++");
        
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mService == null) {
                mService = new BluetoothService(this, mHandler);
                mService.start();
                Intent i = getIntent();
                if (i.hasExtra(EXTRA_SERVER)) {
                    mServerFlag = i.getIntExtra(EXTRA_SERVER, 0);
                    i.removeExtra(EXTRA_SERVER);
                    if (mServerFlag == 1) {
                    } else {
                        pickBluetooth();
                    }
                }
            }
        }
    }
    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_AS_SERVER_CHANGE:
                if (mService != null && mService.getStateAsServer() == BluetoothService.STATE_NONE) {
                    mService.startAcceptAsServer();
                }
                onChangeBluetoothServerMessageState();
                break;
            case BluetoothService.MESSAGE_STATE_AS_CLIENT_CHANGE:
                Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                onChangeBluetoothClientMessageState();
                break;
            case BluetoothService.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, readMessage);
                sendToOtherDevice(readMessage, msg.arg2);
                onMessageRead(readMessage);
                break;
            case BluetoothService.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
                onMessageDeviceName(mConnectedDeviceName);
                break;
            case BluetoothService.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothService.TOAST), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    protected void onChangeBluetoothServerMessageState(){};
    
    protected void onChangeBluetoothClientMessageState(){};
    
    protected void onMessageRead(String message){};
    
    protected void onMessageDeviceName(String connectedDeviceName){};

    private void pickBluetooth() {
        Intent intent = new Intent(this, BluetoothListPickerActivity.class);
        this.startActivityForResult(intent, REQUEST_PICK_BLUETOOTH_DEVICE);
    }
    
    private void sendToOtherDevice(String sendData, int from) {
        if(mService != null) {
            switch(from) {
            case BluetoothService.fromClient:
                if(mServerFlag == 0) {
                    mService.writeAsClient(sendData.getBytes());
                }
                break;
            case BluetoothService.fromServer:
                mService.writeAsServer(sendData.getBytes());
                break;
            default:
                mService.writeAsServer(sendData.getBytes());
                if(mServerFlag == 0) {
                    mService.writeAsClient(sendData.getBytes());
                }
                break;
            }
        }
    }
    
    protected void sendToOtherDevice(String sendData) {
        sendToOtherDevice(sendData, -1);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                mService = new BluetoothService(this, mHandler);
                mService.start();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bluetooth_not_enabled, Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        case REQUEST_PICK_BLUETOOTH_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, R.string.connecting, Toast.LENGTH_SHORT).show();
                connectDevice(data.getExtras().getString("address"));
            }
        }
    }
    
    private void connectDevice(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mService.connectAsClient(device);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        if (mService != null) {
            mService.stop();
            mService = null;
        }
    }
}

