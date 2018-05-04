package co.siempo.phone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;

public class EmailListAdapter extends ArrayAdapter<String> {

    ArrayList<String> customers = new ArrayList<>();
    ArrayList<String> suggestions = new ArrayList<>();
    private Context mContext;
    private int itemLayout;
    private ListFilter listFilter = new ListFilter();


    public EmailListAdapter(Context context, int resource, ArrayList<String> storeDataLst) {
        super(context, resource, storeDataLst);
        this.customers = storeDataLst;
        suggestions.addAll(storeDataLst);
        mContext = context;
        itemLayout = resource;
    }


    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        TextView strName = view.findViewById(R.id.title);
        strName.setText(getItem(position));
        return view;
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
//            @Override
//            public CharSequence convertResultToString(Object resultValue) {
//                String customer = (String) resultValue;
//                return customer;
//            }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String searchString = constraint != null ? constraint.toString().toLowerCase().trim() : "";

            FilterResults ret = new FilterResults();

            int count = customers.size();
            List<String> suggestions = new ArrayList<>();
            if (!searchString.isEmpty()) {

                for (int i = 0; i < count; i++) {
                    if (customers.get(i).toString().contains(searchString.toLowerCase())) {
                        suggestions.add(customers.get(i).toString());
                    }
                }
            } else {
                suggestions.addAll(customers);
            }
            ret.values = suggestions;
            ret.count = suggestions.size();
            return ret;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                suggestions = (ArrayList<String>) results.values;
            } else {
                suggestions = new ArrayList<String>(customers);
            }
            Log.d("Rajesh", "" + suggestions.size());
            notifyDataSetChanged();
        }

    }
}