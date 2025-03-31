package com.example.bluetoothapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ListPairedDevices extends AppCompatActivity {

    ListView devicelist;
    BluetoothAdapter bluetoothAdapter;
    private static final int BT_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;
    ArrayList<String> devices;
    ArrayList<String> deviceMAC;
    ArrayList<String> deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_paired_devices);

        devicelist = findViewById(R.id.devicelist);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if Bluetooth is supported
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device.", Toast.LENGTH_LONG).show();
            return;
        }

        // Check permissions and Bluetooth state
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_CODE);
                return;
            }
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent requestBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(requestBT, BT_REQUEST_CODE);
        } else {
            listpaireddevices();
        }

        // Handle item click
        devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String macaddress = deviceMAC.get(position);
                String name = deviceName.get(position);

                Intent intent = new Intent(ListPairedDevices.this, MainActivity.class);
                intent.putExtra("MAC_ADDRESS", macaddress);
                intent.putExtra("NAME",name);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BT_REQUEST_CODE && resultCode == RESULT_OK) {
            listpaireddevices();
        } else if (requestCode == BT_REQUEST_CODE) {
            Toast.makeText(this, "Bluetooth must be enabled to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listpaireddevices();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access Bluetooth devices.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void listpaireddevices() {
        devices = new ArrayList<>();
        deviceMAC = new ArrayList<>();
        deviceName = new ArrayList<>();

        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();

        if (deviceSet.size() > 0) {
            for (BluetoothDevice bluetoothDevice : deviceSet) {
                devices.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                deviceMAC.add(bluetoothDevice.getAddress());
                deviceName.add(bluetoothDevice.getName());
            }
        } else {
            Toast.makeText(this, "No paired devices found.", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                devices);

        devicelist.setAdapter(adapter);
    }
}
