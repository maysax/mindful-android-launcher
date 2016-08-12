package minium.co.notes.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import minium.co.core.log.Tracer;

public class NoteEventReceiver extends BroadcastReceiver {
    public NoteEventReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("minium.co.notes.CREATE_NOTES")) {
            Tracer.d("minium.co.notes.CREATE_NOTES received");
            

        } else if (intent.getAction().equals("minium.co.notes.EDIT_NOTES")) {
            Tracer.d("minium.co.notes.EDIT_NOTES received");
        }
    }
}
