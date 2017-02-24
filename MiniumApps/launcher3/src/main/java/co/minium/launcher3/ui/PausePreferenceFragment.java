package co.minium.launcher3.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.minium.launcher3.R;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

@EFragment(R.layout.fragment_pause_preference)
public class PausePreferenceFragment extends CoreFragment {
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;

    public PausePreferenceFragment() {
        // Required empty public constructor
    }


    @ViewById
    RecyclerView pref_recyclerview;
    @AfterViews
    void afterViews() {
        String[] subjects =
                {"Allow favorites","Allow calls"};

        recylerViewLayoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        pref_recyclerview.setLayoutManager(recylerViewLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(context, subjects);

        pref_recyclerview.setAdapter(recyclerViewAdapter);
    }
}
