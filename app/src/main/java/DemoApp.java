import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by xinmei on 16/1/5.
 */
public class DemoApp extends Application {

    public static Context applicationContext;
    private static DemoApp instance;
    private static boolean mIsLive;


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;

        //init demo helper
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static DemoApp getInstance() {
        return instance;
    }

    public static boolean isIsLive() {
        return mIsLive;
    }

    public static void setIsLive(boolean isLive) {
        mIsLive = isLive;
    }
}
