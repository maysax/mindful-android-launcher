package co.siempo.phone.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.adapters.EmailListAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.models.UserModel;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;

public class EmailRequestActivity extends CoreActivity implements View.OnClickListener {
    EmailListAdapter arrayAdapter;
    private Button btnNotNow, btnContinue;
    private TextView txtPrivacy, txtErrorMessage;
    private AutoCompleteTextView autoCompleteTextViewEmail;
    private PermissionUtil permissionUtil;

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
        if (Build.VERSION.SDK_INT >= 23) {
            if (!permissionUtil.hasGiven(PermissionUtil.ACCOUNT_PERMISSION)) {
                askForPermission(new String[]{android.Manifest.permission.GET_ACCOUNTS});
            }
        }
    }

    private void initView() {
        btnNotNow = findViewById(R.id.btnNotNow);
        btnContinue = findViewById(R.id.btnContinue);
        autoCompleteTextViewEmail = findViewById(R.id.auto_mail);
        addAdapterToViews();
        txtPrivacy = findViewById(R.id.txtPrivacy);
        txtErrorMessage = findViewById(R.id.txtErrorMessage);
        txtPrivacy.setOnClickListener(this);
        btnNotNow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

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
                if (!TextUtils.isEmpty(autoCompleteTextViewEmail.getText().toString())) {
                    String val_email = autoCompleteTextViewEmail.getText().toString().trim();
                    arrayAdapter.getFilter().filter(val_email);
                    Log.d("Rajesh", val_email);
                    boolean isValidEmail = UIUtils.isValidEmail(val_email);
                    if (isValidEmail) {
                        txtErrorMessage.setVisibility(View.INVISIBLE);
                    } else {
                        txtErrorMessage.setVisibility(View.VISIBLE);
                    }
                } else {
                    txtErrorMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addAdapterToViews() {

        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        for (Account account : accounts) {
            if (UIUtils.isValidEmail(account.name)) {
                emailSet.add(account.name);
            }

        }
        autoCompleteTextViewEmail.setThreshold(0);
        ArrayList<String> list = new ArrayList<String>(emailSet);
        arrayAdapter = new EmailListAdapter(this, R.layout.email_row, list);
        autoCompleteTextViewEmail.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
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
                finish();
                break;
            case R.id.btnContinue:
                String strEmail = autoCompleteTextViewEmail.getText().toString();
                boolean isValidEmail = UIUtils.isValidEmail(strEmail);
                if (isValidEmail) {
                    PrefSiempo.getInstance(this).write(PrefSiempo.USER_SEEN_EMAIL_REQUEST, true);
                    PrefSiempo.getInstance(this).write(PrefSiempo
                            .USER_EMAILID, strEmail);
                    storeDataToFirebase(CoreApplication.getInstance().getDeviceId(), strEmail);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void askForPermission(String[] PERMISSIONS) {
        try {
            TedPermission.with(this)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            addAdapterToViews();
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                        }
                    })
                    .setPermissions(PERMISSIONS)
                    .check();
        } catch (Exception e) {
            e.printStackTrace();
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
