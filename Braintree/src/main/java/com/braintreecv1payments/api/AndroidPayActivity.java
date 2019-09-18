package com.braintreecv1payments.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import java.util.ArrayList;

@Deprecated
public class AndroidPayActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String EXTRA_ENVIRONMENT = "com.braintreecv1payments.api.EXTRA_ENVIRONMENT";
    protected static final String EXTRA_MERCHANT_NAME = "com.braintreecv1payments.api.EXTRA_MERCHANT_NAME";
    protected static final String EXTRA_CART = "com.braintreecv1payments.api.EXTRA_CART";
    protected static final String EXTRA_TOKENIZATION_PARAMETERS = "com.braintreecv1payments.api.EXTRA_TOKENIZATION_PARAMETERS";
    protected static final String EXTRA_ALLOWED_CARD_NETWORKS = "com.braintreecv1payments.api.EXTRA_ALLOWED_CARD_NETWORKS";
    protected static final String EXTRA_SHIPPING_ADDRESS_REQUIRED = "com.braintreecv1payments.api.EXTRA_SHIPPING_ADDRESS_REQUIRED";
    protected static final String EXTRA_PHONE_NUMBER_REQUIRED = "com.braintreecv1payments.api.EXTRA_PHONE_NUMBER_REQUIRED";
    protected static final String EXTRA_ALLOWED_COUNTRIES = "com.braintreecv1payments.api.EXTRA_ALLOWED_COUNTRIES";
    protected static final String EXTRA_GOOGLE_TRANSACTION_ID = "com.braintreecv1payments.api.EXTRA_GOOGLE_TRANSACTION_ID";

    protected static final String EXTRA_ERROR = "com.braintreecv1payments.api.EXTRA_ERROR";
    protected static final int RESULT_ERROR = 2;

    protected static final String EXTRA_REQUEST_TYPE = "com.braintreecv1payments.api.EXTRA_REQUEST_TYPE";
    protected static final int AUTHORIZE = 1;
    protected static final int CHANGE_PAYMENT_METHOD = 2;
    private static final int FULL_WALLET_REQUEST = 3;

    private static final String EXTRA_RECREATING = "com.braintreecv1payments.api.EXTRA_RECREATING";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGoogleApiClient();

        if (savedInstanceState != null && savedInstanceState.getBoolean(EXTRA_RECREATING)) {
            return;
        }

        int requestType = getIntent().getIntExtra(EXTRA_REQUEST_TYPE, -1);
        switch (requestType) {
            case AUTHORIZE:
                loadMaskedWallet();
                break;
            case CHANGE_PAYMENT_METHOD:
                changeMaskedWallet();
                break;
            default:
                setResult(RESULT_ERROR, new Intent().putExtra(EXTRA_ERROR,
                        "EXTRA_REQUEST_TYPE contained an unexpected type: " + requestType));
                finish();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_RECREATING, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    private void loadMaskedWallet() {
        MaskedWalletRequest.Builder maskedWalletRequestBuilder = MaskedWalletRequest.newBuilder()
                .setMerchantName(getIntent().getStringExtra(EXTRA_MERCHANT_NAME))
                .setCurrencyCode(getCart().getCurrencyCode())
                .setCart(getCart())
                .setEstimatedTotalPrice(getCart().getTotalPrice())
                .setShippingAddressRequired(getIntent().getBooleanExtra(EXTRA_SHIPPING_ADDRESS_REQUIRED, false))
                .setPhoneNumberRequired(getIntent().getBooleanExtra(EXTRA_PHONE_NUMBER_REQUIRED, false))
                .setPaymentMethodTokenizationParameters((PaymentMethodTokenizationParameters) getIntent()
                        .getParcelableExtra(EXTRA_TOKENIZATION_PARAMETERS))
                .addAllowedCardNetworks(getIntent().getIntegerArrayListExtra(EXTRA_ALLOWED_CARD_NETWORKS))
                .addAllowedCountrySpecificationsForShipping((ArrayList) getIntent()
                        .getParcelableArrayListExtra(EXTRA_ALLOWED_COUNTRIES));

        Wallet.Payments.loadMaskedWallet(mGoogleApiClient, maskedWalletRequestBuilder.build(), AUTHORIZE);
    }

    private void changeMaskedWallet() {
        Wallet.Payments.changeMaskedWallet(mGoogleApiClient, getIntent().getStringExtra(EXTRA_GOOGLE_TRANSACTION_ID),
                null, CHANGE_PAYMENT_METHOD);
    }

    private void loadFullWallet(String googleTransactionId) {
        FullWalletRequest.Builder fullWalletRequestBuilder = FullWalletRequest.newBuilder()
                .setCart(getCart())
                .setGoogleTransactionId(googleTransactionId);

        Wallet.Payments.loadFullWallet(mGoogleApiClient, fullWalletRequestBuilder.build(), FULL_WALLET_REQUEST);
    }

    private void setupGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(getIntent().getIntExtra(EXTRA_ENVIRONMENT, WalletConstants.ENVIRONMENT_TEST))
                        .setTheme(WalletConstants.THEME_LIGHT)
                        .build())
                .build();

        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {
        setResult(RESULT_ERROR, new Intent().putExtra(EXTRA_ERROR, "Connection suspended: " + i));
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        setResult(RESULT_ERROR, new Intent().putExtra(EXTRA_ERROR, "Connection failed. " +
                connectionResult.getErrorMessage() + ". Code: " + connectionResult.getErrorCode()));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && (requestCode == AUTHORIZE || requestCode == CHANGE_PAYMENT_METHOD)) {
            String googleTransactionId = ((MaskedWallet) data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET))
                    .getGoogleTransactionId();
            loadFullWallet(googleTransactionId);
        } else if (resultCode == Activity.RESULT_OK && requestCode == FULL_WALLET_REQUEST) {
            data.putExtra(EXTRA_CART, getCart());
            setResult(resultCode, data);
            finish();
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    private Cart getCart() {
        return getIntent().getParcelableExtra(EXTRA_CART);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

