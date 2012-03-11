
package jp.preety.ispants.bluetooth;

import java.util.ArrayList;
import java.util.List;

import jp.preety.ispants.R;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class BluetoothListPickerActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> data = new ArrayList<String>();
        for (BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            data.add(device.getAddress() + " " + device.getName());
        }
        if (data.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, data);
            setListAdapter(adapter);

            getListView().setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    String data = parent.getItemAtPosition(position).toString();
                    intent.putExtra("address", data.split(" ")[0]);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            Toast.makeText(this, R.string.device_not_found, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
