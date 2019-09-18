package com.braintreecv1payments.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;

import org.json.JSONException;
import org.json.JSONObject;

import static com.braintreecv1payments.api.models.BinData.BIN_DATA_KEY;

/**
 * @deprecated Android Pay is deprecated, use {@link GooglePaymentCardNonce} instead. For more information see the
 * <a href="https://developers.braintreepayments.com/guides/pay-with-google/overview">documentation</a>
 *
 * {@link PaymentMethodNonce} representing an Android Pay card.
 * @see PaymentMethodNonce
 */
@Deprecated
public class AndroidPayCardNonce extends PaymentMethodNonce implements Parcelable {

    protected static final String TYPE = "AndroidPayCard";
    protected static final String API_RESOURCE_KEY = "androidPayCards";

    private static final String CARD_DETAILS_KEY = "details";
    private static final String CARD_TYPE_KEY = "cardType";
    private static final String LAST_TWO_KEY = "lastTwo";

    private String mCardType;
    private String mLastTwo;
    private String mEmail;
    private UserAddress mBillingAddress;
    private UserAddress mShippingAddress;
    private String mGoogleTransactionId;
    private Cart mCart;
    private BinData mBinData;

    /**
     * @deprecated Use {@link #fromFullWallet(FullWallet, Cart)} instead.
     */
    @Deprecated
    public static AndroidPayCardNonce fromFullWallet(FullWallet wallet) throws JSONException {
        return fromFullWallet(wallet, null);
    }

    /**
     * @deprecated Android Pay is deprecated, use {@link GooglePaymentCardNonce} instead. For more information see the
     * <a href="https://developers.braintreepayments.com/guides/pay-with-google/overview">documentation</a>
     *
     * Convert a {@link FullWallet} to an {@link AndroidPayCardNonce}.
     *
     * @param wallet the {@link FullWallet} from an Android Pay response.
     * @param cart the {@link Cart} used to create the {@link FullWallet}.
     * @return {@link AndroidPayCardNonce}.
     * @throws JSONException when parsing the response fails.
     */
    @Deprecated
    public static AndroidPayCardNonce fromFullWallet(FullWallet wallet, Cart cart) throws JSONException {
        AndroidPayCardNonce androidPayCardNonce =
                AndroidPayCardNonce.fromJson(wallet.getPaymentMethodToken().getToken());
        androidPayCardNonce.mDescription = wallet.getPaymentDescriptions()[0];
        androidPayCardNonce.mEmail = wallet.getEmail();
        androidPayCardNonce.mBillingAddress = wallet.getBuyerBillingAddress();
        androidPayCardNonce.mShippingAddress = wallet.getBuyerShippingAddress();
        androidPayCardNonce.mGoogleTransactionId = wallet.getGoogleTransactionId();
        androidPayCardNonce.mCart = cart;

        return androidPayCardNonce;
    }

    /**
     * @deprecated Android Pay is deprecated, use {@link GooglePaymentCardNonce} instead. For more information see the
     * <a href="https://developers.braintreepayments.com/guides/pay-with-google/overview">documentation</a>
     *
     * Convert an API response to an {@link AndroidPayCardNonce}.
     *
     * @param json Raw JSON response from Braintree of a {@link AndroidPayCardNonce}.
     * @return {@link AndroidPayCardNonce}.
     * @throws JSONException when parsing the response fails.
     */
    @Deprecated
    public static AndroidPayCardNonce fromJson(String json) throws JSONException {
        AndroidPayCardNonce androidPayCardNonce = new AndroidPayCardNonce();
        androidPayCardNonce.fromJson(AndroidPayCardNonce.getJsonObjectForType(API_RESOURCE_KEY, new JSONObject(json)));

        return androidPayCardNonce;
    }

    protected void fromJson(JSONObject json) throws JSONException {
        super.fromJson(json);

        mBinData = BinData.fromJson(json.optJSONObject(BIN_DATA_KEY));
        JSONObject details = json.getJSONObject(CARD_DETAILS_KEY);
        mLastTwo = details.getString(LAST_TWO_KEY);
        mCardType = details.getString(CARD_TYPE_KEY);
    }

    @Override
    public String getTypeLabel() {
        return "Android Pay";
    }

    /**
     * @return Type of this card (e.g. Visa, MasterCard, American Express)
     */
    public String getCardType() {
        return mCardType;
    }

    /**
     * @return Last two digits of the user's underlying card, intended for display purposes.
     */
    public String getLastTwo() {
        return mLastTwo;
    }

    /**
     * @return The user's email address associated the Android Pay account.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * @return The user's billing address.
     */
    public UserAddress getBillingAddress() {
        return mBillingAddress;
    }

    /**
     * @return The user's shipping address.
     */
    public UserAddress getShippingAddress() {
        return mShippingAddress;
    }

    /**
     * @return The Google transaction id associated with this payment method.
     */
    public String getGoogleTransactionId() {
        return mGoogleTransactionId;
    }

    /**
     * @return The {@link Cart} used to create this {@link AndroidPayCardNonce}.
     */
    public Cart getCart() {
        return mCart;
    }

    /**
     * @return The BIN data for the card number associated with {@link AndroidPayCardNonce}
     */
    public BinData getBinData() {
        return mBinData;
    }

    public AndroidPayCardNonce() {}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mCardType);
        dest.writeString(mLastTwo);
        dest.writeString(mEmail);
        dest.writeParcelable(mBillingAddress, flags);
        dest.writeParcelable(mShippingAddress, flags);
        dest.writeString(mGoogleTransactionId);
        dest.writeParcelable(mCart, flags);
        dest.writeParcelable(mBinData, flags);
    }

    private AndroidPayCardNonce(Parcel in) {
        super(in);
        mCardType = in.readString();
        mLastTwo = in.readString();
        mEmail = in.readString();
        mBillingAddress = in.readParcelable(UserAddress.class.getClassLoader());
        mShippingAddress = in.readParcelable(UserAddress.class.getClassLoader());
        mGoogleTransactionId = in.readString();
        mCart = in.readParcelable(Cart.class.getClassLoader());
        mBinData = in.readParcelable(BinData.class.getClassLoader());
    }

    public static final Creator<AndroidPayCardNonce> CREATOR = new Creator<AndroidPayCardNonce>() {
        public AndroidPayCardNonce createFromParcel(Parcel source) {
            return new AndroidPayCardNonce(source);
        }

        public AndroidPayCardNonce[] newArray(int size) {
            return new AndroidPayCardNonce[size];
        }
    };
}
