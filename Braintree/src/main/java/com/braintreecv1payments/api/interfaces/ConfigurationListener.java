package com.braintreecv1payments.api.interfaces;

import com.braintreecv1payments.api.models.Configuration;

/**
 * Interface that defines a callback for {@link com.braintreecv1payments.api.models.Configuration}.
 */
public interface ConfigurationListener extends BraintreeListener {

    /**
     * {@link #onConfigurationFetched(Configuration)} will be called when
     * {@link com.braintreecv1payments.api.models.Configuration} has been successfully fetched.
     */
    void onConfigurationFetched(Configuration configuration);
}
