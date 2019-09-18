package com.braintreecv1payments.api.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.braintreecv1payments.testutils.SharedPreferencesHelper.getSharedPreferences;

@SuppressWarnings("deprecation")
public class BraintreeActivityTestRule<T extends Activity> extends ActivityTestRule<T> {

    private KeyguardLock mKeyguardLock;

    public BraintreeActivityTestRule(Class<T> activityClass) {
        super(activityClass);
        init();
    }

    public BraintreeActivityTestRule(Class<T> activityClass, boolean initialTouchMode,
            boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
        init();
    }

    @SuppressWarnings("MissingPermission")
    @SuppressLint({"MissingPermission", "ApplySharedPref"})
    private void init() {
        getSharedPreferences(getTargetContext()).edit().clear().commit();

        mKeyguardLock = ((KeyguardManager) getTargetContext().getSystemService(Context.KEYGUARD_SERVICE))
                .newKeyguardLock("BraintreeActivityTestRule");
        mKeyguardLock.disableKeyguard();
    }

    @SuppressWarnings("MissingPermission")
    @SuppressLint({"MissingPermission", "ApplySharedPref"})
    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();

        getSharedPreferences(getTargetContext()).edit().clear().commit();

        mKeyguardLock.reenableKeyguard();
    }
}
