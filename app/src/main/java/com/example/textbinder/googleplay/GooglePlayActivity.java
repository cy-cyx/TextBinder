package com.example.textbinder.googleplay;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.textbinder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import billing.IInAppBillingService;

import static com.example.textbinder.googleplay.Constant.ITEM_TYPE_INAPP;
import static com.example.textbinder.googleplay.Constant.developerPayload;
import static com.example.textbinder.googleplay.Constant.product;

/**
 * create by cy
 * time : 2020/1/19
 * version : 1.0
 * Features : 由于谷歌Dome中涉及谷歌账号已经被封了，购买流程可能受影响
 */
public class GooglePlayActivity extends AppCompatActivity implements View.OnClickListener {
    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    BroadcastReceiver mBroadcastReceiver;

    String packageName;

    TextView LogView;//显示log的view
    TextView isVip;//显示是否是Vip
    Button GetView;//购买Vip的按钮

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        packageName = getPackageName();
        initView();
        initHandler();
        //建立连接
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);

                // check for in-app billing v3 support
                try {
                    int response = mService.isBillingSupported(3, packageName, ITEM_TYPE_INAPP);

                    if (response == 0) {
                        showLog("关联Google play store成功");
                    } else {
                        showLog("关联Google play store不成功");
                    }
                    if (response == 0) {
                        showLog("正在获取用户已经拥有的商品");
                        Bundle purchases = mService.getPurchases(3, packageName, ITEM_TYPE_INAPP, null);
                        int response_code = (int) purchases.get("RESPONSE_CODE");//判断是否请求成功！！！
                        if (response_code == 0) {
                            showLog("已成功获取用户已经拥有的商品");
                            ArrayList<String> ownedSkus = purchases.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                            for (String thisResponse : ownedSkus) {
                                if (thisResponse.equals(product)) {
                                    isVip.setText("是");
                                }
                                showLog("拥有" + thisResponse);
                            }
                        }
                        showLog("开始拉取vip的商品信息。。。");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<String> skuList = new ArrayList<String>();
                                skuList.add(product);
                                Bundle querySkus = new Bundle();
                                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                                try {
                                    Bundle skuDetails = mService.getSkuDetails(3, packageName, ITEM_TYPE_INAPP, querySkus);
                                    int response = skuDetails.getInt("RESPONSE_CODE");
                                    Message message = new Message();
                                    message.what = 0x999;
                                    message.arg1 = response;
                                    handler.sendMessage(message);
                                    if (response == 0) {
                                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                                        for (String thisResponse : responseList) {
                                            JSONObject object = new JSONObject(thisResponse);
                                            String sku = object.getString("productId");
                                            String price = object.getString("price");
                                            if (sku.equals(product)) {
                                                ArrayList<String> obj = new ArrayList<>();
                                                obj.add(0, sku);
                                                obj.add(1, price);
                                                Message message1 = new Message();
                                                message1.what = 0x998;
                                                message1.obj = obj;
                                                handler.sendMessage(message1);
                                            }
                                        }
                                    }

                                } catch (RemoteException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } catch (RemoteException e) {
                    showLog(e.toString());
                }

            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");

        boolean b = bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        if (!b) {
            showLog("可能不存在谷歌商店");
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showLog("正在获取用户已经拥有的商品");
                Bundle purchases = null;
                try {
                    purchases = mService.getPurchases(3, packageName, ITEM_TYPE_INAPP, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                int response_code = (int) purchases.get("RESPONSE_CODE");//判断是否请求成功！！！
                if (response_code == 0) {
                    showLog("已成功获取用户已经拥有的商品");
                    ArrayList<String> ownedSkus = purchases.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    for (String thisResponse : ownedSkus) {
                        if (thisResponse.equals(product)) {
                            isVip.setText("是");
                            showLog("拥有" + thisResponse);
                            return;
                        }
                    }
                    isVip.setText("否");
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter promoFilter = new IntentFilter("com.android.vending.billing.PURCHASES_UPDATED");
        registerReceiver(mBroadcastReceiver, promoFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    protected void initView() {
        LogView = findViewById(R.id.tv_logView);
        isVip = findViewById(R.id.tv_isVIP);
        isVip.setText("否");
        GetView = findViewById(R.id.bu_buyVIP);
        GetView.setOnClickListener(this);
        GetView.setEnabled(false);
    }

    @SuppressLint("HandlerLeak")
    protected void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x999:
                        if (msg.arg1 == 0) {
                            showLog("拉取vip信息成功");
                        } else {
                            showLog("拉取vip信息失败");
                        }
                        break;
                    case 0x998:
                        ArrayList a = (ArrayList) msg.obj;
                        String productId = (String) a.get(0);
                        String price = (String) a.get(1);
                        showLog(productId + "的价格：" + price);
                        GetView.setEnabled(true);
                        break;
                }
            }
        };
    }

    public void showLog(String s) {
        LogView.setText(LogView.getText() + "\n" + s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mServiceConn);
    }

    @Override
    public void onClick(View view) {
        try {
            showLog("开始请求购买");
            Bundle buyIntentBundle = mService.getBuyIntent(3, packageName, product, ITEM_TYPE_INAPP, developerPayload);
            int response_code = buyIntentBundle.getInt("RESPONSE_CODE");
            showLog(response_code + "");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent == null) {
                showLog("错误");
            } else {
                showLog("成功");
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            }
        } catch (RemoteException | IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                showLog("回调成功");

                int Code = data.getIntExtra("RESPONSE_CODE", 0);
                switch (Code) {
                    case 0:
                        showLog("付款成功");
                        isVip.setText("是");
                        String inapp_purchase_data = data.getStringExtra("INAPP_PURCHASE_DATA");
                        showLog("订单详情：" + inapp_purchase_data);
                        break;
                    case 7:
                        showLog("你已经拥有该商品");
                        break;
                    default:
                        showLog(Code + " ");
                }
            } else {
                showLog("回调失败");
            }
        }
    }
}
