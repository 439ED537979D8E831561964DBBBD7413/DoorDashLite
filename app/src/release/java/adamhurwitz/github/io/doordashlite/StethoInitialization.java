package adamhurwitz.github.io.doordashlite;

import android.content.Context;

import okhttp3.OkHttpClient;

/**
 * Created by ahurwitz on 7/16/17.
 */

public class StethoInitialization {
    public static void checkToEnable(Context context) {
        //only enabled for debug build
    }
    public static OkHttpClient getStethoClient(){
        //only enabled for debug build
        return null;
    }
}
