package co.siempo.phone.tempo;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.helper.FirebaseHelper;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;

@EActivity(R.layout.activity_tempo)
public class TempoActivity extends CoreActivity {
    @ViewById
    RadioButton radioIndividual;
    @ViewById
    RelativeLayout top;
    @ViewById
    RadioButton radioBatched;
    @ViewById
    RadioButton radioOnlyAt;
    @ViewById
    TextView txtTop;
    @ViewById
    TextView txBackground;
    @ViewById
    TextView txtBatch;
    @ViewById
    TextView txtOnlyAtTime1;
    @ViewById
    TextView txtOnlyAtTime2;
    @ViewById
    TextView txtOnlyAtTime3;
    @ViewById
    TextView txtSign1;
    @ViewById
    TextView txtSign2;
    @ViewById
    TextView txtAdd;
    @ViewById
    TextView txtMessage;
    @ViewById
    ImageView imgMinus;
    @ViewById
    ImageView imgPlus;
    @ViewById
    RelativeLayout relIndividual;
    @ViewById
    RelativeLayout relBatched;
    @ViewById
    RelativeLayout relOnlyAt;
    @ViewById
    FloatingActionMenu fabMenu;
    @ViewById
    FloatingActionButton fabMute;
    @ViewById
    FloatingActionButton fabSound;

    @ViewById
    RelativeLayout pauseContainer;

