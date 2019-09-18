package com.braintreecv1payments.api.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.braintreecv1payments.api.Venmo;
import com.paypalcv1.android.sdk.onetouch.core.PayPalOneTouchCore;

import org.json.JSONException;
import org.json.JSONObject;

public class AnalyticsEvent {

    private static final String SESSION_ID_KEY = "sessionId";
    private static final String DEVICE_NETWORK_TYPE_KEY = "deviceNetworkType";
    private static final String USER_INTERFACE_ORIENTATION_KEY = "userInterfaceOrientation";
    private static final String MERCHANT_APP_VERSION_KEY = "merchantAppVersion";
    private static final String PAYPAL_INSTALLED_KEY = "paypalInstalled";
    private static final String VENMO_INSTALLED_KEY = "venmoInstalled";

    int id;
    String event;
    long timestamp;
    JSONObject metadata;

    public AnalyticsEvent(Context context, String sessionId, String integration, String event) {
        this.event = "android." + integration + "." + event;
        this.timestamp = System.currentTimeMillis() / 1000;
        metadata = new JSONObject();
        try {
            metadata.put(SESSION_ID_KEY, sessionId)
                    .put(DEVICE_NETWORK_TYPE_KEY, getNetworkType(context))
                    .put(USER_INTERFACE_ORIENTATION_KEY, getUserOrientation(context))
                    .put(MERCHANT_APP_VERSION_KEY, getAppVersion(context))
                    .put(PAYPAL_INSTALLED_KEY, isPayPalInstalled(context))
                    .put(VENMO_INSTALLED_KEY, Venmo.isVenmoInstalled(context));
        } catch (JSONException ignored) {}
    }

    public AnalyticsEvent() {
        metadata = new JSONObject();
    }

    public String getIntegrationType() {
        String[] eventSegments = this.event.split("\\.");
        if (eventSegments.length > 1) {
            return eventSegments[1];
        } else {
            return "";
        }
    }

    private String getNetworkType(Context context) {
        String networkType = null;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            networkType = networkInfo.getTypeName();
        }
        if (networkType == null) {
            networkType = "none";
        }
        return networkType;
    }

    private String getUserOrientation(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                return "Portrait";
            case Configuration.ORIENTATION_LANDSCAPE:
                return "Landscape";
            default:
                return "Unknown";
        }
    }

    private String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "VersionUnknown";
        }
    }

    private boolean isPayPalInstalled(Context context) {
        try {
            Class.forName(PayPalOneTouchCore.class.getName());
            return PayPalOneTouchCore.isWalletAppInstalled(context);
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
            return false;
        }
    }
}
