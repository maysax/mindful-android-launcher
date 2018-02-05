package co.siempo.phone.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import co.siempo.phone.R;


public class ToolsPaneFragment extends Fragment {

    private View view;
    private LinearLayout linTopDoc, linBottomDoc, linPane, linSearchList;
    private RecyclerView recyclerView;

    public ToolsPaneFragment() {
        // Required empty public constructor
    }

    public static ToolsPaneFragment newInstance() {
        return new ToolsPaneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tools_pane, container, false);
        initView();
        return view;

    }

    private void initView() {
        linTopDoc = view.findViewById(R.id.linTopDoc);
        linBottomDoc = view.findViewById(R.id.linBottomDoc);
        linPane = view.findViewById(R.id.linPane);
        linSearchList = view.findViewById(R.id.linSearchList);
        recyclerView = view.findViewById(R.id.recyclerView);
    }


}
