/*
 * Copyright (c) 2017, Roman Sisik
 * All rights reserved.
 * See LICENSE for more information.
 */

package eu.sisik.nodeexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Node;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBut = (Button) findViewById(R.id.but_start);
        ipPortTv = (TextView) findViewById(R.id.tv_ip_port);

        startBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, NodeService.class);
                if (!Utils.isServiceRunning(MainActivity.this, NodeService.class))
                    startService(i);
                else
                    stopService(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(NodeService.BROADCAST_STARTED);
        intentFilter.addAction(NodeService.BROADCAST_FINISHED);
        registerReceiver(nodeStatusReceiver, intentFilter);

        if (Utils.isServiceRunning(this, NodeService.class)) {
            startBut.setText(R.string.but_stop);
            // Try to guess the ip of the device on local network
            String ip = Utils.getIP();
            if (ip != null)
                ipPortTv.setText(ip + ":" + NodeService.PORT);
            else
                ipPortTv.setText(R.string.unknown_ip_port);
        } else {
            startBut.setText(R.string.but_start);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(nodeStatusReceiver);
    }

    private BroadcastReceiver nodeStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case NodeService.BROADCAST_FINISHED:
                    startBut.setText(R.string.but_start);
                    break;
                case NodeService.BROADCAST_STARTED:
                    startBut.setText(R.string.but_stop);
                    break;
            }
        }
    };

    private Button startBut;
    private TextView ipPortTv;
}
