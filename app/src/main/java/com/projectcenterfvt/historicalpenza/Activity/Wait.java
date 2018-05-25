package com.projectcenterfvt.historicalpenza.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.projectcenterfvt.historicalpenza.DataBases.DB_Position;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.R;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by vovaa on 26.03.2018.
 */


public class Wait  extends AppCompatActivity {

    int WAIT_TIME = 100;

    public static Wait ptrOfTransisiton = null;
    long time = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ptrOfTransisiton = this;
        timeExam();
    }

   private void timeExam(){
        for(;;){
            if(System.currentTimeMillis() - time > WAIT_TIME){
                setContentView(R.layout.spinner);
                return;
            }
        }
   }
}
