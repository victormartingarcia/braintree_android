package com.braintreecv1payments.api.interfaces;

import com.braintreecv1payments.api.models.AmericanExpressRewardsBalance;

/**
 * Interface that defines callbacks for {@link com.braintreecv1payments.api.AmericanExpress}.
 */
public interface AmericanExpressListener extends BraintreeListener {

    /**
     * Will be called when
     * {@link com.braintreecv1payments.api.models.AmericanExpressRewardsBalance} has been successfully fetched.
     */
    void onRewardsBalanceFetched(AmericanExpressRewardsBalance rewardsBalance);

}
