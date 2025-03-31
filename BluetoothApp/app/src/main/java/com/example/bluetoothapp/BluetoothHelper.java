package com.example.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothHelper {
    private static final UUID HC05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothHelper instance;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    private BluetoothHelper() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothHelper getInstance() {
        if (instance == null) {
            instance = new BluetoothHelper();
        }
        return instance;
    }

    public boolean connectToDevice(String macAddress, android.content.Context context) throws IOException {
        if (macAddress == null || bluetoothAdapter == null) {
            return false;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

        // Check Bluetooth permissions
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Bluetooth permission not granted");
        }

        bluetoothSocket = device.createRfcommSocketToServiceRecord(HC05_UUID);
        bluetoothSocket.connect();
        outputStream = bluetoothSocket.getOutputStream();
        return true;
    }

    public void sendData(String data) throws IOException {
        if (outputStream != null) {
            outputStream.write(data.getBytes());
        }
    }

    public void closeConnection() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
        if (bluetoothSocket != null) {
            bluetoothSocket.close();
        }
    }
}
