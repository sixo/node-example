/*
 * Copyright (c) 2017, Roman Sisik
 * All rights reserved.
 * See LICENSE for more information.
 */

package eu.sisik.nodeexample;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.os.Process;

import java.util.ServiceConfigurationError;

public class NodeService extends Service {

    public static final String BROADCAST_STARTED = "node.broadcast.started";
    public static final String BROADCAST_FINISHED = "node.broadcast.finished";
    public static final int PORT = 8080;
    public static final int NOTIFICATION_ID = 66667;

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NOTIFICATION_ID, createNotification());

        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsPath = getCacheDir().getAbsolutePath() + "/main.js";
                Utils.copyAssetFile(getAssets(), "main.js", jsPath);
                startNode("node", jsPath, "Hello World", String.valueOf(PORT));
            }
        }).start();

        sendBroadcast(new Intent(BROADCAST_STARTED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(BROADCAST_FINISHED));

        // This ugly hack is for now necessary to kill node's process
        Process.killProcess(Utils.getPid(this, getString(R.string.node_process_name)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new Notification.Builder(this)
                .setContentTitle(this.getText(R.string.app_name))
                .setContentText(this.getText(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setTicker(this.getText(R.string.app_name))
                .build();
    }

    static {
        System.loadLibrary("node");
        System.loadLibrary("native-lib");
    }

    private native void startNode(String... argv);
}
