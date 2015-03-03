package com.cs400.gamifyhealth;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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

//Class is a service which runs on a background thread and calculates random attacks over 48 or so hours
public class AttackService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private HandlerThread thread;
    private static AttackService instance;
    private static long remainingTime;
    private static long recentTime;
    private Random randomGen;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor mEditor;

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
            postAttack(AttackService.this);
            Message m = mServiceHandler.obtainMessage();
            m.arg1 = genTime();
            m.what = 0;
            mServiceHandler.sendMessage(m);
        }

    }

    //Posts a notification of an attack
    private void postAttack(Context context) {
        int attacks = sharedPrefs.getInt("ATTACKS",0);
        if (attacks<3){
            mEditor.putInt("ATTACKS",attacks+1);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("FitFrontier")
                            .setSmallIcon(R.drawable.attack_icon)
                            .setContentText("Your city has been damaged!");
            int mNotificationId = 001;
// Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
            mEditor.commit();
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
        sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mEditor = sharedPrefs.edit();
        long time = sharedPrefs.getLong("REMAINING_TIME",0);
        int time2 = (int) time;
        recentTime = sharedPrefs.getLong("LOGGED_TIME",0);
        if (time2!=0){
            time2-=System.currentTimeMillis()-recentTime;
            msg.arg1 = time2;
        }else{
            msg.arg1 = genTime();
        }
        msg.what = 0;
        mServiceHandler.sendMessage(msg);
        Log.d("TAG","NEXT ATTACK IN :"+msg.arg1);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {

    }

    public int genTime() {
        //return 300000;
        return (randomGen.nextInt(840) + 2160) * 60000;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        logTime();
    }

    //Logs the most recent remaining time in shared prefs
    //Need this because the OS can suspend/restart a service and we need to know at what point this occured
    private void logTime() {
        mEditor.putLong("REMAINING_TIME",remainingTime);
        mEditor.putLong("LOGGED_TIME",recentTime);
        mEditor.commit();
    }
}
