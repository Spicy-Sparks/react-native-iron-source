package co.squaretwo.ironsource;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;

public class RNIronSourceModule extends ReactContextBaseJavaModule {
    private static final String TAG = "RNIronSource";

    private ReactApplicationContext reactContext;

    public RNIronSourceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return TAG;
    }

    /**
       * Call from Activity.onResume:
       *   @Override
       *   public void onResume() {
       *     super.onResume();
       *     RNIronSourceModule.onResume(this);
       *   }
       */
    public static void onResume(Activity reactActivity) {
        IronSource.onResume(reactActivity);
    }

    /**
      * Call from Activity.onPause:
      *   @Override
      *   public void onPause() {
      *     super.onPause();
      *     RNIronSourceModule.onPause(this);
      *   }
      */
    public static void onPause(Activity reactActivity) {
      IronSource.onPause(reactActivity);
    }

    @ReactMethod
    public void initializeIronSource(final String appId, final String userId, final ReadableMap options, final Promise promise) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Activity activity = reactContext.getCurrentActivity();
                final boolean validateIntegration = options.getBoolean("validateIntegration");

                IronSource.setUserId(userId);
                IronSource.init(activity, appId);
                if (activity != null && validateIntegration) {
                    IntegrationHelper.validateIntegration(activity);
                }

                promise.resolve(null);
            }
        });
    }

    @ReactMethod
    public void setConsent(boolean consent) {
        IronSource.setConsent(consent);
    }
}
