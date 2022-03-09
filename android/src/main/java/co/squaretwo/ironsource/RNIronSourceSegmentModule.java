package co.squaretwo.ironsource;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceSegment;

public class RNIronSourceSegmentModule extends ReactContextBaseJavaModule {

    public RNIronSourceSegmentModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNIronSourceSegment";
    }

    private IronSourceSegment segment;

    @ReactMethod
    public void create() {
        segment = new IronSourceSegment();
    }

    @ReactMethod
    public void setSegmentName(String name) {
        segment.setSegmentName(name);
    }

    @ReactMethod
    public void setCustomValue(String value, String key) {
        segment.setCustom(key, value);
    }

    @ReactMethod
    public void activate() {
        if (segment != null) {
            IronSource.setSegment(segment);
        }
    }

    @ReactMethod
    public void addListener(String eventName) {
      // Keep: Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    public void removeListeners(double count) {
      // Keep: Required for RN built in Event Emitter Calls.
    }
}
