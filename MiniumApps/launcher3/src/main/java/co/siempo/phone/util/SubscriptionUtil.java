package co.siempo.phone.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.app.CoreApplication;

public class SubscriptionUtil {
    private static final int REQUEST_CODE = 10001;
    private static final String base64EncodedPublicKey = CoreApplication.getInstance().getBase64EncodedPublicKey();

    private IabHelper iabHelper;
    private Context context;

    private SubscriptionUtil() {
        //No instance
    }

    public SubscriptionUtil(Context context) {
        this.context = context;
        iabHelper = new IabHelper(context, base64EncodedPublicKey);
        iabHelper.enableDebugLogging(true, "Rajesh");
        setup((IabSetupFinishedListener) context);
    }

    private void setup(final IabSetupFinishedListener iabSetupFinishedListener) {
        if (iabHelper != null) {
            iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (result.isFailure()) {
                        Log.d("TEST", "Problem setting up In-app Billing: " + result);
                        iabSetupFinishedListener.onFailure();
                        dispose();
                    } else {
                        Log.d("TEST", "Problem setting up In-app Billing: " + result);
                        iabSetupFinishedListener.onSetupFinish();
                    }
                }
            });
        }
    }

    public void initSubscription(final String subscriptionType,
                                 SubscriptionFinishedListener subscriptionFinishedListener) {
        initSubscriptionWithExtras(subscriptionType, subscriptionFinishedListener, "");
    }


    public void initPurchase(final String subscriptionType,
                             PurchaseFinishedListener subscriptionFinishedListener) {
        initPurchaseWithExtras(subscriptionType, subscriptionFinishedListener, CoreApplication.getInstance().getDeviceId());
    }


    public void initSubscriptionWithExtras(final String subscriptionType,
                                           final SubscriptionFinishedListener subscriptionFinishedListener,
                                           String payload) {
        if (iabHelper != null) {
            try {
                iabHelper.launchSubscriptionPurchaseFlow((Activity) context,
                        subscriptionType,
                        REQUEST_CODE,
                        new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                if (result.isFailure()) {
                                    Log.e("TEST", "Error purchasing: " + result);
                                    return;
                                }
                                if (info.getSku().equals(subscriptionType)) {
                                    if (subscriptionFinishedListener != null) {
                                        subscriptionFinishedListener.onSuccess();
                                    }
                                    Log.e("TEST", "Thank you for upgrading to premium!");
                                }
                            }
                        },
                        payload
                );
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            //In case you get below error:
            //`Can't start async operation (refresh inventory) because another async operation (launchPurchaseFlow) is in progress.`
            //Include this line of code to end proccess after purchase
            //iabHelper.flagEndAsync();
        }
    }

    public void initPurchaseWithExtras(final String subscriptionType,
                                       final PurchaseFinishedListener subscriptionFinishedListener,
                                       String payload) {
        if (iabHelper != null) {
            try {
                iabHelper.flagEndAsync();
                iabHelper.launchPurchaseFlow((Activity) context,
                        subscriptionType,
                        REQUEST_CODE,
                        new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                if (iabHelper == null) return;
                                if (result.isFailure()) {
                                    if (result.getResponse() == 7) {
                                        Toast.makeText(context, "Item is already owned.", Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                }

                                if (!verifyDeveloperPayload(info)) {
                                    return;
                                }
                                if (info.getSku().equals(subscriptionType)) {
                                    if (subscriptionFinishedListener != null) {
                                        subscriptionFinishedListener.onSuccess();
                                    }
                                    try {
                                        iabHelper.consumeAsync(info, new IabHelper.OnConsumeFinishedListener() {
                                            @Override
                                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                Log.d("Test", "Consumption finished. Purchase: " + purchase + ", result: " + result);
                                                // if we were disposed of in the meantime, quit.
                                                if (iabHelper == null) return;
                                                // We know this is the "gas" sku because it's the only one we consume,
                                                // so we don't check which sku was consumed. If you have more than one
                                                // sku, you probably should check...
                                                if (result.isSuccess()) {
                                                    // successfully consumed, so we apply the effects of the item in our
                                                    // game world's logic, which in our case means filling the gas tank a bit
                                                    Log.d("Test", "Consumption successful. Provisioning.");
                                                } else {
                                                }
                                                Log.d("Test", "End consumption flow.");
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    Log.e("TEST", "Thank you for upgrading to premium!");
                                }
                            }
                        },
                        payload
                );
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get sku detail list for Subscription items.
     *
     * @param skuSubscriptionIdsList
     * @param subscriptionInventoryListener
     */
    public void getSkuDetailsList(final List<String> skuSubscriptionIdsList,
                                  final SubscriptionInventoryListener subscriptionInventoryListener) {
        if (iabHelper != null) {
            try {
                iabHelper.queryInventoryAsync(true, null, skuSubscriptionIdsList, new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                        if (result.isFailure()) {
                            Log.d("TEST", "Problem querying inventory: " + result);
                            dispose();
                            return;
                        }
                        ArrayList<SkuDetails> skuDetailsList = new ArrayList<>();
                        if (inventory != null) {
                            for (String skuId : skuSubscriptionIdsList) {
                                SkuDetails sku = inventory.getSkuDetails(skuId);
                                if (sku.getSku().equals(skuId)) {
                                    skuDetailsList.add(sku);
                                }
                            }
                        }
                        if (subscriptionInventoryListener != null) {
                            subscriptionInventoryListener.onQueryInventoryFinished(skuDetailsList);
                        }
                    }
                });
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.e("TEST", "EXCEPTION:" + e.getMessage());
            }
        }

    }

    // Third method.

    /**
     * get SKU details for in-app purchase items.
     *
     * @param skuIdsList
     * @param subscriptionInventoryListener
     */
    public void getSkuInAppDetailsList(final List<String> skuIdsList,
                                       final InAppInventoryListener subscriptionInventoryListener, final boolean isFirstTime) {
        if (iabHelper != null) {
            try {
                iabHelper.queryInventoryAsync(true, skuIdsList, null, new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                        if (result.isFailure()) {
                            Log.d("TEST", "Problem querying inventory: " + result);
                            dispose();
                            subscriptionInventoryListener.onFailureInventory();
                            return;
                        } else {
                            ArrayList<SkuDetails> skuDetailsList = new ArrayList<>();
                            for (String skuId : skuIdsList) {
                                SkuDetails sku = inventory.getSkuDetails(skuId);
                                if (sku.getSku().equals(skuId)) {
                                    skuDetailsList.add(sku);
                                }
                            }

                            if (subscriptionInventoryListener != null) {
                                subscriptionInventoryListener.onQueryInventoryFinished(skuDetailsList, isFirstTime);
                            }
                        }
                    }
                });
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.e("TEST", "EXCEPTION:" + e.getMessage());
            }
        }

    }

    public void dispose() {
        if (iabHelper != null) {
            try {
                iabHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            iabHelper = null;
        }
    }

    public IabHelper getIabHelper() {
        if (iabHelper == null) {
            iabHelper = new IabHelper(context, base64EncodedPublicKey);
        }
        return iabHelper;
    }

    public interface SubscriptionInventoryListener {
        void onQueryInventoryFinished(ArrayList<SkuDetails> skuList);
    }

    public interface InAppInventoryListener {
        void onQueryInventoryFinished(ArrayList<SkuDetails> skuList, boolean isFirstTime);
        void onFailureInventory();
    }


    public interface SubscriptionFinishedListener {
        void onSuccess();
    }

    public interface PurchaseFinishedListener {
        void onSuccess();
    }

    public interface IabSetupFinishedListener {
        void onSetupFinish();

        void onFailure();
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }
}
