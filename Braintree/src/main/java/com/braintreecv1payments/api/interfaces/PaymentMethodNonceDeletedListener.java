package com.braintreecv1payments.api.interfaces;

import com.braintreecv1payments.api.models.PaymentMethodNonce;

/**
 * Interface that defines callbacks to be called after a {@link PaymentMethodNonce} is deleted.
 */
public interface PaymentMethodNonceDeletedListener extends BraintreeListener {
    void onPaymentMethodNonceDeleted(PaymentMethodNonce paymentMethodNonce);
}