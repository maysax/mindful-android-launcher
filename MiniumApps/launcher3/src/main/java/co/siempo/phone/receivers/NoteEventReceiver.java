package co.siempo.phone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.UIUtils;

import static co.siempo.phone.utils.DataUtils.NOTES_FILE_NAME;
import static co.siempo.phone.utils.DataUtils.NOTE_BODY;
import static co.siempo.phone.utils.DataUtils.NOTE_COLOUR;
import static co.siempo.phone.utils.DataUtils.NOTE_FAVOURED;
import static co.siempo.phone.utils.DataUtils.NOTE_FONT_SIZE;
import static co.siempo.phone.utils.DataUtils.NOTE_HIDE_BODY;
import static co.siempo.phone.utils.DataUtils.NOTE_TITLE;
import static co.siempo.phone.utils.DataUtils.retrieveData;
import static co.siempo.phone.utils.DataUtils.saveData;

public class NoteEventReceiver extends BroadcastReceiver {
    public NoteEventReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("minium.co.notes.CREATE_NOTES")) {
                Tracer.i("minium.co.notes.CREATE_NOTES received");

                saveNotes(context, intent);

            } else if (intent.getAction().equals("minium.co.notes.EDIT_NOTES")) {
                Tracer.i("minium.co.notes.EDIT_NOTES received");
            }
        }
    }

    private void saveNotes(Context context, Intent intent) {

        JSONObject newNoteObject;
        File localPath = new File(context.getFilesDir() + "/" + NOTES_FILE_NAME);

        // Init notes array
        JSONArray notes = new JSONArray();

        // Retrieve from local path
        JSONArray tempNotes = retrieveData(localPath);

        // If not null -> equal main notes to retrieved notes
        if (tempNotes != null)
            notes = tempNotes;

        Tracer.i("All notes: ", notes);

        try {
            // Add new note to array
            newNoteObject = new JSONObject();
//            newNoteObject.put(NOTE_TITLE, SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).format(new Date()));
            newNoteObject.put(NOTE_TITLE, getTitle(intent.getStringExtra(NOTE_TITLE)));
            // newNoteObject.put(NOTE_BODY, intent.getStringExtra(NOTE_BODY));
            newNoteObject.put(NOTE_BODY, "");
            newNoteObject.put(NOTE_COLOUR, "#FFFFFF");
            newNoteObject.put(NOTE_FAVOURED, false);
            newNoteObject.put(NOTE_FONT_SIZE, 18);
            newNoteObject.put(NOTE_HIDE_BODY, false);

            notes.put(newNoteObject);

            Tracer.i("New note: " + newNoteObject);

        } catch (JSONException e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }

        // If newNoteObject not null -> save notes array to local file and notify adapter

        Boolean saveSuccessful = saveData(localPath, notes);

        if (saveSuccessful) {
            UIUtils.toast(context, context.getString(R.string.msg_noteCreated));
        }
    }

    private String getTitle(String body) {
        if (body.isEmpty()) return "";
        String[] splits = body.split(" ");
        String ret = "";

        if (splits.length > 0) {
            ret += splits[0];
            ret += " ";
        }

        if (splits.length > 1) {
            ret += splits[1];
            ret += " ";
        }


        if (splits.length > 2) {
            ret += splits[2];
        }

        return ret;

    }
}
