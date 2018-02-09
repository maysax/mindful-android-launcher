package co.siempo.phone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.siempo.phone.R;


public class FavoritePaneFragment extends CoreFragment {

    private View view;

    public FavoritePaneFragment() {
        // Required empty public constructor
    }

    public static FavoritePaneFragment newInstance() {
        return new FavoritePaneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite_pane, container, false);
        return view;

    }
}
