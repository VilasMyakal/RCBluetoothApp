package com.example.bluetoothapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btcontrol, linefollow, obsavoid, selectcar;
    private String selectedMacAddress = null;
    private String deviceName = null;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectcar = findViewById(R.id.selectcar);
        btcontrol = findViewById(R.id.btcontrol);
        linefollow = findViewById(R.id.linefollow);
        obsavoid = findViewById(R.id.obsavoid);

        requestBluetoothPermissions();

        // Handle device selection
        Intent intentData = getIntent();
        selectedMacAddress = intentData.getStringExtra("MAC_ADDRESS");
        deviceName = intentData.getStringExtra("NAME");

        // Update UI for selected car
        if (deviceName == null) {
            selectcar.setText("Select the Car!!");
        } else {
            selectcar.setText("Car: " + deviceName);
            Toast.makeText(MainActivity.this, "MAC Address: " + selectedMacAddress, Toast.LENGTH_SHORT).show();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (BluetoothHelper.getInstance().connectToDevice(selectedMacAddress, this)) {
                        Toast.makeText(this, "Connected to device", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to connect to device", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(this, "Error while connecting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(this, "Bluetooth permission not granted!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Handle button clicks
        selectcar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListPairedDevices.class);
            startActivity(intent);
        });

        btcontrol.setOnClickListener(v -> sendCommand("BT_CONTROL", BluetoothControl.class));
        linefollow.setOnClickListener(v -> sendCommand("LINE_FOLLOWER", null));
        obsavoid.setOnClickListener(v -> sendCommand("OBSTACLE_AVOID", null));
    }

    private void sendCommand(String command, Class<?> activityClass) {
        if (deviceName == null) {
            Toast.makeText(MainActivity.this, "Please select the Car!!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                BluetoothHelper.getInstance().sendData(command);
                Toast.makeText(MainActivity.this, command.replace("_", " ") + " Mode is ON!", Toast.LENGTH_SHORT).show();

                if (activityClass != null) {
                    Intent intent = new Intent(MainActivity.this, activityClass);
                    intent.putExtra("device_name", deviceName);
                    startActivity(intent);
                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error while sending data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.BLUETOOTH_CONNECT,
                                android.Manifest.permission.BLUETOOTH_SCAN}, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.BLUETOOTH}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth permission denied. Please allow it in Settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            BluetoothHelper.getInstance().closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
