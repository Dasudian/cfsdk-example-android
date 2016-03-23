package com.dasudian.cloudfiledemo;

import java.io.File;
import java.io.IOException;

import com.dasudian.cloudfile.DsdCloudFileListener;
import com.dasudian.cloudfile.DsdLibCloudFile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	public static final int CROP_PHOTO = 1;
	private String TAG = "MainActivity";
	private String aucServer = "https://192.168.1.248:8443/auc_app";
	private String version = "1.0";
	private String appId = "1635_A_93fHW6VMmE0wzjUSzA";
	private String appKey = "80ee2b2f2687b501";
	private String userId = "123";
	private String clientId = "123";
	private Button upload;
	private Button chosePicture;
	private ImageView picture;
	private Uri imageUri;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    int ret = DsdLibCloudFile.dsdCfInit(aucServer, version, appId, appKey, userId, clientId);
	    Log.e(TAG, "ret="+ret);
	    picture = (ImageView)findViewById(R.id.picture);
	    
	    upload = (Button)findViewById(R.id.upload);
	    upload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	    
	    chosePicture = (Button)findViewById(R.id.choose_from_album);
	    chosePicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				File outputImage = new File(Environment.getExternalStorageDirectory(),
						"output_image.jpg");
				try {
					if (outputImage.exists()) {
						outputImage.delete();
					}
					outputImage.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				imageUri = Uri.fromFile(outputImage);
				Intent intent = new Intent("android.intent.action.GET_CONTENT");
				intent.setType("image/*");
				startActivityForResult(intent, CROP_PHOTO);
			}
		});  
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		 
        if (requestCode == CROP_PHOTO && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
 
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
 
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
 
            picture.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            
            
            Log.d(TAG, "picturePath = "+ picturePath);
            
            DsdLibCloudFile.dsdCfUpload(picturePath, "java_upload", new DsdCloudFileListener() {
					
					@Override
					public void onSuccess(String url) {
						Log.e(TAG, "url = " + url);
					}
					
					@Override
					public void onFailed(String error) {
						// TODO Auto-generated method stub
						
					}
			});
			
        }
	}
}
