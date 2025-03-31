package com.example.bluetoothapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class BluetoothControl extends AppCompatActivity {

    ImageButton btn_forward, btn_left, btn_stop, btn_right, btn_backward;
    TextView tv_connected_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_control);

        tv_connected_status = findViewById(R.id.tv_connected_status);

        tv_connected_status.setText(getIntent().getStringExtra("device_name"));

        btn_backward = findViewById(R.id.btn_backward);
        btn_forward = findViewById(R.id.btn_forward);
        btn_left = findViewById(R.id.btn_left);
        btn_right = findViewById(R.id.btn_right);
        btn_stop = findViewById(R.id.btn_stop);

        // Handle button clicks to send data
        btn_forward.setOnClickListener(v -> sendCommand("F"));
        btn_backward.setOnClickListener(v -> sendCommand("B"));
        btn_left.setOnClickListener(v -> sendCommand("L"));
        btn_right.setOnClickListener(v -> sendCommand("R"));
        btn_stop.setOnClickListener(v -> sendCommand("S"));
    }

    private void sendCommand(String command) {
        try {
            BluetoothHelper.getInstance().sendData(command);
            Toast.makeText(this, command + " Command Sent", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error while sending command: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
