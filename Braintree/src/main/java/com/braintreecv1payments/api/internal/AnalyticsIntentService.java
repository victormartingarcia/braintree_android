package com.braintreecv1payments.api.internal;

import android.app.IntentService;
import android.content.Intent;

import com.braintreecv1payments.api.exceptions.InvalidArgumentException;
import com.braintreecv1payments.api.models.Authorization;
import com.braintreecv1payments.api.models.Configuration;

import org.json.JSONException;

public class AnalyticsIntentService extends IntentService {

    public static final String EXTRA_AUTHORIZATION =
            "com.braintreecv1payments.api.internal.AnalyticsIntentService.EXTRA_AUTHORIZATION";
    public static final String EXTRA_CONFIGURATION =
            "com.braintreecv1payments.api.internal.AnalyticsIntentService.EXTRA_CONFIGURATION";

    public AnalyticsIntentService() {
        super(AnalyticsIntentService.class.getSimpleName());
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        try {
            Authorization authorization = Authorization.fromString(intent.getStringExtra(EXTRA_AUTHORIZATION));
            Configuration configuration = Configuration.fromJson(intent.getStringExtra(EXTRA_CONFIGURATION));

            AnalyticsSender.send(this, authorization, new BraintreeHttpClient(authorization),
                    configuration.getAnalytics().getUrl(), true);
        } catch (InvalidArgumentException | JSONException ignored) {}
    }
}
