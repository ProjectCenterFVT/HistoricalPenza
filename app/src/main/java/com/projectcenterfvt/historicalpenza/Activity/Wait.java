package com.projectcenterfvt.historicalpenza.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.projectcenterfvt.historicalpenza.R;

/**
 * Created by vovaa on 26.03.2018.
 */


public class Wait  extends AppCompatActivity {

    public static Wait ptrOfTransisiton = null;
    int WAIT_TIME = 100;
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
