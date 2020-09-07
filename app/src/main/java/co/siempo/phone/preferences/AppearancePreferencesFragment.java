package co.siempo.phone.preferences;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.Keep;

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
import co.siempo.phone.activities.UpdateBackgroundActivity;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyBackgroundChange;
import co.siempo.phone.event.NotifyBackgroundToService;
import co.siempo.phone.event.ThemeChangeEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.service.ScreenFilterService;
import co.siempo.phone.util.AppUtils;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;

@Keep
public class AppearancePreferencesFragment extends BasePreferenceFragment {
    private static final int REQUEST_CODE_SELECT_IMAGE = 10;

    private PermissionUtil permissionUtil;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        permissionUtil = new PermissionUtil(requireContext());
        addPreferencesFromResource(R.xml.preferences_appearance);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key == null) {
            return;
        }
        switch (key) {
            case Preferences.KEY_DARK_THEME: {
                EventBus.getDefault().postSticky(new ThemeChangeEvent(true));
                new Handler().postDelayed(() -> {
                    if (getActivity() != null) getActivity().finish();
                }, 60);
                break;
            }
            case Preferences.KEY_INTENTIONS_DISABLED: {
                FirebaseHelper.getInstance().logIntention_IconBranding_Randomize(FirebaseHelper.INTENTIONS, preferences.getBoolean(key, false) ? 1 : 0);
                break;
            }
            case Preferences.KEY_CUSTOM_BACKGROUND_ENABLED: {
                final String strImage = preferences.getString(Preferences.KEY_CUSTOM_BACKGROUND, "");
                final boolean isEnable = preferences.getBoolean(Preferences.KEY_CUSTOM_BACKGROUND_ENABLED, false);
                if (!isEnable && !TextUtils.isEmpty(strImage)) {
                    preferences.edit().putString(Preferences.KEY_CUSTOM_BACKGROUND, "").apply();
                    EventBus.getDefault().post(new NotifyBackgroundToService(false));
                    EventBus.getDefault().postSticky(new NotifyBackgroundChange(true));
                } else if (isEnable && TextUtils.isEmpty(strImage)) {
                    //startActivity(new Intent(requireContext(), ChooseBackgroundActivity.class));
                    checkPermissionsForBackground();

                } else if (isEnable && !TextUtils.isEmpty(strImage)) {
                    EventBus.getDefault().postSticky(new NotifyBackgroundChange(true));
                }
                break;
            }
            case Preferences.KEY_STATUS_BAR_HIDDEN: {
                AppUtils.notificationBarManaged(getActivity(), null);
                break;
            }
            case Preferences.KEY_SCREEN_OVERLAY_ENABLED: {
                if (preferences.getBoolean(Preferences.KEY_SCREEN_OVERLAY_ENABLED, false)) {
                    checkPermissionsForSystemWindow();
                } else {
                    final Intent command = new Intent(getActivity(), ScreenFilterService.class)
                            .putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 1);
                    requireContext().startService(command);
                }
                break;
            }
        }
    }

    private void checkPermissionsForBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)) {
            try {
                TedPermission.with(requireContext())
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
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        })
                        .check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            CoreApplication.getInstance().downloadSiempoImages();
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), REQUEST_CODE_SELECT_IMAGE);
        }
    }

    private void checkPermissionsForSystemWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasGiven(PermissionUtil.SYSTEM_WINDOW_ALERT)) {
            try {
                TedPermission.with(requireContext())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, true);
                                final Intent command = new Intent(getActivity(), ScreenFilterService.class)
                                        .putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 0);
                                requireContext().startService(command);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, false);
                                final Intent command = new Intent(getActivity(), ScreenFilterService.class)
                                        .putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 1);
                                requireContext().startService(command);
                            }
                        })
                        .setDeniedMessage(R.string.msg_permission_denied)
                        .setPermissions(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW})
                        .check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, true);
            final Intent command = new Intent(getActivity(), ScreenFilterService.class)
                    .putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 0);
            requireContext().startService(command);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Intention screen Wallpaper selection
        if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
            // TODO: 24.06.2020 replace with System wallpaper intent (that still launches our own picker though)
            if (resultCode == Activity.RESULT_OK) {
                final Uri uri = data.getData();

                if (uri != null && !TextUtils.isEmpty(uri.toString())) {

                    if (uri.toString().contains("com.google.android.apps.photos.contentprovider")) {
                        return;
                    }

                    if (uri.toString().contains("/storage")) {
                        final String[] storagepath = uri.toString().split("/storage");
                        if (storagepath.length > 1) {
                            final String filePath = "/storage" + storagepath[1];
                            final Intent mUpdateBackgroundIntent = new Intent(getActivity(), UpdateBackgroundActivity.class)
                                    .putExtra("imageUri", filePath);
                            startActivityForResult(mUpdateBackgroundIntent, 3);
                        }
                    } else {
                        final String id = DocumentsContract.getDocumentId(uri);

                        if (!TextUtils.isEmpty(id)) {
                            try {
                                final InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                                final File file = new File(getActivity().getCacheDir().getAbsolutePath(), id);
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
        } else if (requestCode == 7) {
            if (resultCode == Activity.RESULT_OK) {
                final Uri selectedImage = data.getData();
                final String[] filePathColumn = {MediaStore.Images.Media.DATA};

                final Cursor cursor = requireContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                final String picturePath = cursor.getString(columnIndex);
                cursor.close();
                final Intent mUpdateBackgroundIntent = new Intent(getActivity(),
                        UpdateBackgroundActivity.class)
                        .putExtra("imageUri", picturePath);
                startActivityForResult(mUpdateBackgroundIntent, 3);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static void writeFile(InputStream in, File file) {
        try (OutputStream out = new FileOutputStream(file)) {
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
