package com.braintreecv1payments.api.interfaces;

import com.braintreecv1payments.api.models.PaymentMethodNonce;

/**
 * Interface that defines callbacks to be called when {@link PaymentMethodNonce}s are created.
 */
public interface PaymentMethodNonceCreatedListener extends BraintreeListener {

    /**
     * {@link #onPaymentMethodNonceCreated} will be called when a new {@link PaymentMethodNonce} has been
     * created.
     *
     * @param paymentMethodNonce the {@link PaymentMethodNonce}.
     */
    void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce);
}
