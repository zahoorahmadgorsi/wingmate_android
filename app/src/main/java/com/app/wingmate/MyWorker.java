package com.app.wingmate;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.wingmate.utils.AppConstants;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MyWorker extends Worker {

    Context mcontext;
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mcontext = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int counter = 0;
            @Override
            public void run() {
                if (counter==900){
                    //AppConstants.QUOTE_OF_THE_DAY = GlobalArray.quotes.get(0).getQuote();
                    GlobalArray.liveQuote.postValue(GlobalArray.quotes.get(getRandomNo()).getQuote());
                }else if (counter == 1800){
                    //AppConstants.QUOTE_OF_THE_DAY = GlobalArray.quotes.get(1).getQuote();
                    GlobalArray.liveQuote.postValue(GlobalArray.quotes.get(getRandomNo()).getQuote());
                    timer.cancel();
                }
                Log.e("TimerTask", String.valueOf(counter));
                counter++;
            }
        };
        timer.schedule(timerTask, 0, 1000);
        return Result.success() ;
    }

    private int getRandomNo() {
        int min = 0;
        int max = GlobalArray.quotes.size()-1;
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
        return random_int;
    }
}
