package co.siempo.phone.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import co.siempo.phone.R;
import co.siempo.phone.databinding.FragmentFaqBinding;
import co.siempo.phone.helper.FirebaseHelper;

/**
 * This screen is use to display FAQ link.
 */
public class FaqFragment extends CoreFragment {
    private long startTime = 0;

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentFaqBinding binding = FragmentFaqBinding.inflate(inflater, container, false);

        binding.toolbar.setTitle(R.string.faq_section);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        try {
            binding.webFaq.getSettings().setJavaScriptEnabled(true);
            binding.webFaq.loadUrl(getString(R.string.faqlink));
            binding.webFaq.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return binding;
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
