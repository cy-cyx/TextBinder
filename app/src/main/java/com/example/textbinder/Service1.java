package com.example.textbinder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * create by cy
 * time : 2020/1/19
 * version : 1.0
 * Features :
 */
public class Service1 extends Service {

    private MyBinder myBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new MyBinder();
        Log.d("xx", "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("xx", "onStartCommand: ");

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyBinder extends Binder {

        public String getName() {
            return "service1";
        }

    }
}
