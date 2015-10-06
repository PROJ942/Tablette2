package com.app.proj942_ns_mb;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    public Button       buttonTakePhoto;
    static final int    CAMERA_PIC_REQUEST = 001;
    private ImageView   mImageView;
    private String      mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.buttonTakePhoto  = (Button)this.findViewById(R.id.button_TakePicture);
        this.mImageView = (ImageView)this.findViewById(R.id.imageView_Picture);

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Vous allez prendre une photo !", Toast.LENGTH_LONG).show();
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){

            File photoFile          = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

            if(photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CAMERA_PIC_REQUEST){
            ImageView mImageView = (ImageView) findViewById(R.id.imageView_Picture);
            Bitmap bMap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mImageView.setImageBitmap(bMap);
        }
    }

    private File createImageFile() throws IOException{
        //Create an image file name

        String timeStamp        = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName    = "JPEG_" + timeStamp ;
        File storageDir         = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.d("DEBUG", storageDir.toString());
        File image              = new File(storageDir, imageFileName+".jpg");
        Log.d("Debug",image.toString());


        //Save a file
        mCurrentPhotoPath       = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}