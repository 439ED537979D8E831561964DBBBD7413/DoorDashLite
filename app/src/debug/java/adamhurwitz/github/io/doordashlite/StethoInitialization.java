package adamhurwitz.github.io.doordashlite;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by ahurwitz on 7/16/17.
 */

public class StethoInitialization {
    public static void checkToEnable(Context context) {
        Stetho.initializeWithDefaults(context);
    }
    public static OkHttpClient getStethoClient(){
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }
}
