package co.siempo.phone.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
                e.printStackTrace();
            }
            //In case you get below error:
            //`Can't start async operation (refresh inventory) because another async operation (launchPurchaseFlow) is in progress.`
            //Include this line of code to end proccess after purchase
            //iabHelper.flagEndAsync();
        }
    }

    public void getSkuDetailsList(final List<String> skuIdsList,
                                  final SubscriptionInventoryListener subscriptionInventoryListener) {
        if (iabHelper != null) {
            try {
                iabHelper.queryInventoryAsync(true, null, skuIdsList, new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                        if (result.isFailure()) {
                            Log.d("TEST", "Problem querying inventory: " + result);
                            dispose();
                            return;
                        }
                        ArrayList<SkuDetails> skuDetailsList = new ArrayList<>();
                        for (String skuId : skuIdsList) {
                            SkuDetails sku = inventory.getSkuDetails(skuId);
                            if (sku.getSku().equals(skuId)) {
                                skuDetailsList.add(sku);
                                sku.getPrice();
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

    public interface SubscriptionFinishedListener {
        void onSuccess();
    }

    public interface IabSetupFinishedListener {
        void onSetupFinish();

        void onFailure();
    }
}
