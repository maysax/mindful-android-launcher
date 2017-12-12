package minium.co.notes.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.androidnetworking.core.Core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.FirebaseEvent;
import minium.co.core.event.HomePressEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.notes.R;
import minium.co.notes.colorpicker.ColorPickerDialog;
import minium.co.notes.colorpicker.ColorPickerSwatch;
import minium.co.notes.evernote.EvernoteManager;

import static minium.co.core.util.DataUtils.NEW_NOTE_REQUEST;
import static minium.co.core.util.DataUtils.NOTE_BODY;
import static minium.co.core.util.DataUtils.NOTE_COLOUR;
import static minium.co.core.util.DataUtils.NOTE_FAVOURED;
import static minium.co.core.util.DataUtils.NOTE_FONT_SIZE;
import static minium.co.core.util.DataUtils.NOTE_HIDE_BODY;
import static minium.co.core.util.DataUtils.NOTE_REQUEST_CODE;
import static minium.co.core.util.DataUtils.NOTE_TITLE;
import static minium.co.core.util.DataUtils.retrieveData;
import static minium.co.core.util.DataUtils.saveData;


public class EditActivity extends CoreActivity implements Toolbar.OnMenuItemClickListener {

    // Layout components
    private EditText titleEdit, bodyEdit;
    private RelativeLayout relativeLayoutEdit;
    private Toolbar toolbar;
    private MenuItem menuHideBody;

    private InputMethodManager imm;
    private Bundle bundle;

    private String[] colourArr; // Colours string array
    private int[] colourArrResId; // colourArr to resource int array
    private int[] fontSizeArr; // Font sizes int array
    private String[] fontSizeNameArr; // Font size names string array

    // Defaults
    private String colour = "#FFFFFF"; // white default
    private int fontSize = 18; // Medium default
    private Boolean hideBody = false;

    private AlertDialog fontDialog, saveChangesDialog;
    private ColorPickerDialog colorPickerDialog;
    private long startTime;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CoreApplication.getInstance().setEditNotOpen(true);
        // Android version >= 18 -> set orientation fullUser
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);

        // Initialize colours and font sizes arrays
        colourArr = getResources().getStringArray(R.array.colours);

        colourArrResId = new int[colourArr.length];
        for (int i = 0; i < colourArr.length; i++)
            colourArrResId[i] = Color.parseColor(colourArr[i]);

        fontSizeArr = new int[]{14, 18, 22}; // 0 for small, 1 for medium, 2 for large
        fontSizeNameArr = getResources().getStringArray(R.array.fontSizeNames);

        setContentView(R.layout.activity_edit);

        // Init layout components
        toolbar = findViewById(R.id.toolbarEdit);
        int statusbarheight = retrieveStatusBarHeight();

//
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, statusbarheight, 0, 0);
        toolbar.setLayoutParams(params);
