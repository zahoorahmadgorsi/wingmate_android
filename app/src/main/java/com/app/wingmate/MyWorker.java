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
                    int random = new Random().nextInt(GlobalArray.quotes.size());
                    Log.e("random no",random+"");
                    GlobalArray.liveQuote.postValue(GlobalArray.quotes.get((int) (Math.random()*GlobalArray.quotes.size())).getQuote());
                }else if (counter == 1800){
                    //AppConstants.QUOTE_OF_THE_DAY = GlobalArray.quotes.get(1).getQuote();
                    int random = new Random().nextInt(GlobalArray.quotes.size());
                    Log.e("random no",random+"");
                    GlobalArray.liveQuote.postValue(GlobalArray.quotes.get(random).getQuote());
                    timer.cancel();
                }
                Log.e("TimerTask", String.valueOf(counter));
                counter++;
            }
        };
        timer.schedule(timerTask, 0, 1000);
        return Result.success() ;
    }
}
