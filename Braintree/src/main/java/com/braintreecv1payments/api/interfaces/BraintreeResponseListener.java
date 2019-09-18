package com.braintreecv1payments.api.interfaces;

public interface BraintreeResponseListener<T> {

    void onResponse(T t);
}
