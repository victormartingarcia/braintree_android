package com.braintreecv1payments.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreecv1payments.api.PayPal;
import com.braintreecv1payments.api.dropin.DropInActivity;
import com.braintreecv1payments.api.dropin.DropInRequest;
import com.braintreecv1payments.api.dropin.DropInResult;
import com.braintreecv1payments.api.dropin.utils.PaymentMethodType;
import com.braintreecv1payments.api.models.AndroidPayCardNonce;
import com.braintreecv1payments.api.models.BraintreePaymentResult;
import com.braintreecv1payments.api.models.CardNonce;
import com.braintreecv1payments.api.models.GooglePaymentCardNonce;
import com.braintreecv1payments.api.models.IdealResult;
import com.braintreecv1payments.api.models.LocalPaymentResult;
import com.braintreecv1payments.api.models.PayPalAccountNonce;
import com.braintreecv1payments.api.models.PaymentMethodNonce;
import com.braintreecv1payments.api.models.VenmoAccountNonce;
import com.braintreecv1payments.api.models.VisaCheckoutNonce;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.LineItem;

import java.util.Collections;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends BaseActivity {

    static final String EXTRA_PAYMENT_RESULT = "payment_result";
    static final String EXTRA_DEVICE_DATA = "device_data";
    static final String EXTRA_COLLECT_DEVICE_DATA = "collect_device_data";
    static final String EXTRA_ANDROID_PAY_CART = "android_pay_cart";

    private static final int DROP_IN_REQUEST = 1;
    private static final int ANDROID_PAY_REQUEST = 2;
    private static final int GOOGLE_PAYMENT_REQUEST = 3;
    private static final int CARDS_REQUEST = 4;
    private static final int PAYPAL_REQUEST = 5;
    private static final int VENMO_REQUEST = 6;
    private static final int VISA_CHECKOUT_REQUEST = 7;
    private static final int IDEAL_REQUEST = 8;

    private static final String KEY_NONCE = "nonce";

    private PaymentMethodNonce mNonce;

    private ImageView mNonceIcon;
    private TextView mNonceString;
    private TextView mNonceDetails;
    private TextView mDeviceData;

    private Button mDropInButton;
    private Button mAndroidPayButton;
    private Button mGooglePaymentButton;
    private Button mCardsButton;
    private Button mPayPalButton;
    private Button mVenmoButton;
    private Button mVisaCheckoutButton;
    private Button mCreateTransactionButton;
    private Button mIdealButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mNonceIcon = findViewById(R.id.nonce_icon);
        mNonceString = findViewById(R.id.nonce);
        mNonceDetails = findViewById(R.id.nonce_details);
        mDeviceData = findViewById(R.id.device_data);

        mDropInButton = findViewById(R.id.drop_in);
        mAndroidPayButton = findViewById(R.id.android_pay);
        mGooglePaymentButton = findViewById(R.id.google_payment);
        mCardsButton = findViewById(R.id.card);
        mPayPalButton = findViewById(R.id.paypal);
        mVenmoButton = findViewById(R.id.venmo);
        mVisaCheckoutButton = findViewById(R.id.visa_checkout);
        mIdealButton = (Button) findViewById(R.id.ideal);
        mCreateTransactionButton = findViewById(R.id.create_transaction);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_NONCE)) {
                mNonce = savedInstanceState.getParcelable(KEY_NONCE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNonce != null) {
            outState.putParcelable(KEY_NONCE, mNonce);
        }
    }

    public void launchDropIn(View v) {
        startActivityForResult(getDropInRequest().getIntent(this), DROP_IN_REQUEST);
    }

    public void launchAndroidPay(View v) {
        Intent intent = new Intent(this, AndroidPayActivity.class)
                .putExtra(EXTRA_ANDROID_PAY_CART, getAndroidPayCart());
        startActivityForResult(intent, ANDROID_PAY_REQUEST);
    }

    public void launchGooglePayment(View v) {
        Intent intent = new Intent(this, GooglePaymentActivity.class);
        startActivityForResult(intent, GOOGLE_PAYMENT_REQUEST);
    }

    public void launchCards(View v) {
        Intent intent = new Intent(this, CardActivity.class)
                .putExtra(EXTRA_COLLECT_DEVICE_DATA, Settings.shouldCollectDeviceData(this));
        startActivityForResult(intent, CARDS_REQUEST);
    }

    public void launchPayPal(View v) {
        Intent intent = new Intent(this, PayPalActivity.class)
                .putExtra(EXTRA_COLLECT_DEVICE_DATA, Settings.shouldCollectDeviceData(this));
        startActivityForResult(intent, PAYPAL_REQUEST);
    }

    public void launchVenmo(View v) {
        Intent intent = new Intent(this, VenmoActivity.class);
        startActivityForResult(intent, VENMO_REQUEST);
    }

    public void launchVisaCheckout(View v) {
        Intent intent = new Intent(this, VisaCheckoutActivity.class);
        startActivityForResult(intent, VISA_CHECKOUT_REQUEST);
    }

    public void launchIdeal(View v) {
        Intent intent = new Intent(this, IdealActivity.class);
        startActivityForResult(intent, IDEAL_REQUEST);
    }

    private DropInRequest getDropInRequest() {
        DropInRequest dropInRequest = new DropInRequest()
                .amount("1.00")
                .clientToken(mAuthorization)
                .collectDeviceData(Settings.shouldCollectDeviceData(this))
                .requestThreeDSecureVerification(Settings.isThreeDSecureEnabled(this))
                .androidPayCart(getAndroidPayCart())
                .androidPayShippingAddressRequired(Settings.isAndroidPayShippingAddressRequired(this))
                .androidPayPhoneNumberRequired(Settings.isAndroidPayPhoneNumberRequired(this))
                .androidPayAllowedCountriesForShipping(Settings.getAndroidPayAllowedCountriesForShipping(this));

        if (Settings.isPayPalAddressScopeRequested(this)) {
            dropInRequest.paypalAdditionalScopes(Collections.singletonList(PayPal.SCOPE_ADDRESS));
        }

        return dropInRequest;
    }

    public void createTransaction(View v) {
        Intent intent = new Intent(this, CreateTransactionActivity.class)
                .putExtra(CreateTransactionActivity.EXTRA_PAYMENT_METHOD_NONCE, mNonce);
        startActivity(intent);

        mCreateTransactionButton.setEnabled(false);
        clearNonce();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == DROP_IN_REQUEST) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                displayNonce(result.getPaymentMethodNonce(), result.getDeviceData());
            } else {
                Parcelable returnedData = data.getParcelableExtra(EXTRA_PAYMENT_RESULT);
                String deviceData = data.getStringExtra(EXTRA_DEVICE_DATA);
                if (returnedData instanceof PaymentMethodNonce) {
                    displayNonce((PaymentMethodNonce) returnedData, deviceData);
                } else if (returnedData instanceof BraintreePaymentResult) {
                    displayBraintreeResult((BraintreePaymentResult) returnedData);
                }

                mCreateTransactionButton.setEnabled(true);
            }
        } else if (resultCode != RESULT_CANCELED) {
            showDialog(((Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR)).getMessage());
        }
    }

    @Override
    protected void reset() {
        enableButtons(false);
        mCreateTransactionButton.setEnabled(false);

        clearNonce();
    }

    @Override
    protected void onAuthorizationFetched() {
        enableButtons(true);
    }

    private void displayNonce(PaymentMethodNonce paymentMethodNonce, String deviceData) {
        mNonce = paymentMethodNonce;

        mNonceIcon.setImageResource(PaymentMethodType.forType(mNonce).getDrawable());
        mNonceIcon.setVisibility(VISIBLE);

        mNonceString.setText(getString(R.string.nonce_placeholder, mNonce.getNonce()));
        mNonceString.setVisibility(VISIBLE);

        String details = "";
        if (mNonce instanceof CardNonce) {
            details = CardActivity.getDisplayString((CardNonce) mNonce);
        } else if (mNonce instanceof PayPalAccountNonce) {
            details = PayPalActivity.getDisplayString((PayPalAccountNonce) mNonce);
        } else if (mNonce instanceof AndroidPayCardNonce) {
            details = AndroidPayActivity.getDisplayString((AndroidPayCardNonce) mNonce);
        } else if (mNonce instanceof GooglePaymentCardNonce) {
            details = GooglePaymentActivity.getDisplayString((GooglePaymentCardNonce) mNonce);
        } else if (mNonce instanceof VisaCheckoutNonce) {
            details = VisaCheckoutActivity.getDisplayString((VisaCheckoutNonce) mNonce);
        } else if (mNonce instanceof VenmoAccountNonce) {
            details = VenmoActivity.getDisplayString((VenmoAccountNonce) mNonce);
        } else if (mNonce instanceof LocalPaymentResult) {
            details = IdealActivity.getDisplayString((LocalPaymentResult) mNonce);
        }

        mNonceDetails.setText(details);
        mNonceDetails.setVisibility(VISIBLE);

        mDeviceData.setText(getString(R.string.device_data_placeholder, deviceData));
        mDeviceData.setVisibility(VISIBLE);

        mCreateTransactionButton.setEnabled(true);
    }

    private void displayBraintreeResult(BraintreePaymentResult result) {
        if (result instanceof IdealResult) {
            IdealResult idealResult = (IdealResult) result;
            mNonceString.setText(getString(R.string.ideal_id_placeholder, idealResult.getId()));
            mNonceString.setVisibility(VISIBLE);

            mNonceDetails.setText(getString(R.string.ideal_status_placeholder, idealResult.getStatus()));
            mNonceDetails.setVisibility(VISIBLE);

            mCreateTransactionButton.setEnabled(false);
        }
    }

    private void clearNonce() {
        mNonceIcon.setVisibility(GONE);
        mNonceString.setVisibility(GONE);
        mNonceDetails.setVisibility(GONE);
        mDeviceData.setVisibility(GONE);
        mCreateTransactionButton.setEnabled(false);
    }

    private Cart getAndroidPayCart() {
        return Cart.newBuilder()
                .setCurrencyCode(Settings.getAndroidPayCurrency(this))
                .setTotalPrice("1.00")
                .addLineItem(LineItem.newBuilder()
                        .setCurrencyCode("USD")
                        .setDescription("Description")
                        .setQuantity("1")
                        .setUnitPrice("1.00")
                        .setTotalPrice("1.00")
                        .build())
                .build();
    }

    private void enableButtons(boolean enable) {
        mDropInButton.setEnabled(enable);
        mAndroidPayButton.setEnabled(enable);
        mGooglePaymentButton.setEnabled(enable);
        mCardsButton.setEnabled(enable);
        mPayPalButton.setEnabled(enable);
        mVenmoButton.setEnabled(enable);
        mVisaCheckoutButton.setEnabled(enable);
        mIdealButton.setEnabled(enable);
    }
}
