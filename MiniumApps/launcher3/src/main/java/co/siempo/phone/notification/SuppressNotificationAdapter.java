package co.siempo.phone.notification;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Telephony;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import minium.co.core.app.CoreApplication;
import minium.co.core.util.UIUtils;

/**
 * Created by hardik on 4/12/17.
 */

public class SuppressNotificationAdapter extends ArrayAdapter<Notification> {
    ArrayList<Notification>  tempNotifications, suggestions;
    List<Notification> notificationList;
    Context context;
    private String defSMSApp;

    public SuppressNotificationAdapter(Context context, List<Notification> notificationList) {
        super(context, R.layout.notification_card, notificationList);
        this.notificationList = notificationList;
        this.context = context;
        defSMSApp = Telephony.Sms.getDefaultSmsPackage(context);
        this.tempNotifications = new ArrayList<Notification>(notificationList);
        this.suggestions = new ArrayList<Notification>(notificationList);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_card, null);
            holder = new ViewHolder();
            holder.imgAppIcon = convertView.findViewById(R.id.imgAppIcon);
            holder.imgUserImage = convertView.findViewById(R.id.imgUserImage);
            holder.txtAppName = convertView.findViewById(R.id.txtAppName);
            holder.txtTime = convertView.findViewById(R.id.txtTime);
            holder.txtUserName = convertView.findViewById(R.id.txtUserName);
            holder.txtMessage = convertView.findViewById(R.id.txtMessage);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Notification notification = notificationList.get(position);
        if (notification.getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_EVENT) {
            Bitmap bitmap = CoreApplication.getInstance().iconList.get(notification.getPackageName());
            holder.imgAppIcon.setBackground(null);
            holder.imgAppIcon.setImageBitmap(null);
            if (bitmap != null) {
                holder.imgAppIcon.setImageBitmap(bitmap);
            }else{
                holder.imgAppIcon.setBackground(context.getDrawable(R.mipmap.ic_launcher));
            }
            holder.txtAppName.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(notification.getPackageName()));
            if (notification.getStrTitle() == null || notification.getStrTitle().equalsIgnoreCase("")) {
                holder.txtUserName.setText("");
                holder.txtUserName.setVisibility(View.GONE);
                holder.txtMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                holder.txtUserName.setText(notification.getStrTitle());
                holder.txtUserName.setVisibility(View.VISIBLE);
                holder.txtMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            holder.txtMessage.setText(notification.get_text());
            holder.txtTime.setText(notification.get_time());
            holder.imgUserImage.setVisibility(View.GONE);
            if (notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_MESSENGER_PACKAGE)
                    || notification.getPackageName().equalsIgnoreCase(Constants.WHATSAPP_PACKAGE)
                    || notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_LITE_PACKAGE)
                    || notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_PACKAGE)
                    || notification.getPackageName().equalsIgnoreCase(Constants.GOOGLE_HANGOUTS_PACKAGES)) {
                holder.imgUserImage.setVisibility(View.VISIBLE);
                if (notification.getUser_icon() != null) {
                    holder.imgUserImage.setImageBitmap(UIUtils.convertBytetoBitmap(notification.getUser_icon()));
                } else {
                    holder.imgUserImage.setBackground(null);
                    holder.imgUserImage.setImageBitmap(null);
                    holder.imgUserImage.setImageResource(R.drawable.ic_person_black_24dp);
                }
            }

        } else {
            holder.txtUserName.setText(notification.getNotificationContactModel().getName());
            if (notification.get_text().equalsIgnoreCase(context.getString(R.string.missed_call))) {
                holder.imgAppIcon.setBackground(null);
                holder.imgAppIcon.setImageDrawable(context.getResources().getDrawable(android.R.drawable.sym_call_missed, null));
                holder.txtAppName.setText(R.string.phone);
            } else {
                holder.imgAppIcon.setBackground(null);
                holder.txtAppName.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(defSMSApp));
                holder.imgAppIcon.setImageBitmap(CoreApplication.getInstance().iconList.get(defSMSApp));
            }
            holder.txtMessage.setText(notification.get_text());
            holder.txtTime.setText(notification.get_time());
            try {
                if (notification.getNotificationContactModel().getImage() != null && !notification.getNotificationContactModel().getImage().equals("")) {
                    Glide.with(context)
                            .load(Uri.parse(notification.getNotificationContactModel().getImage()))
                            .placeholder(R.drawable.ic_person_black_24dp)
                            .into(holder.imgUserImage);
                }else{
                    holder.imgUserImage.setBackground(null);
                    holder.imgUserImage.setImageBitmap(null);
                    holder.imgUserImage.setImageResource(R.drawable.ic_person_black_24dp);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Notification notification = (Notification) resultValue;
            return notification.getPackageName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for(int i =0 ;i<tempNotifications.size();i++){
                    if (CoreApplication.getInstance().getApplicationNameFromPackageName(tempNotifications.get(i).getPackageName()).toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(tempNotifications.get(i));
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Notification> c = (List<Notification>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Notification cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
        }
    };


    static class ViewHolder {
        ImageView imgAppIcon, imgUserImage;
        TextView txtAppName, txtTime, txtUserName, txtMessage;
    }
}

