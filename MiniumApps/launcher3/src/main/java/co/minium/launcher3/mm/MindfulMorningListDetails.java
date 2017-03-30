package co.minium.launcher3.mm;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import co.minium.launcher3.R;
import co.minium.launcher3.event.MindfulMorgingEventStart;
import co.minium.launcher3.event.PauseStartEvent;
import de.greenrobot.event.EventBus;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-10.
 */
@EFragment(R.layout.mindful_morning_details)
public class MindfulMorningListDetails extends CoreFragment {
    @FragmentArg
    String title;
    @FragmentArg
    int value;

    @ViewById
    HoloCircleSeekBar seekbar;
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mindful_morning_details, parent, false);

    }*/

    @ViewById
    TextView titleActionBar;
    @ViewById
    ImageView crossActionBar;
    @ViewById
    ImageButton pause_button;
    @Click
    void pause_button(){
      //  MMTimePickerActivity_.intent(getActivity()).start();
        startPause();
    }
    @Click
    void crossActionBar(){
        getActivity().onBackPressed();
    }
    @AfterViews
    public void afterViews(){
        seekbar.setOnSeekBarChangeListener(seekbarListener);
        seekbar.setValue(value);
    }
    private void startPause() {
        EventBus.getDefault().post(new MindfulMorgingEventStart(1));
    }
    private HoloCircleSeekBar.OnCircleSeekBarChangeListener seekbarListener = new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {

        @Override
        public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(HoloCircleSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(HoloCircleSeekBar seekBar) {

        }
    };
}
