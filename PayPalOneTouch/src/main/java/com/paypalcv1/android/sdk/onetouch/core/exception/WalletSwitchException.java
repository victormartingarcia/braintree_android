package com.paypalcv1.android.sdk.onetouch.core.exception;

/**
 * Exception for whenever the Wallet app has returned an 'error' in its response.
 */
public class WalletSwitchException extends Exception {

    public WalletSwitchException(String detailMessage) {
        super(detailMessage);
    }

    @Deprecated
    public WalletSwitchException(Throwable throwable) {
        super(throwable);
    }
}
