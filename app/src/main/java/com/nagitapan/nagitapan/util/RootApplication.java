package com.nagitapan.nagitapan.util;
/**
 * Created by Roshane De Silva on 09/03/2016.
 */

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

public class RootApplication extends Application {
    private static RootApplication mInstance;
    private Typeface arkhip;

    /**
     * Singleton method for Root Application
     *
     * @return RootApplication
     */
    public static synchronized RootApplication getInstance() {
        return mInstance;
    }

    /**
     * Get the Common Application Context
     *
     * @return Context
     */
    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        setFontFace();

    }

    private void setFontFace() {
        String arkhipPath = "fonts/Arkhip_font.otf";
        arkhip = Typeface.createFromAsset(getAssets(), arkhipPath);

    }

    /**
     * Get the Font Type Face - Arkhip
     *
     * @return type face
     */
    public Typeface getArkhip() {
        return arkhip;
    }


}