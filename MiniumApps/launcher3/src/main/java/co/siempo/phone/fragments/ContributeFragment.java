package co.siempo.phone.fragments;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.util.IabHelper;
import co.siempo.phone.util.IabResult;
import co.siempo.phone.util.Inventory;
import co.siempo.phone.util.SkuDetails;

/**
 * This screen is use to display FAQ link.
 */
@EFragment(R.layout.fragment_contribute)
public class ContributeFragment extends CoreFragment {

    @ViewById
    Spinner spinnerContribute;

    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView txtSubmit;

    private long startTime = 0;

    List<String> additionalSkuList = new ArrayList();
    ArrayList<SkuDetails> skuList;
    IabHelper mHelper;

    private ProgressDialog progressDialog;

    @AfterViews
    void afterViews() {
        additionalSkuList.addAll(Arrays.asList(getResources().getStringArray(R.array.in_app_items_donate)));
        toolbar.setTitle(R.string.contribute_to_siempo);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mHelper = new IabHelper(getActivity(), CoreApplication.getInstance().getBase64EncodedPublicKey());
        mHelper.enableDebugLogging(false);
        Log.d("Contribute", "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            public void onIabSetupFinished(IabResult result) {
                Log.d("Contribute", "Setup finished.");

                if (!result.isSuccess()) {
                    Toast.makeText(getActivity(), "In app billing error", Toast.LENGTH_SHORT).show();
                    dispose();
                    return;
                }
                if (mHelper == null)
                    return;

// IAB is fully set up. Now, let's get an inventory of stuff we own.

//   --commented out here as we didn't need it for donation purposes.

                //Log.d("Contribute", "Setup successful. Querying inventory.");

//                try {
//                    mHelper.queryInventoryAsync(mGotInventoryListener);
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    e.printStackTrace();
//                }

            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Contribute", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null)
            return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d("Contribute", "onActivityResult handled by IABUtil.");
        }

    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(final IabResult result,
                                             final Inventory inventory) {
            Log.d("Contribute", "Query inventory finished.");
            if (mHelper == null) {
                return;
            }
            if (result.isFailure()) {
                Toast.makeText(getActivity(),
                        "Failed to query inventory: " + result,
                        Toast.LENGTH_LONG).show();
                return;
            }

            Log.d("Contribute", "Query inventory was successful.");
            Log.d("Contribute", "Initial inventory query finished; enabling main UI.");
        }
    };


    /*
     ** Method for releasing resources (dispose of object)
     */
    public void dispose() {
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
            mHelper = null;
        }
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


}
