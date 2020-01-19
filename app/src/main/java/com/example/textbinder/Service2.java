package com.example.textbinder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

/**
 * create by cy
 * time : 2020/1/19
 * version : 1.0
 * Features :
 */
public class Service2 extends Service {

    private MyBinder myBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new MyBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public String getName() throws RemoteException {
            return "service2";
        }
    }
}
