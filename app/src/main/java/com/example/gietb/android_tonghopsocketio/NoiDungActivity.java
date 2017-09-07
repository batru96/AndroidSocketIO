package com.example.gietb.android_tonghopsocketio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class NoiDungActivity extends AppCompatActivity {

    private ListView lvNames;
    private ArrayList<String> names;
    private ArrayAdapter<String> adapterNames;

    private ListView lvMessage;
    private ArrayList<String> messages;
    private ArrayAdapter<String> adapterMessages;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noi_dung);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnSend = findViewById(R.id.btnSend);
        final EditText edtMessage = findViewById(R.id.edtMessage);

        MainActivity.mSocket.on("SERVER_CO_NGUOI_DANG_XUAT", userDangXuatListener);
        MainActivity.mSocket.on("SERVER_SEND_NEW_USERNAME", newUserJoined);
        MainActivity.mSocket.on("SERVER_GLOBAL_SEND_MESSAGE", globalMessageListener);

        Intent intent = getIntent();
        userName = intent.getStringExtra("TEN");
        names = new ArrayList<>();
        String namesString = intent.getStringExtra("DANH_SACH");
        try {
            JSONArray arrayName = new JSONArray(namesString);
            for (int i = 0; i < arrayName.length(); i++) {
                names.add(arrayName.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvNames = findViewById(R.id.lvNames);
        adapterNames = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        lvNames.setAdapter(adapterNames);

        lvMessage = findViewById(R.id.lvMessage);
        messages = new ArrayList<>();
        adapterMessages = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
        lvMessage.setAdapter(adapterMessages);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString().trim();
                if (message.equals("")) return;
                String jsonObjString = "{\"name\": \"" + userName + "\", \"message\":\"" + message + "\"}";
                try {
                    JSONObject messObject = new JSONObject(jsonObjString);
                    MainActivity.mSocket.emit("USER_GLOBAL_SEND_MESSAGE", messObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                edtMessage.setText("");
            }
        });
    }

    private Emitter.Listener globalMessageListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        String message = object.getString("name") + ": " + object.getString("message");
                        messages.add(message);
                        adapterMessages.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    };
                }
            });
        }
    };

    private Emitter.Listener newUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String name = (String) args[0];
                    names.add(name);
                    adapterNames.notifyDataSetChanged();
                }
            });
        }
    };

    private Emitter.Listener userDangXuatListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String name = (String) args[0];
                    names.remove(name);
                    adapterNames.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        MainActivity.mSocket.emit("USER_DANG_XUAT");
        super.onBackPressed();
    }
}
