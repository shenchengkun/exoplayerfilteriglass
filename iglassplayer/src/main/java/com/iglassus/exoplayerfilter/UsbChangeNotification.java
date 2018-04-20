package com.iglassus.exoplayerfilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by AdminUser on 3/22/2018.
 */

public class UsbChangeNotification extends BroadcastReceiver {
    public static boolean appIsRunning=false;
    private static String HDMIINTENT = "android.intent.action.HDMI_PLUGGED";

    @Override
    public void onReceive(final Context ctxt, Intent receivedIt) {
        String action = receivedIt.getAction();

        if (action.equals(HDMIINTENT)) {
            boolean state = receivedIt.getBooleanExtra("state", false);

            if (state == true) {
                Log.d("HDMIListner", "BroadcastReceiver.onReceive() : Connected HDMI-TV");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        ctxt.startActivity(new Intent(ctxt,IGLassMainActivity.class));
                    }
                }, 100);
                //Toast.makeText(ctxt, "HDMI >>", Toast.LENGTH_LONG).show();
            } else {
                Log.d("HDMIListner", "HDMI >>: Disconnected HDMI-TV");
                //Toast.makeText(ctxt, "HDMI DisConnected>>", Toast.LENGTH_LONG).show();
                if(IGLassMainActivity.app!=null) IGLassMainActivity.app.finishAndRemoveTask();
            }

        }
    }

}