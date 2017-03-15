package co.minium.launcher3.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 1/9/2017.
 */

public class NFCUtils {
    public static NdefMessage getNewMessage(String mimeType, byte[] payload)
    {
        return new NdefMessage(new NdefRecord[] { getNewRecord(mimeType, payload) });
    }

    private static NdefRecord getNewRecord(String mimeType, byte[] payload)
    {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    public static boolean writeMessageToTag(NdefMessage message, Tag tag)
    {
        int size = message.toByteArray().length;
        try
        {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null)
            {
                ndef.connect();
                if (!ndef.isWritable())
                {
                    Tracer.e("Tag is not writable: ");
                    return false;
                }
                if (ndef.getMaxSize() < size)
                {
                    Tracer.e("Message exceeds the max tag size " + ndef.getMaxSize());
                    return false;
                }
                ndef.writeNdefMessage(message);
                return true;
            } else
            {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null)
                {
                    try
                    {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e)
                    {
                        Tracer.e(e, e.getMessage());
                        return false;
                    }
                } else
                {
                    Tracer.e("Undefined format");
                    return false;
                }
            }
        } catch (Exception e)
        {
            Tracer.e(e, e.getMessage());
            return false;
        }
    }

    public static List<String> getStringsFromNfcIntent(Intent intent)
    {
        List<String> payloadStrings = new ArrayList<String>();

        for (NdefMessage message : getMessagesFromIntent(intent))
        {
            for (NdefRecord record : message.getRecords())
            {
                byte[] payload = record.getPayload();
                String payloadString = new String(payload);

                if (!TextUtils.isEmpty(payloadString)) payloadStrings.add(payloadString);
            }
        }

        return payloadStrings;
    }

    public static List<NdefMessage> getMessagesFromIntent(Intent intent)
    {
        List<NdefMessage> intentMessages = new ArrayList<>();
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null)
            {
                for (Parcelable msg : rawMsgs)
                {
                    if (msg instanceof NdefMessage)
                    {
                        intentMessages.add((NdefMessage) msg);
                    }
                }
            } else
            {
                byte[] empty = new byte[] {};
                final NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                final NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                intentMessages = new ArrayList<>();
                intentMessages.add(msg);
            }
        }
        return intentMessages;
    }
}
