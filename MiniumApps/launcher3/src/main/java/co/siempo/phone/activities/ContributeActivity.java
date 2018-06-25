package co.siempo.phone.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.util.SkuDetails;
import co.siempo.phone.util.SubscriptionUtil;

/**
 * This screen is use to display FAQ link.
 */
public class ContributeActivity extends CoreActivity implements
        SubscriptionUtil.IabSetupFinishedListener, SubscriptionUtil.InAppInventoryListener, SubscriptionUtil.PurchaseFinishedListener {

    private Spinner spinnerContribute;
    private Toolbar toolbar;
    private TextView txtSubmit;
    private TextView text;
    private long startTime = 0;
    private SubscriptionUtil subscriptionUtil;
    private List<String> additionalSkuListPlayStore = new ArrayList();
    private ArrayList<SkuDetails> skuList;
    private ProgressDialog progressDialog;
    private SkuDetails skuDetails;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contribute);
        setUpToolbar();

        additionalSkuListPlayStore.addAll(Arrays.asList(getResources().getStringArray(R.array.in_app_items_donate)));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        subscriptionUtil = new SubscriptionUtil(this);

        clickListener();
    }


    private void clickListener() {
        spinnerContribute = findViewById(R.id.spinnerContribute);
        spinnerContribute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (skuList != null)
                    skuDetails = skuList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        txtSubmit = findViewById(R.id.txtSubmit);
        text = findViewById(R.id.text);
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscriptionUtil.initPurchase(skuDetails.getSku(), ContributeActivity.this);
            }
        });
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.contribute_to_siempo);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Contribute", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (subscriptionUtil.getIabHelper() == null)
            return;
        // Pass on the activity result to the helper for handling
        if (!subscriptionUtil.getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
//            Toast.makeText(this, getString(R.string.donations__thanks_dialog), Toast.LENGTH_SHORT).show();
            Log.d("Contribute", "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onSetupFinish() {
        Log.d("Contribute", "Setup finished.");
        subscriptionUtil.getSkuInAppDetailsList(additionalSkuListPlayStore, this, true);
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, "In app billing error", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onQueryInventoryFinished(ArrayList<SkuDetails> skuList, boolean isFirstTime) {
        if (isFirstTime) {
            if (skuList.size() == 0) {
                Toast.makeText(this, "No items found", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                this.skuList = skuList;

                if(!isFinishing()) {
                    if(null!=progressDialog && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    adapter = new CustomAdapter(this,
                            R.layout.row_spinner, R.id.title, skuList);
                    spinnerContribute.setAdapter(adapter);
                    text.setVisibility(View.VISIBLE);
                    spinnerContribute.setVisibility(View.VISIBLE);
                    txtSubmit.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    @Override
    public void onFailureInventory() {
        Toast.makeText(this, "Unable to connect.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSuccess() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(getString(R.string.donations__thanks_dialog))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public class CustomAdapter extends ArrayAdapter<SkuDetails> {

        LayoutInflater flater;

        public CustomAdapter(Context context, int resouceId, int textviewId, List<SkuDetails> list) {

            super(context, resouceId, textviewId, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return rowview(convertView, position);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return rowview(convertView, position);
        }

        private View rowview(View convertView, int position) {

            SkuDetails rowItem = getItem(position);

            viewHolder holder;
            View rowview = convertView;
            if (rowview == null) {

                holder = new viewHolder();
                flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (flater != null) {
                    rowview = flater.inflate(R.layout.row_spinner, null, false);
                }

                holder.txtTitle = rowview.findViewById(R.id.text1);
                rowview.setTag(holder);
            } else {
                holder = (viewHolder) rowview.getTag();
            }

            holder.txtTitle.setText(rowItem.getPrice());

            return rowview;
        }

        private class viewHolder {
            TextView txtTitle;
        }
    }

}
