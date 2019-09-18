package com.braintreecv1payments.api.exceptions;

import com.braintreecv1payments.api.BraintreeFragment;
import com.braintreecv1payments.api.PaymentMethod;
import com.braintreecv1payments.api.models.PaymentMethodNonce;

/**
 * Error class thrown when a {@link PaymentMethod#deletePaymentMethod(BraintreeFragment, PaymentMethodNonce)}
 * fails to delete a payment method.
 */
public class PaymentMethodDeleteException extends Exception {

    private final PaymentMethodNonce mPaymentMethodNonce;

    public PaymentMethodDeleteException(PaymentMethodNonce paymentMethodNonce, Exception exception) {
        super(exception);
        mPaymentMethodNonce = paymentMethodNonce;
    }

    /**
     * @return The {@link PaymentMethodNonce} that failed to be deleted.
     */
    public PaymentMethodNonce getPaymentMethodNonce() {
        return mPaymentMethodNonce;
    }
}
