package com.app.proj942_ns_mb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    public Button       buttonTakePhoto;
    public Button       buttonUploadPhoto;

    private TextView    textView_Result;
    private TextView    textView_Path;

    static final int    CAMERA_PIC_REQUEST = 001;
    private ImageView   mImageView;
    private String      mCurrentPhotoPath;
    private String      im_64;

    // fixed URL for tests on XAMPP server
    public static String URL = "http://192.168.164.1/uploads/server.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.textView_Path          = (TextView) this.findViewById(R.id.textView_Path);
        this.textView_Result        = (TextView)this.findViewById(R.id.textView_Result);

        this.buttonTakePhoto        = (Button)this.findViewById(R.id.button_TakePicture);
        this.buttonUploadPhoto      = (Button)this.findViewById(R.id.buttonUploadPhoto);
        this.mImageView             = (ImageView)this.findViewById(R.id.imageView_Picture);

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Vous allez prendre une photo !", Toast.LENGTH_LONG).show();
                dispatchTakePictureIntent();
            }
        });

        buttonUploadPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                upload();
            }
        });
    }

    //Preparing image before uploading to server
    private void upload() {
        //Image Location URL
        Log.e("path","------------" + mCurrentPhotoPath);

        //Converting image to base64
        Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,90,bao);
        byte[] ba = bao.toByteArray();
        im_64 = Base64.encodeBytes(ba);

        Log.e("base64", "----" + im_64);

        //Upload image to server
        new uploadToServer().execute();

    }

    //Uploading Image to Server Using HTTPClient
    public class uploadToServer extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Image Uploading !");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("base64", im_64));
            nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.hide();
            dialog.dismiss();
        }
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