package com.trafalgartmc.guinep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.trafalgartmc.guinep.Adapters.PhotoStreamAdapter;
import com.trafalgartmc.guinep.Classes.GalleryDataParser;
import com.trafalgartmc.guinep.Classes.SnackbarMsg;
import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.AlertBox;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Base64.encodeToString;

public class PhotoStreamActivity extends MainBaseActivity {
    private static final int REQUEST_PICK_PHOTO = 1000;
    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA  = 2000;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2001;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE_ONLY = 2002;
    private FloatingActionButton fab;
    private FloatingActionButton fab_photo;
    private FloatingActionButton fab_gallery;
    private Animation FabOpen;
    private Animation FabClose;
    private Animation FabClockWise;
    private Animation FabAntClockWise;
    private boolean isOpen;
    private String mCurrentPhotoPath;
    private PhotoStreamFragment photoStreamFragment;
    private SwipeRefreshLayout mSwipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoStreamFragment = PhotoStreamFragment.newInstance();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, photoStreamFragment)
                    .commit();
        }

        ImageView banner = (ImageView) findViewById(R.id.banner);
        banner.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.gallery));

        fab             = (FloatingActionButton) findViewById(R.id.fab);
        fab_photo       = (FloatingActionButton) findViewById(R.id.fab_photo);
        fab_gallery     = (FloatingActionButton) findViewById(R.id.fab_gallery);

        FabOpen         = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        FabClose        = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        FabClockWise    = AnimationUtils.loadAnimation(this, R.anim.rot_clock_wise);
        FabAntClockWise = AnimationUtils.loadAnimation(this, R.anim.rot_ant_clock_wise);

        final SnackbarMsg snackbarMsg = new SnackbarMsg();
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                    snackbarMsg.show(findViewById(R.id.container), getBaseContext(), getString(R.string.sign_in_photo), SnackbarMsg.Type.NORMAL);
                }else{
                    if(isOpen){
                        closeMenu();
                    }else{
                        openMenu();
                    }
                }
            }
        });

        fab_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        fab_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGallery();
            }
        });

        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipe.setProgressViewOffset(false, 0,getResources().getDimensionPixelSize(R.dimen.refresher_offset_end));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                photoStreamFragment.getPhotoGallery(mSwipe);
            }
        });
    }

    private void loadGallery() {
        if(ContextCompat.checkSelfPermission(mBaseActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)), REQUEST_PICK_PHOTO);
        }else{
            //if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mBaseActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){}
            ActivityCompat.requestPermissions((Activity) mBaseActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE_ONLY);
        }
    }

    private void launchCamera() {
        if(ContextCompat.checkSelfPermission(mBaseActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
        {
            if( ContextCompat.checkSelfPermission(mBaseActivity,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }else{
                // Should we show an explanation?
                //if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mBaseActivity,Manifest.permission.CAMERA)) {}
                ActivityCompat.requestPermissions((Activity) mBaseActivity,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }else{
            //if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mBaseActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){}
            ActivityCompat.requestPermissions((Activity) mBaseActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
    }

    private void closeMenu(){
        fab_photo.startAnimation(FabClose);
        fab_gallery.startAnimation(FabClose);
        fab.startAnimation(FabAntClockWise);
        fab_photo.setClickable(false);
        fab_gallery.setClickable(false);
        isOpen = false;
    }

    private void openMenu(){
        fab_photo.startAnimation(FabOpen);
        fab_gallery.startAnimation(FabOpen);
        fab.startAnimation(FabClockWise);
        fab_photo.setClickable(true);
        fab_gallery.setClickable(true);
        isOpen = true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = getResources().getString(R.string.app_name) + "_" + Common.timeStamp() + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(mBaseActivity,
                            getResources().getString(R.string.file_provider),
                            photoFile);
                    //Add permission to intent resolver
                    List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        this.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    //Save the image
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }catch (IllegalArgumentException e){
                    Toast.makeText(mBaseActivity, R.string.camera_failed, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void reSizeImage(String imagePath) {
        float fraction;
        //Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int imgWidth = bmOptions.outWidth;
        int imgHeight = bmOptions.outHeight;
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        if(imgWidth > imgHeight){
            if(imgWidth>800){
                fraction = 800f/imgWidth;
                int newHeight = (int) (imgHeight*fraction);
                bitmap = Bitmap.createScaledBitmap(bitmap,800,newHeight,false);
            }
        }else{
            if(imgHeight>800){
                fraction = 800f/imgHeight;
                int newWidth = (int) (imgWidth*fraction);
                bitmap = Bitmap.createScaledBitmap(bitmap,newWidth, 800,false);
            }
        }

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        String encodedImage = encodeToString(byteArray.toByteArray(), Base64.NO_WRAP);

        if(encodedImage!=null){
            int userId = Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0);
            if(userId==0) {
                AlertBox.Show(mBaseActivity,getString(R.string.network_error),AlertBox.Type.ERROR);
            }else{
                uploadImage(encodedImage, userId);
            }
        }
    }

    private void uploadImage(String img, int userId) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("image", img)
                .add("title", "#trafalgar")
                .add("user_id", String.valueOf(userId))
                .build();

        final Request request = new Request.Builder()
                .url(Common.API_SERVER + "upload_image.php")
                .post(requestBody)
                .build();

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Common.showLoading(PhotoStreamActivity.this, getString(R.string.uploading));
            }

            @Override
            protected String doInBackground(Void... params) {
                String body = "";
                try {
                    Response response = client.newCall(request).execute();
                    body = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(!body.equals("_SYS_ERROR_")) {
                    //Clear gallery list and Write new json string
                    GalleryDataParser.clearGalleryData();
                    try {
                        FileOutputStream file = mBaseContext.openFileOutput(Common.GALLERY_FILE, MODE_PRIVATE);
                        file.write(body.getBytes());
                        file.close();
                    } catch (IOException e) {
                        Log.e(Common.LOG_TAG, e.getMessage());
                    }
                }

                return body;
            }

            @Override
            protected void onPostExecute(String body) {
                super.onPostExecute(body);
                switch (body){
                    case "_SYS_ERROR_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.network_error),AlertBox.Type.ERROR);
                    break;
                    case "_UPLOAD_ERROR_" :
                        AlertBox.Show(mBaseActivity,getString(R.string.image_upload_error),AlertBox.Type.ERROR);
                    break;
                    default:
                        photoStreamFragment.getPhotoGallery(mSwipe);
                    break;
                }
                Common.closeDialog(PhotoStreamActivity.this);
            }
        };
        task.execute();

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if(response.body().string().equals("1")){
                        photoStreamFragment.getPhotoGallery(mSwipe, mBaseContext);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    /*private void setPic() {
         Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
            break;
            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                }
            break;
            case MY_PERMISSIONS_REQUEST_STORAGE_ONLY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadGallery();
                }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = null;
        closeMenu();
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            path = mCurrentPhotoPath;
            galleryAddPic();
        }else if(requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK){
            path = getAbsolutePath(data.getData());
        }
        if(path!=null){
            reSizeImage(path);
        }
    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA };
        CursorLoader loader =  new CursorLoader(mBaseContext, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_special) {
            if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                AlertBox.Show(mBaseActivity,getString(R.string.access_denied),AlertBox.Type.ERROR);
            }else {
                Intent intent = new Intent(this, SpecialsActivity.class);
                startActivity(intent);
                return true;
            }
        }
        if (id == R.id.action_chat) {
            if(Common.getSession(mBaseContext).getInt(getString(R.string.SESSION_ID),0)==0){
                AlertBox.Show(mBaseActivity,getString(R.string.access_denied),AlertBox.Type.ERROR);
            }else {
                Intent intent = new Intent(this, ChatConnectActivity.class);
                startActivity(intent);
                return true;
            }
        }
        if (id == R.id.action_photo_stream) {
            return true;
        }
        if (id == R.id.action_refresh) {
            photoStreamFragment.getPhotoGallery(mSwipe);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            Common.about(mBaseActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment
     * */
    public static class PhotoStreamFragment extends Fragment {
        private PhotoStreamAdapter mPhotoStreamAdapter;
        private Activity mActivity;
        private Context mContext;

        public PhotoStreamFragment() {}

        public static PhotoStreamFragment newInstance() {
            return new PhotoStreamFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View viewRoot = inflater.inflate(R.layout.recycler_view, container, false);

            RecyclerView recyclerView = viewRoot.findViewById(R.id.recycler_view);
            int spanCount = (this.getResources().getConfiguration().orientation == 1)? 3 : 5;

            mPhotoStreamAdapter = new PhotoStreamAdapter(getActivity());

            recyclerView.setAdapter(mPhotoStreamAdapter);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL) );
            return viewRoot;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getPhotoGallery(null);
        }

        public void getPhotoGallery(final SwipeRefreshLayout swipe) {
            if(swipe==null){
                Common.showLoading(mActivity, getResources().getString(R.string.loading));
            }else {
                swipe.setRefreshing(true);
            }
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    GalleryDataParser.clearGalleryData();
                    Common.saveDownloadedDataFile(  mContext,
                                                    Common.GALLERY_API,
                                                    Common.GALLERY_FILE, false, swipe!=null);
                    return null;
                }

                @Override
                protected void onPostExecute(Void o) {
                    super.onPostExecute(o);
                    mPhotoStreamAdapter.loadData(mContext);
                    mPhotoStreamAdapter.notifyDataSetChanged();
                    if(swipe==null){
                        Common.closeDialog(mActivity);
                    }else {
                        swipe.setRefreshing(false);
                    }
                }
            };
            if(Utility.isConnected(mContext)) {
                task.execute();
            }
            /*swipe.setRefreshing(true);

            if(mPhotoStreamAdapter==null){
                mPhotoStreamAdapter = new PhotoStreamAdapter(c);
            }

            mPhotoStreamAdapter.loadData(c);
            mPhotoStreamAdapter.notifyDataSetChanged();
            swipe.setRefreshing(false);*/
        }
    }
}
