package com.trafalgartmc.guinep;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.trafalgartmc.guinep.Utility.Common;
import com.trafalgartmc.guinep.Utility.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.util.Base64.encodeToString;

/**
 * Created by Rohan
 * Date 7/4/2017.
 */

public class CropProfilePhotoActivity extends AppCompatActivity {
    private CropImageView mCropImageView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image_activity);

        mCropImageView = (CropImageView)  findViewById(R.id.CropImageView);
        mCropImageView.setFixedAspectRatio(true);
        mCropImageView.setCropShape(CropImageView.CropShape.OVAL);

        mCropImageView.setImageUriAsync(getIntent().getData());
        mContext = getBaseContext();
    }

    public void onCropImageClick(View view) {
        Common.showLoading(CropProfilePhotoActivity.this, getString(R.string.uploading));
        Bitmap cropped = mCropImageView.getCroppedImage(400, 400);
        if (cropped != null) {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            cropped.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);

            int id = (Common.getSession(mContext).getInt(getString(R.string.SESSION_ID),0)!=0)?
                        Common.getSession(mContext).getInt(getString(R.string.SESSION_ID),0): 0;

            uploadImage(encodeToString(byteArray.toByteArray(), Base64.NO_WRAP), id);
        }else{
            //Almost should never happen
            Toast.makeText(mContext,getString(R.string.image_crop_error),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mContext, UpdateProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void onCancelClick(View view) {
        finish();
    }

    private void uploadImage(String img, final int userId) {
        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("image", img)
                .add("user_id", String.valueOf(userId))
                .build();

        final Request request = new Request.Builder()
                .url(Common.API_SERVER + "upload_profile_image.php")
                .post(requestBody)
                .build();

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    File myDir = getFilesDir(); //get your internal directory
                    File myFile = new File(myDir, userId + ".jpg");
                    if(myFile.exists()){ myFile.delete(); }
                    return client.newCall(request).execute().body().string();
                } catch (IOException e) {
                    Log.e(Common.LOG_TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String body) {
                super.onPostExecute(body);
                switch (body){
                    case "_NO_INPUT_" :
                        Toast.makeText(mContext,getString(R.string.network_error),Toast.LENGTH_LONG).show();
                    break;
                    case "_UPLOAD_ERROR_" :
                        Toast.makeText(mContext,getString(R.string.image_upload_error),Toast.LENGTH_LONG).show();
                    break;
                    case "_UPLOADED_" :
                    break;
                    default:
                        Toast.makeText(mContext,getString(R.string.image_upload_error),Toast.LENGTH_LONG).show();
                    break;
                }
                Intent intent = new Intent(mContext, UpdateProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };

        if(Utility.isConnected(this)) {
            task.execute();
        }
    }
}
