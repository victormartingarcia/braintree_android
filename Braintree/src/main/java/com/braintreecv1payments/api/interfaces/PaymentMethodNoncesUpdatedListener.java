package com.braintreecv1payments.api.interfaces;

import com.braintreecv1payments.api.BraintreeFragment;
import com.braintreecv1payments.api.models.PaymentMethodNonce;

import java.util.List;

/**
 * Interface that defines callbacks to be called when existing {@link PaymentMethodNonce}s are fetched.
 */
public interface PaymentMethodNoncesUpdatedListener extends BraintreeListener {

    /**
     * {@link #onPaymentMethodNoncesUpdated(List)} will be called with a list of {@link PaymentMethodNonce}s
     * as a callback when
     * {@link com.braintreecv1payments.api.TokenizationClient#getPaymentMethodNonces(BraintreeFragment)}
     * is called.
     *
     * @param paymentMethodNonces the {@link List} of {@link PaymentMethodNonce}s.
     */
    void onPaymentMethodNoncesUpdated(List<PaymentMethodNonce> paymentMethodNonces);
}
