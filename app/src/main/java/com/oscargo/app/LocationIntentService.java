package com.oscargo.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationIntentService extends IntentService {
    int counter = 0;
    private Handler handler;
    private Runnable runnable;





    public LocationIntentService() {
        super("LocationIntentService");
    }

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LocationIntentService.class);
        intent.putExtra("param1", param1);
        intent.putExtra("param2", param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            System.out.println("before");
            Thread.sleep(1000);
            System.out.println("after");



            new CountDownTimer(50000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");
                    long hour = (millisUntilFinished / 3600000) % 24;
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;

                    System.out.println("tiempo: "+f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                }
                // When the task is over it will print 00:00:00 there
                public void onFinish() {

                    System.out.println("tiempo: 00:00:00");
                }
            }.start();




        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
        if (intent != null) {
            System.out.println("intent != null");
                final String param1 = intent.getStringExtra("param1");
                final String param2 = intent.getStringExtra("param2");
                handleActionFoo(param1, param2);
        }else{

            System.out.println("intent null");
        }
    }

    private void handleActionFoo(String param1, String param2) {




        System.out.println("Handle action Foo");

        countDownStart();
    }


    public void countDownStart() {





    }

}