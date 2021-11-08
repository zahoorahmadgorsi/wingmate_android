package com.app.wingmate;

import android.content.Context;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class WorkRequestSingleton {
    private static WorkRequest tRequest;
    //private constructor.
    private WorkRequestSingleton(){

        //Prevent form the reflection api.
        if (tRequest != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public synchronized static void createInstance(Context context){
        if (tRequest == null){ //if there is no instance available... create new one
            tRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                    .build();
            WorkManager
                    .getInstance(context)
                    .enqueue(tRequest);
        }
    }
}
