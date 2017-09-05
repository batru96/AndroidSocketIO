package com.example.gietb.android_tonghopsocketio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    public static Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            socket = IO.socket("http://192.168.20:118:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Button btnDangNhap = findViewById(R.id.button);
        final EditText edtName = findViewById(R.id.inputEdit);

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtName.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "NHU NOI", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(MainActivity.this, NoiDungActivity.class));
                }
            }
        });
    }
}
