package com.iglassus.exoplayerfilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by AdminUser on 3/22/2018.
 */

public class UsbChangeNotification extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        int delay=0;

        if (intent.getAction().equals("android.hardware.usb.action.USB_STATE")) {
            boolean connected = intent.getExtras().getBoolean("connected");
            //Toast.makeText(context, "USB 变化", Toast.LENGTH_LONG).show();
            if (connected) {
                //Toast.makeText(context, "USB 设备连接", Toast.LENGTH_SHORT).show();
                //delay=2000;
            }else {
                //Toast.makeText(context, "USB 设备拔出或关闭", Toast.LENGTH_SHORT).show();
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Intent intentStartGlass = new Intent(context, MainActivity.class);
                context.startActivity(intentStartGlass);
            }
        }, delay);
    }
}