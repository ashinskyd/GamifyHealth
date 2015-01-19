package com.cs400.gamifyhealth;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

import java.util.Random;

public class AttackService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private HandlerThread thread;
    private static AttackService instance;
    private static long remainingTime;
    private static long recentTime;
    private Random randomGen;

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
               remainingTime = endTime-System.currentTimeMillis();
                recentTime = System.currentTimeMillis();
            }
            Log.d("TAG", "Attack Posted!");
            AttackService.postAttack(AttackService.this);
            Message m = mServiceHandler.obtainMessage();
            m.arg1 = genTime();
            m.what = 0;
            mServiceHandler.sendMessage(m);
        }

    }

    //Posts a notification of an attack
    private static void postAttack(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
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
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        // If we get killed, after returning from here, restart

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        Log.d("TAG","STARTED");
        //Gets the most recent attack time interval (if available)
        //Only called when the service is being restarted
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        long time = sharedPref.getLong("REMAINING_TIME",0);
        int time2 = (int) time;
        recentTime = sharedPref.getLong("LOGGED_TIME",0);
        if (time2!=0){
            time2-=System.currentTimeMillis()-recentTime;
            msg.arg1 = time2;
        }else{
            msg.arg1 = genTime();
        }
        msg.what = 0;
        mServiceHandler.sendMessage(msg);
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
        return (randomGen.nextInt(2880) + 2880) * 60000;
        //return 20000;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        logTime();
    }

    //Logs the most recent remaining time in shared prefs
    //Need this because the OS can suspend/restart a service and we need to know at what point this occured
    private void logTime() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("REMAINING_TIME",remainingTime);
        editor.putLong("LOGGED_TIME",recentTime);
        editor.commit();
    }
}