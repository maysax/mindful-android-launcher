package minium.co.launcher2.nfc;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.MainActivity;
import minium.co.launcher2.MainActivity_;
import minium.co.launcher2.events.NFCEvent;
import minium.co.launcher2.flow.FlowActivity_;

/**
 * Created by Shahab on 12/15/2016.
 */

public class NfcManager {

    private NfcAdapter _nfcAdapter;
    private PendingIntent _pendingIntent;
    private IntentFilter[] _intentFilters;
    //
    private final String _MIME_TYPE = "text/plain";

    private MainActivity context;

    private Handler nfcCheckHandler;


    public void init(MainActivity context) {
        this.context = context;
        _nfcAdapter = NfcAdapter.getDefaultAdapter(context);

        if (_nfcAdapter == null)
        {
            Tracer.w("This device does not support NFC.");
            return;
        }

        if (_nfcAdapter.isEnabled())
        {
            _pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try
            {
                ndefDetected.addDataType(_MIME_TYPE);
            } catch (IntentFilter.MalformedMimeTypeException e)
            {
                Tracer.e(e, e.getMessage());
            }

            _intentFilters = new IntentFilter[] { ndefDetected };
            nfcCheckHandler = new Handler();
        }
    }

    public void onResume() {
        if (_nfcAdapter != null)
            _nfcAdapter.enableForegroundDispatch(context, _pendingIntent, _intentFilters, null);
    }


    public void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            List<String> msgs = NFCUtils.getStringsFromNfcIntent(intent);

            Tracer.i("NFC tag read: " + msgs.get(0));
            FlowActivity_.intent(context).start();

            final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            nfcCheckHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Ndef ndef = Ndef.get(tag);
                    try {
                        ndef.connect();
                        Tracer.d("Connection heart-beat for nfc tag " + tag);
                        nfcCheckHandler.postDelayed(this, 1000);
                    } catch (IOException e) {
                        // if the tag is gone we might want to end the thread:
                        EventBus.getDefault().post(new NFCEvent(false));
                        Tracer.e(e, e.getMessage());
                        Tracer.d("Disconnected from nfc tag" + tag);
                        nfcCheckHandler.removeCallbacks(this);
                    } finally {
                        try {
                            ndef.close();
                        } catch (IOException e) {
                            Tracer.e(e, e.getMessage());
                        }
                    }
                }
            }, 1000);

        }
    }
}
