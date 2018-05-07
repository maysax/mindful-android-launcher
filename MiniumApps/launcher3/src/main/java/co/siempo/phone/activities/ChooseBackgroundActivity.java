package co.siempo.phone.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import co.siempo.phone.R;
import co.siempo.phone.adapters.BackgroundItemAdapter;
import co.siempo.phone.models.ImageItem;
import co.siempo.phone.utils.PrefSiempo;

public class ChooseBackgroundActivity extends CoreActivity {
    ArrayList<ImageItem> internallist;
    File folderSiempoImage;
    boolean openSubFolder = false;
    private Toolbar toolbar;
    private GridView mImageGridview;
    private ArrayList<ImageItem> mItems;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_background);

        initView();

        createSiempoFolder();

        bindGridView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.choose_background);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mImageGridview = (GridView) findViewById(R.id.ImageGridview);

    }

    private void createSiempoFolder() {
        try {
            folderSiempoImage = new File(Environment.getExternalStorageDirectory() +
                    "/Siempo images");
            boolean success = true;
            if (!folderSiempoImage.exists()) {
                success = folderSiempoImage.mkdirs();
            }
            if (success) {

            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindGridView() {
        mItems = new ArrayList<>();
        final String strDefault = PrefSiempo.getInstance(ChooseBackgroundActivity.this).read(PrefSiempo
                .DEFAULT_BAG, "");
        new AsyncTask<String, String, ArrayList<ImageItem>>() {
            ArrayList<ImageItem> local;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                local = new ArrayList<>();
            }

            @Override
            protected ArrayList<ImageItem> doInBackground(String... strings) {
                if (!strDefault.equalsIgnoreCase("")) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(strDefault);
                    local.add(new ImageItem("Current background", list, true));
                }

                if (getAllImagePaths() != null && getAllImagePaths().size() > 0) {
                    local.add(new ImageItem("My images", getAllImagePaths(), true));
                }

                if (folderSiempoImage.exists()) {
                    folderSiempoImage.list();
                    ArrayList<String> list = new ArrayList<>();
                    for (File imagePath : folderSiempoImage.listFiles()) {
                        if (imagePath.toString().endsWith(".jpg") || imagePath.toString().endsWith(".JPG")
                                || imagePath.toString().endsWith(".jpeg") || imagePath.toString().endsWith(".JPEG")
                                || imagePath.toString().endsWith(".png") || imagePath.toString().endsWith(".PNG")
                                ) {
                            list.add(imagePath.toString());
                        }
                    }
                    if (list != null && list.size() > 0) {
                        String first_image = list.get(0);
                        local.add(new ImageItem("Siempo images", list, true));
                    }

                }
                return local;
            }

            @Override
            protected void onPostExecute(ArrayList<ImageItem> local) {
                super.onPostExecute(local);
                mItems = local;
                mImageGridview.setAdapter(new BackgroundItemAdapter(ChooseBackgroundActivity
                        .this, mItems));
            }
        }.execute();


        mImageGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                String mItemText = item.name;
                if (mItemText == "Current background") {
                    finish();
                } else if (mItemText == "My images") {
                    openSubFolder = true;
                    internallist = new ArrayList<>();
                    for (String imageItem : mItems.get(position).getDrawableId()) {
                        ArrayList<String> stringArrayList = new ArrayList<>();
                        stringArrayList.add(imageItem);
                        ImageItem imageItem1 = new ImageItem("", stringArrayList, true);
                        internallist.add(imageItem1);
                    }
                    toolbar.setTitle("My images");
                    mImageGridview.setAdapter(new BackgroundItemAdapter(ChooseBackgroundActivity
                            .this, internallist));
//                    Intent intent=new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                } else if (mItemText == "Siempo images") {
//                    Uri selectedUri = Uri.parse(folderSiempoImage.toString()+"/");
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setDataAndType(selectedUri, "image/*");
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    openSubFolder = true;
                    internallist = new ArrayList<>();
                    for (String imageItem : mItems.get(position).getDrawableId()) {
                        ArrayList<String> stringArrayList = new ArrayList<>();
                        stringArrayList.add(imageItem);
                        ImageItem imageItem1 = new ImageItem("", stringArrayList, true);
                        internallist.add(imageItem1);

                    }
                    toolbar.setTitle("Siempo images");
                    mImageGridview.setAdapter(new BackgroundItemAdapter(ChooseBackgroundActivity
                            .this, internallist));
                } else {
                    Intent mUpdateBackgroundIntent = new Intent(ChooseBackgroundActivity.this,
                            UpdateBackgroundActivity
                                    .class);
                    mUpdateBackgroundIntent.putExtra("imageUri", internallist.get(position)
                            .getDrawableId().get(0).toString());
                    startActivityForResult(mUpdateBackgroundIntent, PICK_IMAGE_REQUEST);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (openSubFolder) {
            openSubFolder = false;
            toolbar.setTitle(R.string.choose_background);
            mImageGridview.setAdapter(new BackgroundItemAdapter(this, mItems));
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
//            Uri imageUri = data.getData();
//            Intent mUpdateBackgroundIntent=new Intent(this,UpdateBackgroundActivity.class);
//            mUpdateBackgroundIntent.putExtra("imageUri", imageUri.toString());
//            startActivity(mUpdateBackgroundIntent);
            openSubFolder = false;
            toolbar.setTitle(R.string.choose_background);
            bindGridView();
        } else {

        }
    }


    public ArrayList<String> getAllImagePaths() {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = null;
        final SortedSet<String> dirList = new TreeSet<String>();
        final ArrayList<String> resultIAV = new ArrayList<String>();

        String[] directories = null;
        if (imageUri != null) {
            cursor = getContentResolver().query(imageUri, projection, null, null, null);
        }

        if ((cursor != null) && (cursor.moveToFirst())) {
            do {
                String tempDir = cursor.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (cursor.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);
        }
        final String[] finalDirectories = directories;
        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(finalDirectories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                if (!imagePath.toString().endsWith("Siempo images")) {
                    try {
                        if (imagePath.isDirectory()) {
                            //imageList = imagePath.listFiles();
                        }
                        if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                                || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                                || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                                ) {
                            String path = imagePath.getAbsolutePath();
                            resultIAV.add(path);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return resultIAV;
    }
}

