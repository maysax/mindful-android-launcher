package co.siempo.phone.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.adapters.SubscriptionItemAdapter;
import co.siempo.phone.util.SkuDetails;
import co.siempo.phone.util.SubscriptionUtil;

public class InAppItemListActivity extends CoreActivity implements
        SubscriptionUtil.IabSetupFinishedListener, SubscriptionUtil.SubscriptionInventoryListener,
        SubscriptionUtil.SubscriptionFinishedListener {
    public SubscriptionUtil subscriptionUtil;
    RecyclerView recyclerView;
    List<String> additionalSkuList = new ArrayList();
    ArrayList<SkuDetails> skuList;
    SubscriptionItemAdapter subscriptionItemAdapter;
    ProgressDialog progressDialog;
    TextView txtNoItem;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_item_list);
        initToolBar();
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        additionalSkuList.addAll(Arrays.asList(getResources().getStringArray(R.array.in_app_items_subscription)));
        recyclerView = findViewById(R.id.recyclerView);
        txtNoItem = findViewById(R.id.txtNoItem);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        subscriptionUtil = new SubscriptionUtil(InAppItemListActivity.this);
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle("In App Products");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void purchaseItem(SkuDetails skuDetails) {
        subscriptionUtil.initSubscription(skuDetails.getSku(), this);
    }

    @Override
    public void onQueryInventoryFinished(ArrayList<SkuDetails> skuList) {
        if (skuList.size() == 0) {
            txtNoItem.setVisibility(View.VISIBLE);
        } else {
            txtNoItem.setVisibility(View.GONE);
            this.skuList = skuList;
            progressDialog.dismiss();
            subscriptionItemAdapter = new SubscriptionItemAdapter(this, skuList);
            recyclerView.setAdapter(subscriptionItemAdapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!subscriptionUtil.getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Purchased Successfully", Toast.LENGTH_SHORT);
    }

    @Override
    public void onSetupFinish() {
        subscriptionUtil.getSkuDetailsList(additionalSkuList, this);
    }

    @Override
    public void onFailure() {
        progressDialog.dismiss();
    }
}
