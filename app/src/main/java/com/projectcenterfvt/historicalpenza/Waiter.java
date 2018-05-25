package com.projectcenterfvt.historicalpenza;

import android.content.Context;
import android.content.Intent;


import com.projectcenterfvt.historicalpenza.Activity.Wait;

import java.util.TimerTask;

public class Waiter extends TimerTask {

    @Override
    public void run() {

    }

    public static void createWaiter(Context context){
        Intent intent = new Intent(context, Wait.class);
        context.startActivity(intent);
    }

    public static void closeWaiter(){
            Wait.ptrOfTransisiton.finish();
    }
}
