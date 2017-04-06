package com.cphandheld.johnc.appaugmentedreality;



import android.app.Application;

/**
 * Created by jcobb on 4/6/17.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


}