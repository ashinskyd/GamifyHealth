package com.cs400.gamifyhealth;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

import java.util.Random;

public class AttackEngine extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private HandlerThread thread;
    private boolean running;
    private int counter;
    public Random randomGen;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //This is where we receive posted messages
            long endTime = System.currentTimeMillis() + msg.arg1;
            while (System.currentTimeMillis() < endTime) {
                //While we are not generating an attack we will wait in this state
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
            Log.d("TAG", "Attack Posted!");
            Message m = mServiceHandler.obtainMessage();
            m.arg1 = genTime();
            mServiceHandler.sendMessage(m);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        randomGen = new Random();
        running =false;
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = genTime();
        mServiceHandler.sendMessage(msg);
        // If we get killed, after returning from here, restart
        Log.d("TAG", "OnCreate Called!");
        running = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "Stub Start Called!");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public int genTime() {
        //return (randomGen.nextInt(2880) + 2880) * 60000;
        return randomGen.nextInt(5000);
    }


}
