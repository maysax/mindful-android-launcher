package co.siempo.phone.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.databinding.FragmentTempoUpdateEmailBinding;
import co.siempo.phone.models.UserModel;
import co.siempo.phone.service.MailChimpOperation;
import co.siempo.phone.service.StatusBarService;
import co.siempo.phone.utils.PrefSiempo;

public class TempoUpdateEmailFragment extends CoreFragment {
    private ConnectivityManager connectivityManager;

    public TempoUpdateEmailFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentTempoUpdateEmailBinding binding = FragmentTempoUpdateEmailBinding.inflate(inflater, container, false);
        binding.edtEmail.setText("");
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(binding.edtEmail, 0);

        binding.toolbar.inflateMenu(R.menu.update_email_menu);
        binding.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.tick) {
                    String val_email = binding.edtEmail.getText().toString().trim();
                    if (!PrefSiempo.getInstance(requireContext()).read(PrefSiempo
                            .USER_EMAILID, "").equals(val_email)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.success_email), Toast.LENGTH_SHORT).show();
                    }
                    try {
                        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context
                                .CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                        if (activeNetwork != null) {
                            new MailChimpOperation(MailChimpOperation.EmailType.EMAIL_REG).execute(val_email);
                            if (PrefSiempo.getInstance(requireContext()).read(PrefSiempo
                                    .USER_EMAILID, "").equalsIgnoreCase("")) {
                                storeDataToFirebase(true, CoreApplication.getInstance().getDeviceId(), val_email);
                            } else {
                                storeDataToFirebase(false, CoreApplication.getInstance().getDeviceId(), val_email);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PrefSiempo.getInstance(requireContext()).write(PrefSiempo.USER_EMAILID, val_email);
                    hideSoftKeyboard();
                    requireFragmentManager().popBackStack();
                    return true;
                }
                return TempoUpdateEmailFragment.super.onOptionsItemSelected(menuItem);
            }
        });
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        binding.toolbar.setTitle(R.string.string_update_email_title);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                getFragmentManager().popBackStack();
            }
        });
        try {
            Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/robotocondensedregular.ttf");
            binding.edtEmail.setTypeface(myTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(PrefSiempo.getInstance(requireContext()).read(PrefSiempo.USER_EMAILID, ""))) {
            binding.edtEmail.setText(PrefSiempo.getInstance(requireContext()).read(PrefSiempo.USER_EMAILID, ""));
            binding.edtEmail.setSelection(binding.edtEmail.getText().length());
        }

        final String strEmailAddress = binding.edtEmail.getText().toString().trim();

        if (strEmailAddress.length() > 0) {
            if (isValidEmail(strEmailAddress)) {
                if (strEmailAddress.equalsIgnoreCase(PrefSiempo.getInstance(requireContext()).read(PrefSiempo.USER_EMAILID, ""))) {
                    binding.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                } else {
                    binding.toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                }
                binding.edtEmail.setTextColor(getResources().getColor(R.color.black));
                binding.textInputLayout.setErrorEnabled(false);

            } else {
                binding.textInputLayout.setError(getResources().getString(R.string.feedback_email));
                binding.textInputLayout.setErrorEnabled(true);
            }
        }

        binding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final FragmentTempoUpdateEmailBinding binding1 = requireViewBinding();
                String strEmailAddressEntered = binding1.edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmailAddressEntered)) {
                    final boolean isValidEmail = isValidEmail(strEmailAddressEntered);
                    binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                    if (isValidEmail) {
                        binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                        binding1.textInputLayout.setErrorEnabled(false);
                        if (strEmailAddressEntered.equalsIgnoreCase(PrefSiempo
                                .getInstance(requireContext())
                                .read(PrefSiempo.USER_EMAILID, ""))) {
                            binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(false);
                        } else {
                            binding1.toolbar.getMenu().findItem(R.id.tick).setVisible(true);
                        }
                    } else {
                        binding1.textInputLayout.setError(getResources().getString(R.string.error_email));
                        binding1.textInputLayout.setErrorEnabled(true);
                    }
                } else {
                    binding1.textInputLayout.setErrorEnabled(false);
                }
            }
        });
        return binding;
    }

    private void storeDataToFirebase(boolean isNew, String userId, String emailId) {
        try {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
            UserModel user = new UserModel(userId, emailId, StatusBarService.latitude, StatusBarService.longitude);
            String key = mDatabase.child(userId).getKey();
            if (key != null) {
                final Map<String, Object> map = new HashMap<>();
                map.put("emailId", emailId);
                map.put("userId", userId);
                map.put("latitude", StatusBarService.latitude);
                map.put("longitude", StatusBarService.longitude);
                mDatabase.child(userId).updateChildren(map);
            } else {
                mDatabase.child(userId).setValue(user);
                mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("Firebase", dataSnapshot.getKey() + "  " + dataSnapshot.getValue(UserModel.class)
                                .toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("Firebase RealTime", "Failed to read value.", error.toException());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
