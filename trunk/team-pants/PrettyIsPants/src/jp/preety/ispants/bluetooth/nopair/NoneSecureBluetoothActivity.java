
package jp.preety.ispants.bluetooth.nopair;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.preety.ispants.R;
import jp.preety.ispants.bluetooth.BluetoothService;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * ノンセキュアで1対1で接続可能なAcitivity
 */
abstract public class NoneSecureBluetoothActivity extends Activity implements
        ConnectionListener<BluetoothSocket>, Runnable {

    private final String TAG = NoneSecureBluetoothActivity.class.getSimpleName();

    static final int REQUEST_MAKE_DISCOVERABLE = 0;

    static final int REQUEST_START_DISCOVERY = 1;

    static final UUID MY_UUID = UUID.fromString("830831f0-68d5-11e0-ae3e-0800200c9a66");

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;

    private static final int REQUEST_PICK_BLUETOOTH_DEVICE = 4;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    private int mServerFlag = 0;

    private boolean mBTClientSelected = false;

    String mConnectedDeviceName;

    public static final String EXTRA_SERVER = "EXTRA_SERVER";

    BluetoothServerSocket mServerSocket;

    BluetoothSocket mClientSocket;

    ExecutorService mExec;

    InputStream mInputStream;

    OutputStream mOutputStream;

    static final String MY_SERVICE = "myservice";

    public static final int FROM_SERVER = 0;

    public static final int FROM_CLIENT = 1;

    private static final String EOD = "          EOD          ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExec = Executors.newCachedThreadPool();
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
            Intent i = getIntent();
            if (i.hasExtra(EXTRA_SERVER)) {
                // 写真を撮った人が1stサーバになる
                mServerFlag = i.getIntExtra(EXTRA_SERVER, 0);
                i.removeExtra(EXTRA_SERVER);
                if (mServerFlag == 1) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_MAKE_DISCOVERABLE);
                } else {
                    Log.d(TAG, "pickBluetooth() called!");
                    pickBluetooth();
                }
            } else if (mClientSocket == null) {
                // ここ？
                Log.d(TAG, "pickBluetooth() called!");
                pickBluetooth();
            }
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            // case BluetoothService.MESSAGE_STATE_AS_SERVER_CHANGE:
            // if (mService != null
            // && mService.getStateAsServer() == BluetoothService.STATE_NONE) {
            // mService.startAcceptAsServer();
            // }
            // if (mService != null) {
            // onChangeBluetoothServerMessageState(mService.getStateAsServer());
            // }
            // break;
            // case BluetoothService.MESSAGE_STATE_AS_CLIENT_CHANGE:
            // Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
            // if (mService != null) {
            // onChangeBluetoothClientMessageState(mService.getStateAsClient());
            // }
            // break;
            //

                case BluetoothService.MESSAGE_READ:
                    // TODO: 描画を担当する
                    String readMessage = (String) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    // String readMessage = new String(readBuf, 0, msg.arg1);
                    // Log.d(TAG, readMessage);

                    // 描画情報を伝播する msg.arg1は伝播の方向
                    // sendToOtherDevice(readMessage, msg.arg1);

                    // 子Activityに描画を依頼。自分自身が書いた場合は無視する
                    onMessageRead(readMessage);
                    break;

            // case BluetoothService.MESSAGE_DEVICE_NAME:
            // // save the connected device's name
            // mConnectedDeviceName =
            // msg.getData().getString(BluetoothService.DEVICE_NAME);
            // onMessageDeviceName(mConnectedDeviceName);
            // break;
            //
            // case BluetoothService.MESSAGE_TOAST:
            // Toast.makeText(getApplicationContext(),
            // msg.getData().getString(BluetoothService.TOAST),
            // Toast.LENGTH_SHORT)
            // .show();
            // break;
            }
        }
    };

    protected void onChangeBluetoothServerMessageState(int state) {
    };

    protected void onChangeBluetoothClientMessageState(int state) {
        if (state == BluetoothService.STATE_CONNECTED) {
            onBluetoothConnectComplete();
        }
    };

    protected void onMessageRead(String message) {
    };

    protected void onMessageDeviceName(String connectedDeviceName) {
    };

    protected void pickBluetooth() {
        Intent intent = new Intent(this, DiscoveryActivity.class);
        this.startActivityForResult(intent, REQUEST_START_DISCOVERY);
    }

    /**
     * 他の端末に書き込む
     * 
     * @param sendData
     * @param from
     */
    private void sendToOtherDevice(String sendData, int from) {
        // mOutputStream.write(sendData.getBytes());
        // とろくん方式を使う
        write(sendData);

        // switch (from) {
        // case BluetoothService.FROM_CLIENT:
        // // TODO: クライアントとして伝播
        // if (mServerFlag == 0) {
        // mService.writeAsClient(sendData);
        // }
        // break;
        // case BluetoothService.FROM_SERVER:
        // // TODO: サーバとして伝播
        // mService.writeAsServer(sendData);
        // break;
        // default:
        // // サーバ側とクライアント側に伝播
        // mService.writeAsServer(sendData);
        // if (mServerFlag == 0) {
        // mService.writeAsClient(sendData);
        // }
        // break;
        // }
    }

    protected void sendToOtherDevice(String sendData) {
        sendToOtherDevice(sendData, -1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode);
        switch (requestCode) {
            case REQUEST_MAKE_DISCOVERABLE:
                startServer(resultCode);
                break;

            case REQUEST_START_DISCOVERY:
                if (resultCode != RESULT_OK) {
                    finish();
                }
                connectTo(resultCode, data);
                break;
            default:
                break;
        }
    }

    private void connectTo(int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        // showMessage("connect to remote device");

        BluetoothDevice device = data.getParcelableExtra("REMOTE_DEVICE");
        try {
            mClientSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            mExec.execute(new ConnectTask(mClientSocket, this));
        } catch (IOException e) {
            // showMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    // @Override
    // protected void onResume() {
    // super.onResume();
    // Log.i(TAG, "onResume");
    // if (mServerFlag == 0 && !mBTClientSelected) {
    // pickBluetooth();
    // }
    // }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Intent intent = new
        // Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // startActivityForResult(intent, REQUEST_MAKE_DISCOVERABLE);
    }

    /**
     * bluetoothのコネクトが完了した。
     */
    protected void onBluetoothConnectComplete() {

    }

    void startServer(int resultCode) {
        if (resultCode <= 0)
            return;

        try {
            mServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    MY_SERVICE, MY_UUID);
        } catch (IOException e) {
            // showMessage(e.getMessage());
            e.printStackTrace();
            return;
        }
        // サーバーとして接続を待ち続ける
        mExec.execute(new AcceptTask(mServerSocket, this));
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        StringBuilder builder = new StringBuilder();
        int bytes;
        while (true) {
            try {
                if (mInputStream != null) {
                    bytes = mInputStream.read(buffer);
                    String text = new String(buffer, 0, bytes);
                    builder.append(text);
                    String allStr = builder.toString();
                    int index = allStr.indexOf(EOD);
                    if (index == -1) {
                        continue;
                    }
                    String remine = allStr.substring(index + EOD.length());
                    allStr = allStr.substring(0, index);
                    builder = new StringBuilder();
                    builder.append(remine);
                    Log.d(TAG, "read finish!");
                    allStr = allStr.substring(0, index);
                    // ハンドラーに依頼する
                    int from = (mClientSocket != null) ? FROM_SERVER : FROM_CLIENT;
                    mHandler.obtainMessage(BluetoothService.MESSAGE_READ, from, -1, allStr)
                            .sendToTarget();
                }
            } catch (IOException maybeSocketClosed) {
                closeIfNeccesary(mClientSocket);
                onConnectionClosed(mClientSocket);
            }
        }
    }

    @Override
    public void onConnectionClosed(BluetoothSocket conn) {
        // 接続を閉じた
        closeIfNeccesary(conn);
        mInputStream = null;
        mOutputStream = null;
    }

    // @Override
    // public void onConnectionEstablished(BluetoothSocket conn) {
    // FIXME インターフェースの引数を増やしたので、コメントアウト
    // // 接続が確立した
    // // TODO サーバの場合とクライアントの場合で処理を変える事
    // try {
    // mInputStream = conn.getInputStream();
    // } catch (IOException e) {
    // // showMessage(e.getMessage());
    // e.printStackTrace();
    // return;
    // }
    // try {
    // mOutputStream = conn.getOutputStream();
    // } catch (IOException e) {
    // // showMessage(e.getMessage());
    // e.printStackTrace();
    // return;
    // }
    // mExec.execute(this);
    //
    // // 接続が完了したことを子Activityに通知する
    // onBluetoothConnectComplete();
    // }

    @Override
    public void onConnectionFailure(BluetoothSocket conn) {
        // 接続が失敗した
        // TODO: 接続に失敗したわけだから、終了させるか、再挑戦させるか
    }

    void closeIfNeccesary(Closeable socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
                ;// nop
            }
        }
    }

    @Override
    protected void onDestroy() {
        closeIfNeccesary(mServerSocket);
        closeIfNeccesary(mInputStream);
        closeIfNeccesary(mOutputStream);
        closeIfNeccesary(mClientSocket);
        mExec.shutdownNow();
        super.onDestroy();
    }

    private void write(String sendString) {
        try {
            // 終端文字列を追加
            String str = sendString + EOD;
            String remine = str;
            // remineを1024文字ずつ削りながらOutputStreamに送る
            while (!remine.equals("")) {
                String sendStr = "";
                if (remine.length() < 1024) {
                    sendStr = remine;
                    remine = "";
                } else {
                    sendStr = remine.substring(0, 1024);
                    remine = remine.substring(1024);
                }
                byte[] sendBuffer = sendStr.getBytes();
                mOutputStream.write(sendBuffer);
            }
            // mHandler.obtainMessage(BluetoothService.MESSAGE_WRITE, -1, -1,
            // sendString)
            // .sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionEstablished(BluetoothSocket conn, boolean isServer) {
        // FIXME Auto-generated method stub

    }

}
