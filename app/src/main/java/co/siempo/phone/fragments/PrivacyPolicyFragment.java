package co.siempo.phone.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import co.siempo.phone.R;
import co.siempo.phone.databinding.FragmentPrivacyPolicyBinding;

/**
 * This screen is use to display privacy policy which is load from assets folder.
 */
public class PrivacyPolicyFragment extends CoreFragment {

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentPrivacyPolicyBinding binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        binding.toolbar.setTitle(R.string.privacypolicy);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        try {
            binding.webPrivacyPolicy.setHorizontalScrollBarEnabled(false);
            binding.webPrivacyPolicy.getSettings().setJavaScriptEnabled(true);
            binding.webPrivacyPolicy.loadUrl("http://www.getsiempo.com/app/pp.html");
            binding.webPrivacyPolicy.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // do your handling codes here, which url is the requested url
                    // probably you need to open that url rather than redirect:
                    view.loadUrl(url);
                    return false; // then it is not handled by default action
                }
            });
            binding.webPrivacyPolicy.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return binding;
    }
}
