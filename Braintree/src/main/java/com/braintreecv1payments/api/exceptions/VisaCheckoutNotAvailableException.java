package com.braintreecv1payments.api.exceptions;

/**
 * Error class thrown when Visa Checkout classes are not available
 */
public class VisaCheckoutNotAvailableException extends BraintreeException {

    public VisaCheckoutNotAvailableException(String message) {
        super(message);
    }
}
