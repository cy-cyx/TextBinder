package com.example.textbinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * create by cy
 * time : 2020/1/19
 * version : 1.0
 * Features :
 */
public class ServiceFactory {

    public static void textStartService(Context context) {
        context.startService(new Intent(context, Service1.class));
    }

    public static ServiceConnection textBindService(Context context) {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("xx", "onServiceConnected: " + ((Service1.MyBinder) service).getName());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        context.bindService(new Intent(context, Service1.class), serviceConnection, Context.BIND_AUTO_CREATE);
        return serviceConnection;
    }

    public static void textBindServiceProcess(Context context) {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IMyAidlInterface proxy = IMyAidlInterface.Stub.asInterface(service);
                try {
                    Log.d("xx", "onServiceConnected: " + proxy.getName());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        context.bindService(new Intent(context, Service2.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
