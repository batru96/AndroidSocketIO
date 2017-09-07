package com.example.gietb.android_tonghopsocketio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    public static Socket mSocket;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mSocket = IO.socket("http://192.168.20.100:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();

        mSocket.on("SERVER_SEND_THAT_BAI", dangNhapThatBaiListener);
        mSocket.on("SERVER_SEND_THANH_CONG", dangNhapThanhCongListener);

        Button btnDangNhap = findViewById(R.id.button);
        final EditText edtName = findViewById(R.id.inputEdit);

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtName.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "NHU NOI", Toast.LENGTH_SHORT).show();
                } else {
                    name = edtName.getText().toString();
                    mSocket.emit("USER_SEND_NAME", name);
                }
            }
        });
    }

    private Socket.Listener dangNhapThanhCongListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray namesArray = (JSONArray) args[0];
                    Intent intent = new Intent(MainActivity.this, NoiDungActivity.class);
                    intent.putExtra("DANH_SACH", namesArray.toString());
                    intent.putExtra("TEN", name);
                    startActivity(intent);
                }
            });
        }
    };

    private Socket.Listener dangNhapThatBaiListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "USER_DA_TON_TAI", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
