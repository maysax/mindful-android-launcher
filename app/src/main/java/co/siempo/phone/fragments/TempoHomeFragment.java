package co.siempo.phone.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.activities.UpdateBackgroundActivity;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.databinding.FragmentTempoHomeBinding;
import co.siempo.phone.event.NotifyBackgroundChange;
import co.siempo.phone.event.NotifyBackgroundToService;
import co.siempo.phone.event.ThemeChangeEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.service.ScreenFilterService;
import co.siempo.phone.util.AppUtils;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;

public class TempoHomeFragment extends CoreFragment {
    private PermissionUtil permissionUtil;

    public TempoHomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    protected ViewBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final FragmentTempoHomeBinding binding = FragmentTempoHomeBinding.inflate(inflater, container, false);
        // Download siempo images
        if (permissionUtil == null) {
            permissionUtil = new PermissionUtil(getActivity());
        }
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        binding.toolbar.setTitle(R.string.homescreen);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        binding.switchDisableIntentionsControls.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo
                .IS_INTENTION_ENABLE, false));
        binding.switchDisableIntentionsControls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.IS_INTENTION_ENABLE, isChecked);
                FirebaseHelper.getInstance().logIntention_IconBranding_Randomize(FirebaseHelper.INTENTIONS, isChecked ? 1 : 0);
            }
        });

        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo
                .IS_DARK_THEME, false)) {
            binding.switchDarkTheme.setChecked(true);
        } else {
            binding.switchDarkTheme.setChecked(false);
        }

        binding.switchDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.IS_DARK_THEME, isChecked);
                EventBus.getDefault().postSticky(new ThemeChangeEvent(true));
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != getActivity()) {
                            getActivity().finish();
                        }
                    }
                }, 60);
            }
        });

        binding.switchCustomBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strImage = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG, "");
                boolean isEnable = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG_ENABLE, false);
                if (isEnable && !TextUtils.isEmpty(strImage)) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG_ENABLE, false);
                    EventBus.getDefault().post(new NotifyBackgroundToService(false));
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG, "");
                    EventBus.getDefault().postSticky(new NotifyBackgroundChange(true));
                    binding.switchCustomBackground.setChecked(false);
                } else if (!isEnable && TextUtils.isEmpty(strImage)) {
                    //    startActivity(new Intent(context, ChooseBackgroundActivity.class));
                    binding.switchCustomBackground.setChecked(false);
                    checkPermissionsForBackground();

                } else if (!isEnable && !TextUtils.isEmpty(strImage)) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG_ENABLE, true);
                    EventBus.getDefault().postSticky(new NotifyBackgroundChange(true));
                    binding.switchCustomBackground.setChecked(true);
                }
            }
        });

        /*switchIconToolsVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_TOOLS_TEXT_VISIBILITY_ENABLE, false));
        switchIconToolsVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sb = (Switch) v;
                if (sb.isChecked()) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_TOOLS_TEXT_VISIBILITY_ENABLE, true);
                } else  {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_TOOLS_TEXT_VISIBILITY_ENABLE, false);
                }
            }
        });

        switchIconFavoriteVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, false));
        switchIconFavoriteVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sb = (Switch) v;
                if (sb.isChecked()) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, true);
                } else  {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, false);
                }
            }
        });

        switchIconJunkFoodVisibility.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_ICON_JUNKFOOD_TEXT_VISIBILITY_ENABLE, false));
        switchIconJunkFoodVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sb = (Switch) v;
                if (sb.isChecked()) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_JUNKFOOD_TEXT_VISIBILITY_ENABLE, true);
                } else  {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_ICON_JUNKFOOD_TEXT_VISIBILITY_ENABLE, false);
                }
            }
        });*/

        binding.relIconLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CoreActivity) requireActivity()).loadChildFragment(new IconLabelsFragment(), R.id.tempoView);
            }
        });

        final View decorView = requireActivity().getWindow().getDecorView();
        final int uiOptions = decorView.getSystemUiVisibility();
        final int[] newUiOptions = {uiOptions};

        binding.switchNotification.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, true));
        binding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, isChecked);
                AppUtils.notificationBarManaged(getActivity(), null);
            }
        });

        binding.switchScreenOverlay.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_SCREEN_OVERLAY, false));
        binding.switchScreenOverlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkPermissionsForSystemWindow();
                } else {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, false);
                    final Intent command = new Intent(getActivity(), ScreenFilterService.class);
                    command.putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 1);
                    requireActivity().startService(command);
                }
            }
        });

        binding.relAllowSpecificApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.switchDisableIntentionsControls.performClick();
            }
        });

        binding.relCustomBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsForBackground();
            }
        });

        binding.relDarkTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.switchDarkTheme.performClick();
            }
        });

        return binding;
    }

    private void checkPermissionsForSystemWindow() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasGiven
                (PermissionUtil.SYSTEM_WINDOW_ALERT))) {
            try {
                TedPermission.with(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, true);
                                Intent command = new Intent(getActivity(), ScreenFilterService.class);
                                command.putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 0);
                                getActivity().startService(command);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                            }
                        })
                        .setDeniedMessage(R.string.msg_permission_denied)
                        .setPermissions(new String[]{
                                Manifest.permission.SYSTEM_ALERT_WINDOW})
                        .check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, true);
            Intent command = new Intent(getActivity(), ScreenFilterService.class);
            command.putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 0);
            getActivity().startService(command);
        }
    }


    private void notificationVisibility() {

        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, true)) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setFitsSystemWindows(false);
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setFitsSystemWindows(true);
        }
    }

    private void checkPermissionsForBackground() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasGiven
                (PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION))) {
            try {
                TedPermission.with(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                CoreApplication.getInstance().downloadSiempoImages();
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, 10);
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
                                        .READ_EXTERNAL_STORAGE})
                        .check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CoreApplication.getInstance().downloadSiempoImages();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 10);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final FragmentTempoHomeBinding binding = requireViewBinding();
        final String strImage = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG, "");
        final boolean isEnable = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG_ENABLE, false);
        final boolean isPermission = permissionUtil.hasGiven(PermissionUtil
                .WRITE_EXTERNAL_STORAGE_PERMISSION);
        if (isEnable && !TextUtils.isEmpty(strImage) && isPermission) {
            binding.switchCustomBackground.setChecked(true);
        } else if (!isEnable && TextUtils.isEmpty(strImage) && !isPermission) {
            binding.switchCustomBackground.setChecked(false);
            PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG_ENABLE, false);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Intention screen Wallpaper selection
        if (requestCode == 10 || requestCode == 7) {
            switch (requestCode) {
                case 10:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri uri = data.getData();

                        if (uri != null && !TextUtils.isEmpty(uri.toString())) {

                            if (uri.toString().contains("com.google.android.apps.photos.contentprovider")) {
                                return;
                            }

                            if (uri.toString().contains("/storage")) {
                                String[] storagepath = uri.toString().split("/storage");
                                if (storagepath.length > 1) {
                                    String filePath = "/storage" + storagepath[1];
                                    Intent mUpdateBackgroundIntent = new Intent(getActivity(), UpdateBackgroundActivity.class);
                                    mUpdateBackgroundIntent.putExtra("imageUri", filePath);
                                    startActivityForResult(mUpdateBackgroundIntent, 3);
                                }
                            } else {
                                String id = DocumentsContract.getDocumentId(uri);

                                if (!TextUtils.isEmpty(id) && uri != null) {

                                    try {
                                        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                                        File file = new File(getActivity().getCacheDir().getAbsolutePath() + "/" + id);
                                        writeFile(inputStream, file);
                                        String filePath = file.getAbsolutePath();

                                        if (filePath.contains("raw:")) {
                                            String[] downloadPath = filePath.split("raw:");
                                            if (downloadPath.length > 1) {
                                                filePath = downloadPath[1];
                                            }
                                        }

                                        Intent mUpdateBackgroundIntent = new Intent(getActivity(), UpdateBackgroundActivity.class);
                                        mUpdateBackgroundIntent.putExtra("imageUri", filePath);
                                        startActivityForResult(mUpdateBackgroundIntent, 3);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                    }
                    break;
                case 7:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Intent mUpdateBackgroundIntent = new Intent(getActivity(),
                                UpdateBackgroundActivity
                                        .class);
                        mUpdateBackgroundIntent.putExtra("imageUri", picturePath);
                        startActivityForResult(mUpdateBackgroundIntent, 3);
                    }
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
