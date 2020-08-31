package com.zilio.bttalk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Set;

public class activity_connect extends AppCompatActivity {
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter bt_adapter;
    private ArrayAdapter<String> discovered_device_arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_connect);

        discovered_device_arrayAdapter = new ArrayAdapter<>(this, R.layout.device_layout);
        ListView available_list = (ListView) findViewById(R.id.connect_available_list);
        available_list.setAdapter(discovered_device_arrayAdapter);
        available_list.setOnItemClickListener(paired_click_listener);

        bt_adapter = BluetoothAdapter.getDefaultAdapter();
        configure_broadcast();
        query_paired_bt();
        setSwitch();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bt_adapter != null) {
            bt_adapter.cancelDiscovery();
        }
        this.unregisterReceiver(bt_receiver);
    }

    private final BroadcastReceiver bt_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // If it's already paired, skip it, because it's been listed already
                    if (device != null && device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        discovered_device_arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    ((Switch)findViewById(R.id.connect_listen_switch)).setChecked(false);
                    break;
            }
        }
    };
    private void configure_broadcast(){
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bt_receiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(bt_receiver, filter);

    }

    private void query_paired_bt() {
        Set<BluetoothDevice> paired_devices = bt_adapter.getBondedDevices();

        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<>(this, R.layout.device_layout);

        ListView paired_list = (ListView) findViewById(R.id.paired_device_list);
        paired_list.setAdapter(pairedDevicesArrayAdapter);
        paired_list.setOnItemClickListener(paired_click_listener);

        if (paired_devices.size() > 0) {
            for (BluetoothDevice device : paired_devices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else pairedDevicesArrayAdapter.add(getResources().getText(R.string.connect_none_paired).toString());

    }

    // responds to clicking a device in the lists
    private AdapterView.OnItemClickListener paired_click_listener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            if (bt_adapter.isDiscovering())
                bt_adapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.split("\n")[1];

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
        }
    };

    //responds to Listen switch toggle
    private void setSwitch () {
        Switch toggle = (Switch) findViewById(R.id.connect_listen_switch);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bt_adapter.startDiscovery();
                } else {
                    if (bt_adapter.isDiscovering())
                        bt_adapter.cancelDiscovery();
                }
            }
        });
    }
}