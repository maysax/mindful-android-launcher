package minium.co.launcher2.nfc;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;

import java.io.IOException;

import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.flow.FlowActivity_;

/**
 * Created by Shahab on 12/15/2016.
 */

public class NfcManager {

    private boolean mResumed = false;
    private boolean mWriteMode = false;
    NfcAdapter mNfcAdapter;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] mWriteTagFilters;
    IntentFilter[] mNdefExchangeFilters;


    private static int MSG_START_CONNECTION_CHECK = 0;
    private static int MSG_CONNECTION_LOST = 1;

    private CoreActivity context;

    public void onCreate(CoreActivity context) {
        this.context = context;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);

        // Handle all of our received NFC intents in this activity.
        mNfcPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading a note from a tag or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            //System.out.println("ndefDetected :: "+ ndefDetected.countActions());
            ndefDetected.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

        // Intent filters for writing to a tag
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] { tagDetected };
    }

    public void onResume() {
        mResumed = true;
        // Sticky notes received from Android
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(context.getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(context.getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            context.setIntent(new Intent()); // Consume this intent.
        }
        enableNdefExchangeMode();
    }

    public void onPause() {
        mResumed = false;
        mNfcAdapter.disableForegroundNdefPush(context);
    }

    public void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!mWriteMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            FlowActivity_.intent(context).start();
            //promptForContent(msgs[0]);
        }

        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //writeTag(getNoteAsNdef(), detectedTag);
        }
    }



    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
        //mNfcAdapter.enableForegroundNdefPush(context, getNoteAsNdef());
        mNfcAdapter.enableForegroundDispatch(context, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    private void disableNdefExchangeMode() {
        mNfcAdapter.disableForegroundNdefPush(context);
        mNfcAdapter.disableForegroundDispatch(context);
    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[] {
                tagDetected
        };
        mNfcAdapter.enableForegroundDispatch(context, mNfcPendingIntent, mWriteTagFilters, null);
    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(context);
    }

    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    UIUtils.toast(context, "Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    UIUtils.toast(context, "Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                UIUtils.toast(context, "Wrote message to pre-formatted tag.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        UIUtils.toast(context, "Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        UIUtils.toast(context, "Failed to format tag.");
                        return false;
                    }
                } else {
                    UIUtils.toast(context, "Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            UIUtils.toast(context, "Failed to write tag");
        }

        return false;
    }
}
