package com.trafalgartmc.guinep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.trafalgartmc.guinep.Adapters.SpinnerAdapter;
import com.trafalgartmc.guinep.Classes.SelectableItem;
import com.trafalgartmc.guinep.Settings.SettingsActivity;
import com.trafalgartmc.guinep.Utility.AlertBox;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Rohan MOrris
 * on 6/20/2017.
 */

public class UpdateProfileActivity extends AppCompatActivity {
    private static String mGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Utility.hasLolliPop()){
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.share_ele_transition));
        }

        setContentView(R.layout.default_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, UpdateProfileFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mini_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Common.about(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void genderSelect(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.female:
                if(checked) mGender = "F";
                break;
            case R.id.male:
                if(checked) mGender = "M";
                break;
        }
    }
    /**
     * Fragment
     * */
    public static class UpdateProfileFragment extends Fragment {
        private static final int REQUEST_CODE = 1200;
        private static final int MY_PERMISSIONS_REQUEST_CAMERA  = 2100;
        private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2101;
        private Activity mActivity;
        private Context mContext;
        List<SelectableItem> jobList;
        private Button dob;
        private RadioButton male, female;
        private Spinner occupation;
        private TextView    first_name,
                            middle_name,
                            last_name,
                            email,
                            mobile,
                            telephone1,
                            telephone2,
                            address1,
                            address2,
                            address3;
        private static String mDateVal;

        public UpdateProfileFragment(){}

        public static UpdateProfileFragment newInstance()
        {
            return new UpdateProfileFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            mContext = mActivity.getApplicationContext();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_update_profile, container, false);

            Button btn = rootView.findViewById(R.id.btn);

            final ImageView add_photo = rootView.findViewById(R.id.add_photo);
            CircleImageView profile  = rootView.findViewById(R.id.profile);
            first_name  = rootView.findViewById(R.id.first_name);
            middle_name = rootView.findViewById(R.id.middle_name);
            last_name   = rootView.findViewById(R.id.last_name);
            dob         = rootView.findViewById(R.id.dob);
            email       = rootView.findViewById(R.id.email);
            mobile      = rootView.findViewById(R.id.mobile);
            male        = rootView.findViewById(R.id.male);
            female      = rootView.findViewById(R.id.female);
            telephone1  = rootView.findViewById(R.id.telephone1);
            telephone2  = rootView.findViewById(R.id.telephone2);
            address1    = rootView.findViewById(R.id.address1);
            address2    = rootView.findViewById(R.id.address2);
            address3    = rootView.findViewById(R.id.address3);
            occupation  = rootView.findViewById(R.id.occupation_key);

            Common.setProfilePhoto(mContext, profile);

            add_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
                    {
                        if( ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                        {
                            startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE);
                        }else{
                            // Should we show an explanation?
                            //if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),android.Manifest.permission.CAMERA)) {}
                            requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    }else{
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_STORAGE);
                    }
                }
            });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean valid =  true;
                    StringBuilder str = new StringBuilder();

                    if(first_name.getText().toString().trim().isEmpty()){
                        str.append("Your first name is required\n");
                        valid =  false;
                    }

                    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
                        str.append("Your email address is required\n");
                        valid =  false;
                    }

                    if(dob.getText().toString().trim().isEmpty()){
                        str.append("Your date of birth is required\n");
                        valid =  false;
                    }

                    if(mobile.getText().toString().trim().isEmpty()){
                        str.append("Your mobile number is required\n");
                        valid =  false;
                    }

                    if(occupation.getSelectedItemPosition() == 0){
                        str.append("Please select your occupation type\n");
                        valid =  false;
                    }

                    if(valid){
                        updateProfile();
                    }else {
                        AlertBox.Show(mActivity,str.toString(),AlertBox.Type.ERROR);
                    }
                }
            });


            dob.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    return true;
                }
            });

            dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                    View v = View.inflate(mActivity, R.layout.calendar_dialog, null);
                    final DatePicker datePicker = v.findViewById(R.id.date_picker);

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.YEAR,-15);
                    datePicker.setMaxDate(c.getTimeInMillis());
                    c.add(Calendar.YEAR,-80);
                    datePicker.setMinDate(c.getTimeInMillis());

                    try {
                        SimpleDateFormat inDate = new SimpleDateFormat("MMM dd, yyyy.", Locale.US);
                        Date calDate = inDate.parse(dob.getText().toString());

                        DateFormat y = new SimpleDateFormat("yyyy", Locale.US);
                        DateFormat m = new SimpleDateFormat("mm", Locale.US);
                        DateFormat d = new SimpleDateFormat("dd", Locale.US);

                        datePicker.updateDate(  Integer.parseInt(y.format(calDate)),
                                                Integer.parseInt(m.format(calDate)),
                                                Integer.parseInt(d.format(calDate)));
                    } catch (ParseException e) {
                        Log.e(Common.LOG_TAG, e.getMessage());
                    }

                    dialog.setView(v)
                            .setTitle("Select Your Birth Day")
                            .setCancelable(false)
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface d, int i) {
                                    d.dismiss();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface d, int i) {
                                    dob.setText(getFormattedDate(datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth()));
                                }
                            })
                            .show();
                }
            });

            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            loadData();
        }

        private String getFormattedDate(String inDate) {
            DateFormat dt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            mDateVal = inDate;
            try {
                Date date = dt.parse(inDate);
                SimpleDateFormat newDate = new SimpleDateFormat("MMM dd, yyyy.", Locale.US);
                return newDate.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void loadData() {
            final AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                OkHttpClient client = new OkHttpClient();
                Request request;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //Common.showLoading(mActivity, getResources().getString(R.string.loading));
                }

                @Override
                protected String doInBackground(Void... params) {
                    int id = Common.getSession(mContext).getInt(getString(R.string.SESSION_ID),0);
                    RequestBody requestBody = new FormBody.Builder()
                            .add("user_id", String.valueOf(id))
                            .build();

                    request = new Request.Builder()
                            .url(Common.API_SERVER + "mobile_profile.php")
                            .post(requestBody)
                            .build();
                    try {
                        return client.newCall(request).execute().body().string();
                    } catch (IOException e) {
                        Log.e(Common.LOG_TAG,e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String data) {
                    super.onPostExecute(data);
                    jobList = new ArrayList<>();
                    if(data!=null) {
                        try {
                            JSONObject obj  = new JSONObject(data);
                            first_name.setText(obj.getString("firstname"));
                            middle_name.setText(obj.getString("middlename"));
                            last_name.setText(obj.getString("lastname"));
                            email.setText(obj.getString("email"));
                            dob.setText(getFormattedDate(obj.getString("dob")));
                            mobile.setText(obj.getString("cell"));
                            telephone1.setText(obj.getString("telephone1"));
                            telephone2.setText(obj.getString("telephone2"));
                            address1.setText(obj.getString("address1"));
                            address2.setText(obj.getString("address2"));
                            address3.setText(obj.getString("address3"));

                            /* Load occupation drop down List */
                            JSONArray list = new JSONObject(data).getJSONArray("occ");
                            int occupationSelectedIndex = 0;
                            jobList.add(new SelectableItem(
                                    0, mActivity.getString(R.string.select_your_occupation)));
                            for(int i=0; i<list.length(); i++){
                                jobList.add(new SelectableItem(
                                                list.getJSONObject(i).getInt("occupation_key"),
                                                list.getJSONObject(i).getString("occupation_name")
                                ));
                                if(obj.getInt("occupation_key")==list.getJSONObject(i).getInt("occupation_key")){
                                    occupationSelectedIndex = i+1;
                                }
                            }

                            SpinnerAdapter occupationAdapter = new SpinnerAdapter(mContext);
                            occupationAdapter.loadData(jobList);
                            occupation.setAdapter(occupationAdapter);
                            occupation.setSelection(occupationSelectedIndex);
                            /* End Load occupation drop down List */
                            /*Gender will return F even if empty*/
                            if(obj.getString("gender").equals("F")){
                                female.setChecked(true);
                            }else{
                                male.setChecked(true);
                            }
                            mGender = (obj.getString("gender").equals("F"))? "F" : "M";
                        } catch (JSONException e) {
                            Log.e(Common.LOG_TAG,e.getMessage());
                        }
                    }
                    //Common.closeDialog(mActivity);
                    Log.e(Common.LOG_TAG,"INFO LOADED");
                }
            };

            if(Utility.isConnected(mContext)) {
                task.execute();
            }
        }

        private void updateProfile(){
            final AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody;
                Request request;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Common.showLoading(mActivity, getResources().getString(R.string.saving));
                    int id = Common.getSession(mContext).getInt(getString(R.string.SESSION_ID),0);
                    requestBody = new FormBody.Builder()
                            .add("user_id",     String.valueOf(id))
                            .add("firstname",   first_name.getText().toString())
                            .add("middlename",  middle_name.getText().toString())
                            .add("lastname",    last_name.getText().toString())
                            .add("email",       email.getText().toString())
                            .add("dob",         mDateVal)
                            .add("gender",      mGender)
                            .add("cell",        mobile.getText().toString())
                            .add("telephone1",  telephone1.getText().toString())
                            .add("telephone2",  telephone2.getText().toString())
                            .add("address1",    address1.getText().toString())
                            .add("address2",    address2.getText().toString())
                            .add("address3",    address3.getText().toString())
                            .add("occupation_key", String.valueOf(jobList.get((int) occupation.getSelectedItemId()).getKey()))
                            .build();
                }

                @Override
                protected String doInBackground(Void... params) {
                    request = new Request.Builder()
                            .url(Common.API_SERVER + "mobile_profile_update.php")
                            .post(requestBody)
                            .build();
                    try {
                        return client.newCall(request).execute().body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String data) {
                    super.onPostExecute(data);
                    switch(data){
                        case "_NO_INPUT_" :
                            Common.closeDialog(mActivity);
                            AlertBox.Show(mActivity,getString(R.string.sys_error),AlertBox.Type.ERROR);
                        break;
                        case "_SYS_ERROR_" :
                            Common.closeDialog(mActivity);
                            AlertBox.Show(mActivity,getString(R.string.sys_error),AlertBox.Type.ERROR);
                        break;
                        case "_FAIL_" :
                            Common.closeDialog(mActivity);
                            AlertBox.Show(mActivity,getString(R.string.sys_error),AlertBox.Type.ERROR);
                        break;
                        case "_PASS_" :
                            SharedPreferences sharedPref = mContext.getSharedPreferences(Common.PREFERENCE_LOGIN,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.SESSION_NAME),
                                    first_name.getText().toString() + " " + last_name.getText().toString());
                            editor.putString(getString(R.string.SESSION_EMAIL), email.getText().toString());
                            editor.apply();
                            Common.playConfirmation(mActivity);
                        break;
                        default:
                            Common.closeDialog(mActivity);
                            AlertBox.Show(mActivity,getString(R.string.unknown_error),AlertBox.Type.ERROR);
                        break;
                    }
                }
            };

            if(Utility.isConnected(mContext)) {
                task.execute();
            }
        }

        /**
         * Create a chooser intent to select the source to get image from.<br/>
         * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
         * All possible sources are added to the intent chooser.
         */
        public Intent getPickImageChooserIntent() {
            // Determine Uri of camera image to save.
            Uri outputFileUri = getCaptureImageOutputUri();

            List<Intent> allIntents = new ArrayList<>();
            PackageManager packageManager = mActivity.getPackageManager();

            // collect all camera intents
            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                if (outputFileUri != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                }
                allIntents.add(intent);
            }

            // collect all gallery intents
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
            for (ResolveInfo res : listGallery) {
                Intent intent = new Intent(galleryIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(res.activityInfo.packageName);
                allIntents.add(intent);
            }

            // the main intent is the last in the list
            Intent mainIntent = allIntents.get(allIntents.size() - 1);
            for (Intent intent : allIntents) {
                if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                    mainIntent = intent;
                    break;
                }
            }
            allIntents.remove(mainIntent);
            // Create a chooser from the main intent
            Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
            // Add all other intents
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

            return chooserIntent;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(mActivity, CropProfilePhotoActivity.class);
                intent.setData(getPickImageResultUri(data));
                startActivity(intent);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_STORAGE:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                break;
                case MY_PERMISSIONS_REQUEST_CAMERA:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startActivityForResult(getPickImageChooserIntent(), REQUEST_CODE);
                    }
                break;
            }
        }

        /**
         * Get URI to image received from capture by camera.
         */
        private Uri getCaptureImageOutputUri() {
            Uri outputFileUri = null;
            File getImage = mActivity.getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
            return outputFileUri;
        }

        /**
         * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
         * Will return the correct URI for camera and gallery image.
         *
         * @param data the returned data of the activity result
         */
        public Uri getPickImageResultUri(Intent data) {
            boolean isCamera = true;
            if (data != null && data.getData() != null) {
                String action = data.getAction();
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
            return isCamera ? getCaptureImageOutputUri() : data.getData();
        }
    }
}
