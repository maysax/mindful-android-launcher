package co.siempo.phone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import co.siempo.phone.R;
import co.siempo.phone.databinding.FragmentIconLabelsBinding;
import co.siempo.phone.utils.PrefSiempo;

public class IconLabelsFragment extends CoreFragment {

    public IconLabelsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentIconLabelsBinding binding = FragmentIconLabelsBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        binding.toolbar.setTitle(R.string.icon_label);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        binding.switchIconToolsVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_TOOLS_TEXT_VISIBILITY_ENABLE, false));
        binding.switchIconToolsVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_TOOLS_TEXT_VISIBILITY_ENABLE, isChecked);
            }
        });

        binding.switchIconFavoriteVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, false));
        binding.switchIconFavoriteVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, isChecked);
            }
        });

        binding.switchIconJunkFoodVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_JUNKFOOD_TEXT_VISIBILITY_ENABLE, false));
        binding.switchIconJunkFoodVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_JUNKFOOD_TEXT_VISIBILITY_ENABLE, isChecked);
            }
        });
        return binding;
    }
}
