package co.siempo.phone.adapters.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.siempo.phone.R;

public class NoticationFooterViewHolder extends RecyclerView.ViewHolder {
    final TextView textView;

    public NoticationFooterViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.title);
    }

    public void render(String text) {
        textView.setText(text);
    }
}
