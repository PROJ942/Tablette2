package com.app.proj942_ns_mb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    public Button       buttonTakePhoto;

    private TextView    textView_Result;
    private TextView    textView_Path;

    static final int    CAMERA_PIC_REQUEST = 001;
    private ImageView   mImageView;
    private String      mCurrentPhotoPath;

    private int stageWidth, stageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textView_Path          = (TextView) this.findViewById(R.id.textView_Path);
        this.textView_Result        = (TextView)this.findViewById(R.id.textView_Result);

        this.buttonTakePhoto        = (Button)this.findViewById(R.id.button_TakePicture);
        this.mImageView             = (ImageView)this.findViewById(R.id.imageView_Picture);

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Vous allez prendre une photo !", Toast.LENGTH_LONG).show();
                dispatchTakePictureIntent();
            }
        });
    }

    //Save the picture's path in order to reload the picture when the screen rotates
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Store the picture's path
        CharSequence picturePath        = textView_Path.getText();
        outState.putCharSequence("savedPicturePath", picturePath);
    }

    //Recover the picture's path and reload it
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        CharSequence picturePath        = savedInstanceState.getCharSequence("savedPicturePath");
        textView_Path.setText(picturePath);

        //Recover the picture's path and load the picture
        mCurrentPhotoPath           = String.valueOf(textView_Path.getText());
        Bitmap bMap                 = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mImageView.setImageBitmap(bMap);
        mImageView.setRotation(90);
    }

    //Method that take a picture
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
            ImageView mImageView    = (ImageView) findViewById(R.id.imageView_Picture);
            Bitmap bMap             = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mImageView.setImageBitmap(bMap);
            mImageView.setRotation(90);
        }
    }

    //Method that save the picture
    private File createImageFile() throws IOException{

        //Create an image file name
        String timeStamp            = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName        = "JPEG_" + timeStamp ;

        //Define the storage location
        File storageDir             = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.d("DEBUG", storageDir.toString());
        File image                  = new File(storageDir, imageFileName+".jpg");
        Log.d("Debug",image.toString());

        //Save the file
        mCurrentPhotoPath       = image.getAbsolutePath();
        textView_Path.setText(mCurrentPhotoPath);

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