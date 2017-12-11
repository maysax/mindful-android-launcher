package co.siempo.phone.settings;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.siempo.phone.R;

public class AgendaItemViewHolder extends RecyclerView.ViewHolder {

    @Bind({R.id.title})
    TextView textView;

    public AgendaItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void render(String text){
        textView.setText(text);
    }
}