//

        titleEdit = findViewById(R.id.titleEdit);
        bodyEdit = findViewById(R.id.bodyEdit);
        relativeLayoutEdit = findViewById(R.id.relativeLayoutEdit);
        ScrollView scrollView = findViewById(R.id.scrollView);

        imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

        if (toolbar != null)
            initToolbar();

        // If scrollView touched and note body doesn't have focus -> request focus and go to body end
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!bodyEdit.isFocused()) {
                    bodyEdit.requestFocus();
                    bodyEdit.setSelection(bodyEdit.getText().length());
                    // Force show keyboard
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);

                    return true;
                }

                return false;
            }
        });

        // Get data bundle from MainActivity
        bundle = getIntent().getExtras();

        if (bundle != null) {
            // If current note is not new -> initialize colour, font, hideBody and EditTexts
            Tracer.d("Notes Edit" + bundle.getInt(NOTE_REQUEST_CODE));
            if (bundle.getInt(NOTE_REQUEST_CODE) != NEW_NOTE_REQUEST) {
                colour = bundle.getString(NOTE_COLOUR);
                fontSize = bundle.getInt(NOTE_FONT_SIZE);
                hideBody = bundle.getBoolean(NOTE_HIDE_BODY);

                titleEdit.setText(bundle.getString(NOTE_TITLE));
                bodyEdit.setText(bundle.getString(NOTE_BODY));
                bodyEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

                if (hideBody)
                    menuHideBody.setTitle(R.string.label_showNoteBody);
            }

            // If current note is new -> request keyboard focus to note title and show keyboard
            else if (bundle.getInt(NOTE_REQUEST_CODE) == NEW_NOTE_REQUEST) {
                titleEdit.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }

            // Set background colour to note colour
            relativeLayoutEdit.setBackgroundColor(Color.parseColor(colour));
        }

        initDialogs(this);
    }


    /**
     * Initialize toolbar with required components such as
     * - title, navigation icon + listener, menu/OnMenuItemClickListener, menuHideBody -
     */
    @SuppressLint("PrivateResource")
    protected void initToolbar() {
        toolbar.setTitle("");

        // Set a 'Back' navigation icon in the Toolbar and handle the click
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Inflate menu_edit to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.menu_edit);

        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(this);

        Menu menu = toolbar.getMenu();

        if (menu != null)
            menuHideBody = menu.findItem(R.id.action_hide_show_body);
    }


    /**
     * Implementation of AlertDialogs such as
     * - colorPickerDialog, fontDialog and saveChangesDialog -
     *
     * @param context The Activity context of the dialogs; in this case EditActivity context
     */
    protected void initDialogs(Context context) {
        // Colour picker dialog
        colorPickerDialog = ColorPickerDialog.newInstance(R.string.title_noteColor,
                colourArrResId, Color.parseColor(colour), 3,
                isTablet(this) ? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);

        // Colour picker listener in colour picker dialog
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                // Format selected colour to string
                String selectedColourAsString = String.format("#%06X", (0xFFFFFF & color));

                // Check which colour is it and equal to main colour
                for (String aColour : colourArr)
                    if (aColour.equals(selectedColourAsString))
                        colour = aColour;

                // Re-set background colour
                relativeLayoutEdit.setBackgroundColor(Color.parseColor(colour));
            }
        });


        // Font size picker dialog
        fontDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.title_fontSize)
                .setItems(fontSizeNameArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Font size updated with new pick
                        fontSize = fontSizeArr[which];
                        bodyEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
                    }
                })
                .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();


        // 'Save changes?' dialog
        saveChangesDialog = new AlertDialog.Builder(context)
                .setMessage(R.string.title_saveChanges)
                .setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If 'Yes' clicked -> check if title is empty
                        // If title not empty -> save and go back; Otherwise toast
                        if (!isEmpty(titleEdit))
                            saveChanges();
                        else
                            toastEditTextCannotBeEmpty();
                    }
                })
                .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If 'No' clicked in new note -> put extra 'discard' to show toast
                        if (bundle != null && bundle.getInt(NOTE_REQUEST_CODE) ==
                                NEW_NOTE_REQUEST) {

                            Intent intent = new Intent();
                            intent.putExtra("request", "discard");

                            setResult(RESULT_CANCELED, intent);

                            imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                            dialog.dismiss();
                            finish();
                            CoreApplication.getInstance().setEditNotOpen(false);
                            overridePendingTransition(0, 0);
                        }
                    }
                })
                .create();
    }


    /**
     * Check if current device has tablet screen size or not
     *
     * @param context current application context
     * @return true if device is tablet, false otherwise
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * Item clicked in Toolbar menu callback method
     *
     * @param item Item clicked
     * @return true if click detected and logic finished, false otherwise
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        // Note colour menu item clicked -> show colour picker dialog
        if (id == R.id.action_note_colour) {
            colorPickerDialog.show(getFragmentManager(), "colourPicker");
            return true;
        }

        // Font size menu item clicked -> show font picker dialog
        if (id == R.id.action_font_size) {
            fontDialog.show();
            return true;
        }

        // If 'Hide note body in list' or 'Show note body in list' clicked
        if (id == R.id.action_hide_show_body) {
            // If hideBody false -> set to true and change menu item text to 'Show note body in list'
            if (!hideBody) {
                hideBody = true;
                menuHideBody.setTitle(R.string.label_showNoteBody);

                // Toast note body will be hidden
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.msg_noteWillBeHidden),
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            // If hideBody true -> set to false and change menu item text to 'Hide note body in list'
            else {
                hideBody = false;
                menuHideBody.setTitle(R.string.label_hideNoteBody);

                // Toast note body will be shown
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.msg_noteWillBeShown),
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            return true;
        }

        return false;
    }


    /**
     * Create an Intent with title, body, colour, font size and hideBody extras
     * Set RESULT_OK and go back to MainActivity
     */
    protected void saveChanges() {
        Intent intent = new Intent();

        // Package everything and send back to activity with OK
        intent.putExtra(NOTE_TITLE, titleEdit.getText().toString());
        intent.putExtra(NOTE_BODY, bodyEdit.getText().toString());
        intent.putExtra(NOTE_COLOUR, colour);
        intent.putExtra(NOTE_FONT_SIZE, fontSize);
        intent.putExtra(NOTE_HIDE_BODY, hideBody);

        setResult(RESULT_OK, intent);

        imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

        finish();
        CoreApplication.getInstance().setEditNotOpen(false);
        overridePendingTransition(0, 0);
    }


    /**
     * Back or navigation '<-' pressed
     */
    @Override
    public void onBackPressed() {
        // New note -> show 'Save changes?' dialog
        if (bundle != null && bundle.getInt(NOTE_REQUEST_CODE) == NEW_NOTE_REQUEST)
            saveChangesDialog.show();

            // Existing note
        else {
            /*
             * If title is not empty -> Check if note changed
             *  If yes -> saveChanges
             *  If not -> hide keyboard if showing and finish
             */
            if (!isEmpty(titleEdit)) {
                if (bundle != null && !(titleEdit.getText().toString().equals(bundle.getString(NOTE_TITLE))) ||
                        !(bodyEdit.getText().toString().equals(bundle.getString(NOTE_BODY))) ||
                        !(colour.equals(bundle.getString(NOTE_COLOUR))) ||
                        fontSize != bundle.getInt(NOTE_FONT_SIZE) ||
                        hideBody != bundle.getBoolean(NOTE_HIDE_BODY)) {

                    saveChanges();
                } else {
                    imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                    finish();
                    CoreApplication.getInstance().setEditNotOpen(false);
                    overridePendingTransition(0, 0);
                }
            }

            // If title empty -> Toast title cannot be empty
            else
                toastEditTextCannotBeEmpty();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoreApplication.getInstance().setEditNotOpen(false);
    }

    /**
     * Check if passed EditText text is empty or not
     *
     * @param editText The EditText widget to check
     * @return true if empty, false otherwise
     */
    protected boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    /**
     * Show Toast for 'Title cannot be empty'
     */
    protected void toastEditTextCannotBeEmpty() {
        Toast toast = Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.msg_titleCannotBeEmpty),
                Toast.LENGTH_LONG);
        toast.show();
    }


    /**
     * If current window loses focus -> hide keyboard
     *
     * @param hasFocus parameter passed by system; true if focus changed, false otherwise
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus)
            if (imm != null && titleEdit != null)
                imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);
    }


    /**
     * Orientation changed callback method
     * If orientation changed -> If any AlertDialog is showing -> dismiss it to prevent WindowLeaks
     *
     * @param newConfig Configuration passed by system
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (colorPickerDialog != null && colorPickerDialog.isDialogShowing())
            colorPickerDialog.dismiss();

        if (fontDialog != null && fontDialog.isShowing())
            fontDialog.dismiss();

        if (saveChangesDialog != null && saveChangesDialog.isShowing())
            saveChangesDialog.dismiss();

        super.onConfigurationChanged(newConfig);
    }

    public int retrieveStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        try {
            if (UIUtils.isMyLauncherDefault(this)) {
                // onBackPressed();
                if (!isEmpty(titleEdit)) {
                    saveChanges1();
                    Intent intent = new Intent();
                    intent.putExtra("request", "HOME");
                    setResult(RESULT_CANCELED, intent);
                    imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);
                    finish();
                    CoreApplication.getInstance().setEditNotOpen(false);
                    overridePendingTransition(0, 0);
                } else {
                    toastEditTextCannotBeEmpty();
                }
            } else {
                if (!isEmpty(titleEdit)) {
                    saveChanges1();
                    Intent intent = new Intent();
                    intent.putExtra("request", "");
                    setResult(RESULT_CANCELED, intent);
                    imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);
                    finish();
                    CoreApplication.getInstance().setEditNotOpen(false);
                    overridePendingTransition(0, 0);
                }
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

    private void saveChanges1() {
        if (bundle != null) {
            Tracer.d("Notes Edit  1" + bundle.getInt(NOTE_REQUEST_CODE));
            JSONArray notes = new JSONArray();
            JSONArray tempNotes = retrieveData(localPath);
            // If not null -> equal main notes to retrieved notes
            if (tempNotes != null)
                notes = tempNotes;
            // If current note is not new -> initialize colour, font, hideBody and EditTexts
            if (bundle.getInt(NOTE_REQUEST_CODE) != NEW_NOTE_REQUEST) {
                {
                    JSONObject newNoteObject = null;

                    try {

                        // Update array item with new note data
                        newNoteObject = notes.getJSONObject(bundle.getInt(NOTE_REQUEST_CODE));
                        newNoteObject.put(NOTE_TITLE, titleEdit.getText().toString());
                        newNoteObject.put(NOTE_BODY, bodyEdit.getText().toString());
                        newNoteObject.put(NOTE_COLOUR, colour);
                        newNoteObject.put(NOTE_FONT_SIZE, fontSize);
                        newNoteObject.put(NOTE_HIDE_BODY, hideBody);

                        // Update note at position 'requestCode'
                        notes.put(bundle.getInt(NOTE_REQUEST_CODE), newNoteObject);

                    } catch (JSONException e) {
                        CoreApplication.getInstance().logException(e);
                        e.printStackTrace();
                    }

                    // If newNoteObject not null -> save notes array to local file and notify adapter
                    if (newNoteObject != null) {
                        Boolean saveSuccessful = saveData(localPath, notes);

                        if (saveSuccessful) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.msg_noteSaved),
                                    Toast.LENGTH_SHORT);
                            toast.show();

                        }
                    }
                }

            }
            // If current note is new -> request keyboard focus to note title and show keyboard
            else if (bundle.getInt(NOTE_REQUEST_CODE) == NEW_NOTE_REQUEST) {
                JSONObject newNoteObject = null;
                try {
                    // Add new note to array
                    newNoteObject = new JSONObject();
                    newNoteObject.put(NOTE_TITLE, titleEdit.getText().toString());
                    newNoteObject.put(NOTE_BODY, bodyEdit.getText().toString());
                    newNoteObject.put(NOTE_COLOUR, colour);
                    newNoteObject.put(NOTE_FAVOURED, false);
                    newNoteObject.put(NOTE_FONT_SIZE, fontSize);
                    newNoteObject.put(NOTE_HIDE_BODY, hideBody);

                    notes.put(newNoteObject);

                } catch (JSONException e) {
                    CoreApplication.getInstance().logException(e);
                    e.printStackTrace();
                }

                // If newNoteObject not null -> save notes array to local file and notify adapter
                Boolean saveSuccessful = saveData(localPath, notes);
                new EvernoteManager().createNote(newNoteObject);
                if (saveSuccessful) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.msg_noteCreated),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(new FirebaseEvent(EditActivity.this.getClass().getSimpleName(),startTime));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }
}
