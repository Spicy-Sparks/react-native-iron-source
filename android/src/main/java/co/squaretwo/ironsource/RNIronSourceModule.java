package co.squaretwo.ironsource;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import org.json.JSONException;

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
                IronSource.addImpressionDataListener(generateImpressionDataListener());

                IronSource.init(activity, appId);
                if (activity != null && validateIntegration) {
                    IntegrationHelper.validateIntegration(activity);
                }

                promise.resolve(null);
            }
        });
    }

    @ReactMethod
    public void addImpressionDataDelegate() {
        IronSource.addImpressionDataListener(generateImpressionDataListener());
    }

    @ReactMethod
    public void setUserId(final String userId) {
        IronSource.setUserId(userId);
    }

    @ReactMethod
    public void setConsent(boolean consent) {
        IronSource.setConsent(consent);
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @ReactMethod
    public void addListener(String eventName) {
      // Keep: Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    public void removeListeners(double count) {
      // Keep: Required for RN built in Event Emitter Calls.
    }

    private ImpressionDataListener generateImpressionDataListener(){
        return new ImpressionDataListener() {
            @Override
            public void onImpressionSuccess (ImpressionData impressionData){
                WritableMap data = Arguments.createMap();
                data.putDouble("revenue", impressionData.getRevenue());
                data.putString("ad_network", impressionData.getAdNetwork());
                try {
                    data.putMap("all_data", Utility.convertJsonToMap(impressionData.getAllData()));
                } catch (JSONException e) {

                }
                sendEvent("impressionDataDidSucceed", data);
            }
        };
    }
}