    @Pref
    DroidPrefs_ droidPrefs;
    String strMessage;
    TimePickerDialog timePickerDialog;
    boolean isCancelButton = false;
    long startTime = 0;
    @SystemService
    AudioManager audioManager;
    @SystemService
    NotificationManager notificationManager;
    private String TAG = "TempoActivity";

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(TempoActivity.class.getSimpleName(), startTime);
    }

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    @Override
    public void onBackPressed() {
        if (fabMenu.isOpened()) {
            fabMenu.close(true);
            fabMenu.getMenuIconView().setImageResource(R.drawable.ic_play_arrow_transparent_24dp);
            txtTop.setBackgroundColor(ContextCompat.getColor(TempoActivity.this, R.color.transparent));
        } else {
            super.onBackPressed();
        }
    }

    @AfterViews
    void afterViews() {
        enableRadioOnPosition(droidPrefs.tempoType().get());
        bindOnlyAt();
        fabMenu.setClosedOnTouchOutside(true);
        fabMenu.setAnimated(false);
        fabMenu.getMenuIconView().setImageResource(R.drawable.ic_play_arrow_transparent_24dp);
        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabMenu.isOpened()) {
                    fabMenu.setClickable(false);
                    fabMenu.close(true);
                    fabMenu.getMenuIconView().setImageResource(R.drawable.ic_play_arrow_transparent_24dp);
                    txtTop.setBackgroundColor(ContextCompat.getColor(TempoActivity.this, R.color.transparent));
                } else {
                    fabMenu.setClickable(true);
                    fabMenu.open(true);
                    fabMenu.getMenuIconView().setImageResource(R.drawable.ic_add_white_24dp);
                    txtTop.setBackgroundColor(ContextCompat.getColor(TempoActivity.this, R.color.temp_bg_fab_menu));
                }
            }
        });
        txBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenu.isOpened()) {
                    fabMenu.setClickable(false);
                    fabMenu.close(true);
                    fabMenu.getMenuIconView().setImageResource(R.drawable.ic_play_arrow_transparent_24dp);
                    txtTop.setBackgroundColor(ContextCompat.getColor(TempoActivity.this, R.color.transparent));
                } else {
                    onBackPressed();
                }
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.getMenuIconView().setImageResource(R.drawable.ic_play_arrow_transparent_24dp);
                txtTop.setBackgroundColor(ContextCompat.getColor(TempoActivity.this, R.color.transparent));
            }
        });

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenu.isOpened()) {
                    fabMenu.setClickable(false);
                    fabMenu.close(true);
                    fabMenu.getMenuIconView().setImageResource(R.drawable.ic_play_arrow_transparent_24dp);
                    txtTop.setBackgroundColor(ContextCompat.getColor(TempoActivity.this, R.color.transparent));
                }
            }
        });


    }

    @Click
    void radioIndividual() {
        enableRadioOnPosition(0);
        FirebaseHelper.getIntance().logTempoIntervalTime(0, 0, "");
    }

    @Click
    void radioBatched() {
        enableRadioOnPosition(1);
        FirebaseHelper.getIntance().logTempoIntervalTime(1, droidPrefs.batchTime().get(), "");
    }

    @Click
    void radioOnlyAt() {
        enableRadioOnPosition(2);
        FirebaseHelper.getIntance().logTempoIntervalTime(2, 0, droidPrefs.onlyAt().get());
    }

    @Click
    void relIndividual() {
        enableRadioOnPosition(0);
        FirebaseHelper.getIntance().logTempoIntervalTime(0, 0, "");
    }

    @Click
    void relBatched() {
        enableRadioOnPosition(1);
        FirebaseHelper.getIntance().logTempoIntervalTime(1, droidPrefs.batchTime().get(), "");
    }

    @Click
    void relOnlyAt() {
        enableRadioOnPosition(2);
        FirebaseHelper.getIntance().logTempoIntervalTime(2, 0, droidPrefs.onlyAt().get());
    }

    @Click
    void txtAdd() {
        enableRadioOnPosition(2);
        FirebaseHelper.getIntance().logTempoIntervalTime(2, 0, droidPrefs.onlyAt().get());
        Calendar now = Calendar.getInstance();
        showTimePicker(now, -1, true);
    }

    private void showTimePicker(final Calendar now, final int i, final boolean isNewAdded) {
        String strTime[] = droidPrefs.onlyAt().get().split(",");
        final ArrayList listdata = new ArrayList(Arrays.asList(strTime));
        listdata.remove("");
        String strPositiveText;
        String strNegativeText;


        if (listdata.size() <= 1) {
            strNegativeText = getString(R.string.cancel);
            strPositiveText = getString(R.string.save);
            isCancelButton = true;
        } else {
            if (isNewAdded) {
                strNegativeText = getString(R.string.cancel);
                strPositiveText = getString(R.string.save);
                isCancelButton = true;
            } else {
                strNegativeText = getString(R.string.remove);
                strPositiveText = getString(R.string.save);
                isCancelButton = false;
            }
        }
        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(this));
        timePicker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(now.get(Calendar.MINUTE));

        new AlertDialog.Builder(this)
                .setPositiveButton(strPositiveText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Picker", timePicker.getCurrentHour() + ":"
                                + timePicker.getCurrentMinute());
                        String hourString = timePicker.getCurrentHour() < 10 ? "0" + timePicker.getCurrentHour() : "" + timePicker.getCurrentHour();
                        String minuteString = timePicker.getCurrentMinute() < 10 ? "0" + timePicker.getCurrentMinute() : "" + timePicker.getCurrentMinute();
                        String strSelectedTime = hourString + ":" + minuteString;
                        if (isNewAdded) {
                            if (!listdata.contains(strSelectedTime)) {
                                listdata.add(strSelectedTime);
                                Collections.sort(listdata);
                                droidPrefs.onlyAt().put(TextUtils.join(",", listdata));
                                enableRadioOnPosition(2);
                                FirebaseHelper.getIntance().logTempoIntervalTime(2, 0, droidPrefs.onlyAt().get());
                            } else {
                                Toast.makeText(TempoActivity.this, R.string.msg_sametime, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            listdata.set(i, strSelectedTime);
                            Collections.sort(listdata);
                            droidPrefs.onlyAt().put(TextUtils.join(",", listdata));
                            enableRadioOnPosition(2);
                            FirebaseHelper.getIntance().logTempoIntervalTime(2, 0, droidPrefs.onlyAt().get());
                        }


                    }
                })
                .setNegativeButton(strNegativeText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (!isCancelButton) {
                                    if (listdata.size() != 0) {
                                        listdata.remove(i);
                                        if (listdata.size() >= 1) {
                                            droidPrefs.onlyAt().put(TextUtils.join(",", listdata));
                                        } else {
                                            droidPrefs.onlyAt().put("");
                                        }
                                        enableRadioOnPosition(2);
                                        FirebaseHelper.getIntance().logTempoIntervalTime(2, 0, droidPrefs.onlyAt().get());
                                    }
                                }
                            }
                        }).setView(timePicker).show();
    }

    @Click
    void imgMinus() {
        if (radioBatched.isChecked()) {
            if (droidPrefs.batchTime().get() == 15) {
                txtBatch.setText(getString(R.string.batched_every_4_hour));
                droidPrefs.batchTime().put(4);
            } else if (droidPrefs.batchTime().get() == 4) {
                txtBatch.setText(getString(R.string.batched_every_2_hour));
                droidPrefs.batchTime().put(2);
            } else if (droidPrefs.batchTime().get() == 2) {
                txtBatch.setText(getString(R.string.batched_every_1_hour));
                droidPrefs.batchTime().put(1);
            } else if (droidPrefs.batchTime().get() == 1) {
                txtBatch.setText(getString(R.string.batched_every_30_minutes));
                droidPrefs.batchTime().put(30);
            } else if (droidPrefs.batchTime().get() == 30) {
                txtBatch.setText(getString(R.string.batched_every_15_minutes));
                droidPrefs.batchTime().put(15);
            }
        }
        enableRadioOnPosition(1);
        FirebaseHelper.getIntance().logTempoIntervalTime(1, droidPrefs.batchTime().get(), "");
    }

    @Click
    void imgPlus() {
        if (radioBatched.isChecked()) {
            if (droidPrefs.batchTime().get() == 15) {
                txtBatch.setText(getString(R.string.batched_every_30_minutes));
                droidPrefs.batchTime().put(30);
            } else if (droidPrefs.batchTime().get() == 30) {
                txtBatch.setText(getString(R.string.batched_every_1_hour));
                droidPrefs.batchTime().put(1);
            } else if (droidPrefs.batchTime().get() == 1) {
                txtBatch.setText(getString(R.string.batched_every_2_hour));
                droidPrefs.batchTime().put(2);
            } else if (droidPrefs.batchTime().get() == 2) {
                txtBatch.setText(getString(R.string.batched_every_4_hour));
                droidPrefs.batchTime().put(4);
            } else if (droidPrefs.batchTime().get() == 4) {
                txtBatch.setText(getString(R.string.batched_every_15_minutes));
                droidPrefs.batchTime().put(15);
            }
        }
        enableRadioOnPosition(1);
        FirebaseHelper.getIntance().logTempoIntervalTime(1, droidPrefs.batchTime().get(), "");
    }

    @Click
    void fabMute() {
        fabMenu.setClickable(false);
        fabMenu.close(true);
        droidPrefs.tempoSoundProfile().put(0);
        txtTop.setBackgroundColor(ContextCompat.getColor(TempoActivity.this, R.color.transparent));
    }

    @Click
    void fabSound() {
        fabMenu.setClickable(false);
        fabMenu.close(true);
        droidPrefs.tempoSoundProfile().put(1);
        txtTop.setBackgroundColor(ContextCompat.getColor(TempoActivity.this, R.color.transparent));

    }

    @Click
    void txtOnlyAtTime1() {
        enableRadioOnPosition(2);
        if (radioOnlyAt.isChecked()) {
            Calendar calendar1 = Calendar.getInstance();
            String str1 = droidPrefs.onlyAt().get().split(",")[0];
            calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str1.split(":")[0]));
            calendar1.set(Calendar.MINUTE, Integer.parseInt(str1.split(":")[1]));
            showTimePicker(calendar1, 0, false);
        }

    }

    @Click
    void txtOnlyAtTime2() {
        enableRadioOnPosition(2);
        if (radioOnlyAt.isChecked()) {
            Calendar calendar1 = Calendar.getInstance();
            String str1 = droidPrefs.onlyAt().get().split(",")[1];
            calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str1.split(":")[0]));
            calendar1.set(Calendar.MINUTE, Integer.parseInt(str1.split(":")[1]));
            showTimePicker(calendar1, 1, false);
        }

    }

    @Click
    void txtOnlyAtTime3() {
        enableRadioOnPosition(2);
        if (radioOnlyAt.isChecked()) {
            Calendar calendar1 = Calendar.getInstance();
            String str1 = droidPrefs.onlyAt().get().split(",")[2];
            calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str1.split(":")[0]));
            calendar1.set(Calendar.MINUTE, Integer.parseInt(str1.split(":")[1]));
            showTimePicker(calendar1, 2, false);
        }

    }

    private void enableRadioOnPosition(int pos) {
        if (pos == 0) {
            int sound = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, sound, 0);
            Tracer.d("Rajesh", audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        } else {
            Tracer.d("Rajesh Before", audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 1, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            Tracer.d("Rajesh after", audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        }

        String timeString;
        if (android.text.format.DateFormat.is24HourFormat(this)) {
            timeString = "HH:mm";
        } else {
            timeString = "hh:mm a";
        }
        SimpleDateFormat df = new SimpleDateFormat(timeString, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int str = calendar.get(Calendar.AM_PM);
        if (pos == 0) {
            radioIndividual.setChecked(true);
            radioBatched.setChecked(false);
            radioOnlyAt.setChecked(false);
            droidPrefs.tempoType().put(0);
            strMessage = getString(R.string.msg_individual);
            txtMessage.setText(strMessage);
        } else if (pos == 1) {
            radioIndividual.setChecked(false);
            radioBatched.setChecked(true);
            radioOnlyAt.setChecked(false);
            droidPrefs.tempoType().put(1);
            strMessage = getString(R.string.msg_do_not_disturb);
            if (droidPrefs.batchTime().get() == 15) {
                txtBatch.setText(getString(R.string.batched_every_15_minutes));
                strMessage = strMessage + "\n" + getString(R.string.msg_quarter) + "\n";
                if (minute >= 0 && minute < 15) {
                    calendar.set(Calendar.MINUTE, 15);
                } else if (minute >= 15 && minute < 30) {
                    calendar.set(Calendar.MINUTE, 30);
                } else if (minute >= 30 && minute < 45) {
                    calendar.set(Calendar.MINUTE, 45);
                } else if (minute >= 45 && minute < 60) {
                    calendar.set(Calendar.MINUTE, 60);
                }
            } else if (droidPrefs.batchTime().get() == 30) {
                txtBatch.setText(getString(R.string.batched_every_30_minutes));
                strMessage = strMessage + "\n" + getString(R.string.msg_half) + "\n";
                if (minute >= 0 && minute < 30) {
                    calendar.set(Calendar.MINUTE, 30);
                } else if (minute >= 30 && minute < 60) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    calendar.set(Calendar.MINUTE, 0);
                }
            } else if (droidPrefs.batchTime().get() == 1) {
                txtBatch.setText(getString(R.string.batched_every_1_hour));
                strMessage = strMessage + "\n" + getString(R.string.msg_1hour) + "\n";
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                calendar.set(Calendar.MINUTE, 0);
            } else if (droidPrefs.batchTime().get() == 2) {
                txtBatch.setText(getString(R.string.batched_every_2_hour));
                strMessage = strMessage + "\n" + getString(R.string.msg_2hour) + "\n";
                calendar.add(Calendar.HOUR_OF_DAY, 2);
                calendar.set(Calendar.MINUTE, 0);
            } else if (droidPrefs.batchTime().get() == 4) {
                txtBatch.setText(getString(R.string.batched_every_4_hour));
                strMessage = strMessage + "\n" + getString(R.string.msg_4hour) + "\n";
                calendar.add(Calendar.HOUR_OF_DAY, 4);
                calendar.set(Calendar.MINUTE, 0);
            }
            strMessage = strMessage + getString(R.string.msg_next_delivery) + df.format(calendar.getTime());
            txtMessage.setText(strMessage);
        } else if (pos == 2) {
            radioIndividual.setChecked(false);
            radioBatched.setChecked(false);
            radioOnlyAt.setChecked(true);
            droidPrefs.tempoType().put(2);
            bindOnlyAt();
        }
    }


    private void bindOnlyAt() {


        String timeString;
        if (android.text.format.DateFormat.is24HourFormat(this)) {
            timeString = "HH:mm";
        } else {
            timeString = "hh:mm a";
        }
        SimpleDateFormat df = new SimpleDateFormat(timeString, Locale.getDefault());
        String strTimeData = droidPrefs.onlyAt().get();
        String strTime[] = strTimeData.split(",");


        if (strTime.length == 1) {
            txtSign1.setVisibility(View.GONE);
            txtSign2.setVisibility(View.GONE);
            txtOnlyAtTime1.setVisibility(View.VISIBLE);
            txtOnlyAtTime2.setVisibility(View.GONE);
            txtOnlyAtTime3.setVisibility(View.GONE);
            txtAdd.setVisibility(View.VISIBLE);

            Calendar calendar1 = Calendar.getInstance();
            Calendar currentTime = Calendar.getInstance();
            String str1 = strTime[0];
            int setMinute, setHours;

            setHours = Integer.parseInt(str1.split(":")[0]);
            setMinute = Integer.parseInt(str1.split(":")[1]);

            calendar1.set(Calendar.HOUR_OF_DAY, setHours);
            calendar1.set(Calendar.MINUTE, setMinute);
            txtOnlyAtTime1.setText("" + df.format(calendar1.getTime()));
            if (radioOnlyAt.isChecked()) {
                strMessage = getString(R.string.msg_do_not_disturb);
                strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + df.format(calendar1.getTime());
                txtMessage.setText(strMessage);
            }
        } else if (strTime.length == 2) {
            txtSign2.setVisibility(View.GONE);
            txtSign1.setVisibility(View.VISIBLE);
            txtSign1.setText(" & ");
            txtOnlyAtTime1.setVisibility(View.VISIBLE);
            txtOnlyAtTime2.setVisibility(View.VISIBLE);
            txtOnlyAtTime3.setVisibility(View.GONE);
            txtAdd.setVisibility(View.VISIBLE);
            Calendar calendar1 = Calendar.getInstance();
            Calendar currentTime = Calendar.getInstance();

            int systemMinute, setMinute, systemHours, setHours;

            ArrayList<Data> hourList = new ArrayList<>();

            systemHours = currentTime.get(Calendar.HOUR_OF_DAY);
            systemMinute = currentTime.get(Calendar.MINUTE);

            String str1 = strTime[0];
            setHours = Integer.parseInt(str1.split(":")[0]);
            setMinute = Integer.parseInt(str1.split(":")[1]);
            calendar1.set(Calendar.HOUR_OF_DAY, setHours);
            calendar1.set(Calendar.MINUTE, setMinute);
            txtOnlyAtTime1.setText("" + df.format(calendar1.getTime()));
            hourList.add(new Data(setHours, setMinute, df.format(calendar1.getTime())));


            String str2 = strTime[1];
            setHours = Integer.parseInt(str2.split(":")[0]);
            setMinute = Integer.parseInt(str2.split(":")[1]);
            calendar1.set(Calendar.HOUR_OF_DAY, setHours);
            calendar1.set(Calendar.MINUTE, setMinute);
            txtOnlyAtTime2.setText("" + df.format(calendar1.getTime()));
            hourList.add(new Data(setHours, setMinute, df.format(calendar1.getTime())));
            try {
                Collections.sort(hourList, new HoursComparator());
                for (int i = 0; i < hourList.size(); i++) {
                    if (hourList.get(i).getHours() == systemHours) {
                        if (hourList.get(i).getMinute() > systemMinute) {
                            String str4 = strTime[i];
                            setHours = Integer.parseInt(str4.split(":")[0]);
                            setMinute = Integer.parseInt(str4.split(":")[1]);
                            calendar1.set(Calendar.HOUR_OF_DAY, setHours);
                            calendar1.set(Calendar.MINUTE, setMinute);
                            if (radioOnlyAt.isChecked()) {
                                strMessage = getString(R.string.msg_do_not_disturb);
                                strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + hourList.get(i).getIndex();
                                txtMessage.setText(strMessage);
                            }
                            break;
                        } else {
                            if (radioOnlyAt.isChecked()) {
                                strMessage = getString(R.string.msg_do_not_disturb);
                                strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + hourList.get(0).getIndex();
                                txtMessage.setText(strMessage);
                            }

                        }
                    } else if (hourList.get(i).getHours() > systemHours) {
                        String str4 = strTime[i];
                        setHours = Integer.parseInt(str4.split(":")[0]);
                        setMinute = Integer.parseInt(str4.split(":")[1]);
                        calendar1.set(Calendar.HOUR_OF_DAY, setHours);
                        calendar1.set(Calendar.MINUTE, setMinute);
                        if (radioOnlyAt.isChecked()) {
                            strMessage = getString(R.string.msg_do_not_disturb);
                            strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + hourList.get(i).getIndex();
                            txtMessage.setText(strMessage);
                        }
                        break;
                    } else {
                        if (radioOnlyAt.isChecked()) {
                            strMessage = getString(R.string.msg_do_not_disturb);
                            strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + hourList.get(0).getIndex();
                            txtMessage.setText(strMessage);
                        }
                    }
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
            }

        } else if (strTime.length == 3) {
            txtSign1.setVisibility(View.VISIBLE);
            txtSign2.setVisibility(View.VISIBLE);
            txtSign1.setText(", ");
            txtSign2.setText(" & ");
            txtOnlyAtTime1.setVisibility(View.VISIBLE);
            txtOnlyAtTime2.setVisibility(View.VISIBLE);
            txtOnlyAtTime3.setVisibility(View.VISIBLE);
            txtAdd.setVisibility(View.GONE);

            Calendar calendar1 = Calendar.getInstance();
            Calendar currentTime = Calendar.getInstance();

            ArrayList<Data> hourList = new ArrayList<>();

            int systemMinute, setMinute, systemHours, setHours;
            systemHours = currentTime.get(Calendar.HOUR_OF_DAY);
            systemMinute = currentTime.get(Calendar.MINUTE);


            String str1 = strTime[0];
            setHours = Integer.parseInt(str1.split(":")[0]);
            setMinute = Integer.parseInt(str1.split(":")[1]);
            calendar1.set(Calendar.HOUR_OF_DAY, setHours);
            calendar1.set(Calendar.MINUTE, setMinute);
            txtOnlyAtTime1.setText("" + df.format(calendar1.getTime()));
            hourList.add(new Data(setHours, setMinute, df.format(calendar1.getTime())));

            String str2 = strTime[1];
            setHours = Integer.parseInt(str2.split(":")[0]);
            setMinute = Integer.parseInt(str2.split(":")[1]);
            calendar1.set(Calendar.HOUR_OF_DAY, setHours);
            calendar1.set(Calendar.MINUTE, setMinute);
            txtOnlyAtTime2.setText("" + df.format(calendar1.getTime()));
            hourList.add(new Data(setHours, setMinute, df.format(calendar1.getTime())));

            String str3 = strTime[2];
            setHours = Integer.parseInt(str3.split(":")[0]);
            setMinute = Integer.parseInt(str3.split(":")[1]);
            calendar1.set(Calendar.HOUR_OF_DAY, setHours);
            calendar1.set(Calendar.MINUTE, setMinute);
            txtOnlyAtTime3.setText("" + df.format(calendar1.getTime()));
            hourList.add(new Data(setHours, setMinute, df.format(calendar1.getTime())));
            try {
                Collections.sort(hourList, new HoursComparator());
                for (int i = 0; i < hourList.size(); i++) {
                    if (hourList.get(i).getHours() == systemHours) {
                        if (hourList.get(i).getMinute() > systemMinute) {
                            String str4 = strTime[i];
                            setHours = Integer.parseInt(str4.split(":")[0]);
                            setMinute = Integer.parseInt(str4.split(":")[1]);
                            calendar1.set(Calendar.HOUR_OF_DAY, setHours);
                            calendar1.set(Calendar.MINUTE, setMinute);
                            if (radioOnlyAt.isChecked()) {
                                strMessage = getString(R.string.msg_do_not_disturb);
                                strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + hourList.get(i).getIndex();
                                txtMessage.setText(strMessage);
                            }
                            break;
                        } else {
                            if (radioOnlyAt.isChecked()) {
                                strMessage = getString(R.string.msg_do_not_disturb);
                                strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + hourList.get(0).getIndex();
                                txtMessage.setText(strMessage);
                            }
                        }
                    } else if (hourList.get(i).getHours() > systemHours) {
                        String str4 = strTime[i];
                        setHours = Integer.parseInt(str4.split(":")[0]);
                        setMinute = Integer.parseInt(str4.split(":")[1]);
                        calendar1.set(Calendar.HOUR_OF_DAY, setHours);
                        calendar1.set(Calendar.MINUTE, setMinute);
                        if (radioOnlyAt.isChecked()) {
                            strMessage = getString(R.string.msg_do_not_disturb);
                            strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + hourList.get(i).getIndex();
                            txtMessage.setText(strMessage);
                        }
                        break;
                    } else {
                        if (radioOnlyAt.isChecked()) {
                            strMessage = getString(R.string.msg_do_not_disturb);
                            strMessage = strMessage + "\n" + getString(R.string.msg_next_delivery) + hourList.get(0).getIndex();
                            txtMessage.setText(strMessage);
                        }
                    }
                }
            } catch (Exception e) {
                CoreApplication.getInstance().logException(e);
            }


        }
    }

    class Data {
        private int hours;
        private int minute;
        private String index;

        public Data(int hours, int minute, String index) {
            this.hours = hours;
            this.minute = minute;
            this.index = index;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }
    }

    public class HoursComparator implements Comparator<Data> {
        @Override
        public int compare(Data o1, Data o2) {
            if (o1.getHours() == o2.getHours()) {
                return o1.getMinute() - o2.getMinute();
            }
            return o1.getHours() - o2.getHours();
        }
    }

}