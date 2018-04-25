package co.siempo.phone.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.blankj.utilcode.util.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.models.UserModel;
import co.siempo.phone.service.MailChimpOperation;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;

public class EmailRequestActivity extends CoreActivity implements View.OnClickListener {
    private Button btnNotNow, btnContinue;
    private TextView txtPrivacy, txtErrorMessage;
    private TextInputEditText autoCompleteTextViewEmail;
    private PermissionUtil permissionUtil;
    private CardView cardCenter;
    private RelativeLayout relPrivacyEmail;
    private Button btnEnable;
    private ViewFlipper viewFlipperEmail;
    private TextInputLayout inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_request);
        permissionUtil = new PermissionUtil(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.bg_permissionscreenstatusbar));
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initView();
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!permissionUtil.hasGiven(PermissionUtil.ACCOUNT_PERMISSION)) {
//                askForPermission(new String[]{android.Manifest.permission.GET_ACCOUNTS});
//            }
//        }
    }

    private void initView() {
        btnNotNow = findViewById(R.id.btnNotNow);
        btnContinue = findViewById(R.id.btnContinue);
        cardCenter = findViewById(R.id.cardCenter);
        btnEnable = findViewById(R.id.btnEnable);
        relPrivacyEmail = findViewById(R.id.relPrivacyEmail);
        viewFlipperEmail = findViewById(R.id.viewFlipperEmail);

        autoCompleteTextViewEmail = findViewById(R.id.auto_mail);
        inputEmail = findViewById(R.id.inputEmail);
        autoCompleteTextViewEmail.clearFocus();
        inputEmail.clearFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            autoCompleteTextViewEmail.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS);
        }
        txtPrivacy = findViewById(R.id.txtPrivacy);
        txtErrorMessage = findViewById(R.id.txtErrorMessage);
        txtPrivacy.setOnClickListener(this);
        btnNotNow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        txtErrorMessage.setVisibility(View.INVISIBLE);
        btnEnable.setOnClickListener(this);
        if (PrefSiempo.getInstance(this).read(PrefSiempo
                .USER_SEEN_EMAIL_REQUEST, false)) {
            viewFlipperEmail.setDisplayedChild(1);
            relPrivacyEmail.setVisibility(View.GONE);

        } else {
            viewFlipperEmail.setDisplayedChild(0);
            relPrivacyEmail.setVisibility(View.VISIBLE);
        }

        try {
            Typeface myTypefaceregular = Typeface.createFromAsset(getAssets(), "fonts/robotocondensedregular.ttf");
            Typeface myTypefacemedium = Typeface.createFromAsset(getAssets(), "fonts/robotomedium.ttf");
            autoCompleteTextViewEmail.setTypeface(myTypefaceregular);
            btnNotNow.setTypeface(myTypefacemedium);
            btnContinue.setTypeface(myTypefacemedium);
        } catch (Exception e) {
            e.printStackTrace();
        }

        autoCompleteTextViewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(autoCompleteTextViewEmail.getText().toString())) {
//                    String val_email = autoCompleteTextViewEmail.getText().toString().trim();
//                    boolean isValidEmail = UIUtils.isValidEmail(val_email);
//                    if (isValidEmail) {
//                        txtErrorMessage.setVisibility(View.INVISIBLE);
//                    } else {
//
//                        txtErrorMessage.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    txtErrorMessage.setVisibility(View.INVISIBLE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtErrorMessage.setVisibility(View.INVISIBLE);
            }
        });

        autoCompleteTextViewEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendEvent();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtPrivacy:
                Intent intent = new Intent(this, PrivacyPolicyActivity.class);
                startActivity(intent);
                break;
            case R.id.btnNotNow:
                PrefSiempo.getInstance(this).write(PrefSiempo.USER_SEEN_EMAIL_REQUEST, true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    flipView();
                } else {
                    finish();
                }
                break;
            case R.id.btnContinue:
                sendEvent();
                break;
            case R.id.btnEnable:
                if (!permissionUtil.hasGiven(PermissionUtil
                        .WRITE_EXTERNAL_STORAGE_PERMISSION)) {

                    try {
                        TedPermission.with(this)
                                .setPermissionListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted() {
                                        finish();
                                    }

                                    @Override
                                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                                    }
                                })
                                .setDeniedMessage(R.string.msg_permission_denied)
                                .setPermissions(new String[]{

                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest
                                                .permission
                                                .READ_EXTERNAL_STORAGE,})
                                .check();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void flipView() {
        UIUtils.hideSoftKeyboard(this, getWindow().getDecorView().getWindowToken());
        relPrivacyEmail.setVisibility(View.GONE);
        viewFlipperEmail.setInAnimation(this, R.anim.in_from_right_email);
        viewFlipperEmail.setOutAnimation(this, R.anim.out_to_left_email);
        viewFlipperEmail.showNext();
    }

    private void sendEvent() {
        String strEmail = autoCompleteTextViewEmail.getText().toString();
        boolean isValidEmail = UIUtils.isValidEmail(strEmail);
        if (isValidEmail) {
            PrefSiempo.getInstance(this).write(PrefSiempo.USER_SEEN_EMAIL_REQUEST, true);
            PrefSiempo.getInstance(this).write(PrefSiempo
                    .USER_EMAILID, strEmail);
            try {
                if (NetworkUtils.isConnected()) {
                    new MailChimpOperation().execute(strEmail);
                    storeDataToFirebase(CoreApplication.getInstance().getDeviceId(), strEmail);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            relPrivacyEmail.setVisibility(View.GONE);
            viewFlipperEmail.setInAnimation(this, R.anim
                    .in_from_right_email);
            viewFlipperEmail.setOutAnimation(this, R.anim
                    .out_to_left_email);
            UIUtils.hideSoftKeyboard(this, getWindow().getDecorView().getWindowToken());
            viewFlipperEmail.showNext();
        } else {
            autoCompleteTextViewEmail.requestFocus();
            txtErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void storeDataToFirebase(String userId, String emailId) {
        try {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            UserModel user = new UserModel(userId, emailId);
            mDatabase.child(userId).setValue(user);
            mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Firebase RealTime", "Failed to read value.", error.toException());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
