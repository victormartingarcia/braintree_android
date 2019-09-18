package com.braintreecv1payments.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.braintreecv1payments.api.BraintreeFragment;
import com.braintreecv1payments.api.LocalPayment;
import com.braintreecv1payments.api.exceptions.InvalidArgumentException;
import com.braintreecv1payments.api.interfaces.BraintreeResponseListener;
import com.braintreecv1payments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreecv1payments.api.models.LocalPaymentRequest;
import com.braintreecv1payments.api.models.LocalPaymentResult;
import com.braintreecv1payments.api.models.PaymentMethodNonce;
import com.braintreecv1payments.api.models.PostalAddress;

public class IdealActivity extends BaseActivity implements PaymentMethodNonceCreatedListener {

    private Button mIdealButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideal_activity);

        mIdealButton = (Button) findViewById(R.id.ideal_button);
    }

    @Override
    protected void reset() {
        mIdealButton.setEnabled(false);
    }

    @Override
    protected void onAuthorizationFetched() {
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, Settings.getEnvironmentTokenizationKeyForLocalPayment(getApplicationContext()));
            mIdealButton.setEnabled(true);
        } catch (InvalidArgumentException e) {
            onError(e);
        }
    }

    public void launchIdeal(View v) {
        PostalAddress address = new PostalAddress()
                .streetAddress("836486 of 22321 Park Lake")
                .countryCodeAlpha2("NL")
                .locality("Den Haag")
                .postalCode("2585 GJ");
        LocalPaymentRequest request = new LocalPaymentRequest()
                .paymentType("ideal")
                .amount("1.10")
                .address(address)
                .phone("639847934")
                .email("lingo-buyer@paypal.com")
                .givenName("Linh")
                .surname("Ngo")
                .shippingAddressRequired(true)
                .currencyCode("EUR");
        LocalPayment.startPayment(mBraintreeFragment, request, new BraintreeResponseListener<LocalPaymentRequest>() {
            @Override
            public void onResponse(LocalPaymentRequest localPaymentRequest) {
                LocalPayment.approvePayment(mBraintreeFragment, localPaymentRequest);
            }
        });
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        super.onPaymentMethodNonceCreated(paymentMethodNonce);

        Intent intent = new Intent().putExtra(MainActivity.EXTRA_PAYMENT_RESULT, paymentMethodNonce);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static String getDisplayString(LocalPaymentResult nonce) {
        return "First name: " + nonce.getGivenName() + "\n" +
                "Last name: " + nonce.getSurname() + "\n" +
                "Email: " + nonce.getEmail() + "\n" +
                "Phone: " + nonce.getPhone() + "\n" +
                "Payer id: " + nonce.getPayerId() + "\n" +
                "Client metadata id: " + nonce.getClientMetadataId() + "\n" +
                "Billing address: " + formatAddress(nonce.getBillingAddress()) + "\n" +
                "Shipping address: " + formatAddress(nonce.getShippingAddress());
    }

    private static String formatAddress(PostalAddress address) {
        return address.getRecipientName() + " " +
                address.getStreetAddress() + " " +
                address.getExtendedAddress() + " " +
                address.getLocality() + " " +
                address.getRegion() + " " +
                address.getPostalCode() + " " +
                address.getCountryCodeAlpha2();
    }
}
