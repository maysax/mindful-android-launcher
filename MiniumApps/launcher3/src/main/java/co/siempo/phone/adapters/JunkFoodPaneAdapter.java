package co.siempo.phone.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.activities.JunkfoodFlaggingActivity;
import co.siempo.phone.app.BitmapWorkerTask;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.utils.DrawableProvider;

/**
 * Created by RajeshJadi on 2/23/2017.
 */

public class JunkFoodPaneAdapter extends RecyclerView.Adapter<JunkFoodPaneAdapter.ViewHolder> {

    private final Context context;
    private List<String> mainListItemList;
    private boolean isHideIconBranding = false;
    private DrawableProvider mProvider;


    public JunkFoodPaneAdapter(Context context, ArrayList<String> mainListItemList, boolean isHideIconBranding) {
        this.context = context;
        this.mainListItemList = mainListItemList;
        this.isHideIconBranding = isHideIconBranding;
        mProvider = new DrawableProvider(context);
    }

    public void setMainListItemList(List<String> mainListItemList, boolean isHideIconBranding) {
        this.mainListItemList = mainListItemList;
        this.isHideIconBranding = isHideIconBranding;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.list_application_item_grid, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String item = mainListItemList.get(position);
        holder.linearLayout.setVisibility(View.VISIBLE);
        String applicationName = CoreApplication.getInstance().getApplicationNameFromPackageName(item);
        holder.text.setText(applicationName);
        if (isHideIconBranding) {
            holder.txtAppTextImage.setVisibility(View.VISIBLE);
            holder.imgAppIcon.setVisibility(View.GONE);
            holder.imgUnderLine.setVisibility(View.VISIBLE);
            String fontPath = "fonts/robotocondensedregular.ttf";
            if (!TextUtils.isEmpty(applicationName)) {
                holder.txtAppTextImage.setText("" + applicationName.toUpperCase().charAt(0));
            }
            // Loading Font Face
            Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);
            // Applying font
            holder.txtAppTextImage.setTypeface(tf);

        } else {
            holder.txtAppTextImage.setVisibility(View.GONE);
            holder.imgUnderLine.setVisibility(View.GONE);
            holder.imgAppIcon.setVisibility(View.VISIBLE);
            Bitmap bitmap = CoreApplication.getInstance().getBitmapFromMemCache(item);
            if (bitmap != null) {
                holder.imgAppIcon.setImageBitmap(bitmap);
            } else {
                BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(context, item);
                CoreApplication.getInstance().includeTaskPool(bitmapWorkerTask, null);
                Drawable drawable = CoreApplication.getInstance().getApplicationIconFromPackageName(item);
                holder.imgAppIcon.setImageDrawable(drawable);
            }


//            holder.imgAppIcon.setImageDrawable(CoreApplication.getInstance().getApplicationIconFromPackageName(item));
        }

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(context, JunkfoodFlaggingActivity.class);
                context.startActivity(intent);
                ((CoreActivity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.getInstance().logSiempoMenuUsage(2, "", CoreApplication.getInstance().getApplicationNameFromPackageName(item));
//                EventBus.getDefault().post(new HomePress(1, 2));
                new ActivityHelper(context).openAppWithPackageName(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mainListItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View layout;
        // each data item is just a string in this case
        ImageView imgView, imgAppIcon, imgUnderLine;
        TextView text, txtAppTextImage;
        TextView textDefaultApp;
        RelativeLayout relMenu;
        private LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            linearLayout = v.findViewById(R.id.linearList);
            relMenu = v.findViewById(R.id.relMenu);
            text = v.findViewById(R.id.text);
            textDefaultApp = v.findViewById(R.id.textDefaultApp);
            txtAppTextImage = v.findViewById(R.id.txtAppTextImage);
            imgView = v.findViewById(R.id.imgView);
            imgAppIcon = v.findViewById(R.id.imgAppIcon);
            imgUnderLine = v.findViewById(R.id.imgUnderLine);
        }
    }
}
