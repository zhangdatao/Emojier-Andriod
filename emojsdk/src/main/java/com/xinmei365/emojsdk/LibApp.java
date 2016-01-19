package com.xinmei365.emojsdk;

/**
 * Created by xinmei on 15/12/1.
 */

import android.app.Application;
import android.content.Context;

/**
 * Base class for those who need to maintain global application state.
 */
public class LibApp extends Application {

    /** Instance of the current application. */
    private static LibApp instance;

    /**
     * Constructor.
     */
    public LibApp() {
        instance = this;
    }

    /**
     * Gets the application context.
     *
     * @return the application context
     */
    public static Context getContext() {
        return instance;
    }

}