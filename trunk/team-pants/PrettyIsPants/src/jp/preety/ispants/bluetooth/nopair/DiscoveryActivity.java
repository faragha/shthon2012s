
package jp.preety.ispants.bluetooth.nopair;

import java.util.LinkedList;
import java.util.List;

import jp.preety.ispants.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DiscoveryActivity extends Activity {

    final class MyAdapter extends BaseAdapter {

        public int getCount() {
            return mDevices.size();
        }

        public BluetoothDevice getItem(int position) {
            return mDevices.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView deviceName = (TextView) convertView;

            if (deviceName == null)
                deviceName = (TextView) layoutInflater.inflate(android.R.layout.simple_list_item_1,
                        null);

            final BluetoothDevice remDev = getItem(position);

            String name = remDev.getName();

            deviceName.setText(name != null ? name : remDev.getAddress());

            return deviceName;
        }
    }

    final class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String act = intent.getAction();

            if (act.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
                handleDiscoveryStarted(intent);
            else if (act.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
                handleDiscoveryFinished(intent);
            else if (act.equals(BluetoothDevice.ACTION_FOUND))
                handleFound(intent);
            else if (act.equals(BluetoothDevice.ACTION_NAME_CHANGED))
                handleNameChanged(intent);
        }

        void registerSelf(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
            context.registerReceiver(this, filter);
        }
    }

    final class MyListItemClickListener implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            // 接続先のサーバを選択
            BluetoothDevice remDev = mDevices.get(position);
            Intent result = new Intent();
            result.putExtra("REMOTE_DEVICE", remDev);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    private static boolean isNameRetrieved(BluetoothDevice device) {
        return device.getName() != null;
    }

    MyAdapter adapter;

    BluetoothAdapter mBluetoothAdapter;

    List<BluetoothDevice> mDevices;

    LayoutInflater layoutInflater;

    MyBroadcastReceiver receiver;

    public DiscoveryActivity() {
        mDevices = new LinkedList<BluetoothDevice>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.discovery);

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        adapter = new MyAdapter();

        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new MyListItemClickListener());

        receiver = new MyBroadcastReceiver();

        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        startDiscovery();
    }

    @Override
    protected void onResume() {
        receiver.registerSelf(this);
        super.onResume();
    }

    void handleDiscoveryFinished(Intent intent) {
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);
    }

    void handleDiscoveryStarted(Intent intent) {
        setProgressBarIndeterminate(true);
        setProgressBarIndeterminateVisibility(true);
    }

    void handleFound(Intent intent) {
        BluetoothDevice device;
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (!isNameRetrieved(device))
            return;

        synchronized (mDevices) {
            mDevices.add(device);
            adapter.notifyDataSetChanged();
        }
    }

    void handleNameChanged(Intent intent) {
        BluetoothDevice device;
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        synchronized (mDevices) {
            mDevices.add(device);
            adapter.notifyDataSetChanged();
        }
    }

    void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }
}
