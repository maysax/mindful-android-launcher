package co.siempo.phone.activities;

import android.widget.ListView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.event.DefaultAppUpdate;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.old.OldMenuAdapter;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;

@EActivity(R.layout.activity_siempo_settings_default)
public class SiempoSettingsDefaultAppActivity extends CoreActivity {
    @Pref
    public DroidPrefs_ prefs;
    @ViewById
    ListView listView;
    private OldMenuAdapter adapter;
    private List<MainListItem> items;
    private long startTime;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(SiempoSettingsDefaultAppActivity.this.getClass().getSimpleName(), startTime);
    }

    private void loadData() {
        items = new ArrayList<>();
        new MainListItemLoader(this).loadItemsDefaultApp(items);
        adapter = new OldMenuAdapter(this, items);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @ItemClick(R.id.listView)
    public void listItemClicked(int position) {
        int id = items.get(position).getId();
        new MainListItemLoader(SiempoSettingsDefaultAppActivity.this).listItemClicked(id);
    }

    @Subscribe
    public void defaultAppUpdate(DefaultAppUpdate event) {
        try {
            if (event.isDefaultAppUpdate()) {
                loadData();
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

}
